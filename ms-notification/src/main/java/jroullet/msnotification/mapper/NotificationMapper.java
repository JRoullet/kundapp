package jroullet.msnotification.mapper;

import jroullet.msnotification.document.Notification;
import jroullet.msnotification.document.notificationSubObjects.NotificationRecipient;
import jroullet.msnotification.document.notificationSubObjects.NotificationSession;
import jroullet.msnotification.dto.NotificationSessionDto;
import jroullet.msnotification.dto.NotificationUserDto;
import jroullet.msnotification.dto.request.BulkNotificationEventRequest;
import jroullet.msnotification.dto.request.NotificationEventRequest;
import jroullet.msnotification.dto.response.BulkNotificationEventResponse;
import jroullet.msnotification.dto.response.NotificationEventResponse;
import jroullet.msnotification.enums.NotificationEventType;
import jroullet.msnotification.enums.NotificationStatus;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper
 * Automatically generates implementation at compile time
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface NotificationMapper {

    // ========== Request DTO to Entity Mapping ==========

    /**
     * Convert notification event request to notification entity
     * Used when creating new notifications from incoming events
     */
    @Mapping(target = "id", ignore = true)  // MongoDB auto-generates
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "templateName", source = "eventType", qualifiedByName = "mapEventTypeToTemplate")
    @Mapping(target = "emailSubject", source = "eventType", qualifiedByName = "mapEventTypeToSubject")
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "recipient", source = "user")
    @Mapping(target = "session", source = "session")
    Notification toEntity(NotificationEventRequest eventRequest);

    /**
     * Convert NotificationUserDto to NotificationRecipient
     */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    NotificationRecipient toRecipient(NotificationUserDto userDto);

    /**
     * Convert NotificationSessionDto to NotificationSession
     */
    @Mapping(target = "sessionId", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "subject", source = "subject")
    @Mapping(target = "startDateTime", source = "startDateTime")
    @Mapping(target = "durationMinutes", source = "durationMinutes")
    @Mapping(target = "teacherName", expression = "java(getTeacherFullName(sessionDto))")
    @Mapping(target = "isOnline", source = "isOnline")
    @Mapping(target = "roomName", source = "roomName")
    @Mapping(target = "googleMapsLink", source = "googleMapsLink")
    @Mapping(target = "bringYourMattress", source = "bringYourMattress")
    @Mapping(target = "zoomLink", source = "zoomLink")
    NotificationSession toNotificationSession(NotificationSessionDto sessionDto);

    // ========== Entity to Response DTO Mapping ==========

    /**
     * Convert notification entity to response DTO
     * Used when returning notification status to ms-webapp
     */
    @Mapping(target = "notificationId", source = "id")
    @Mapping(target = "recipientEmail", source = "recipient.email")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "sessionId", source = "sessionId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "sentAt", source = "sentAt")
    NotificationEventResponse toResponseDto(Notification notification);

    /**
     * Convert list of notifications to response DTOs
     */
    List<NotificationEventResponse> toResponseDtos(List<Notification> notifications);

    // ========== Bulk Operations Mapping ==========

    /**
     * Convert bulk request to individual notification events
     * Used when processing session cancellations with multiple recipients
     */
    @Mapping(target = "eventType", source = "bulkRequest.eventType")
    @Mapping(target = "session", source = "bulkRequest.session")
    @Mapping(target = "user", source = "recipient")
    @Mapping(target = "internalSecret", source = "bulkRequest.internalSecret")
    NotificationEventRequest toSingleBulkEventDto(BulkNotificationEventRequest bulkRequest,
                                                  NotificationUserDto recipient);

    /**
     * Convert bulk response with individual results
     */
    @Mapping(target = "sessionId", source = "bulkRequest.session.id")
    @Mapping(target = "eventType", source = "bulkRequest.eventType")
    @Mapping(target = "totalRecipients", expression = "java(bulkRequest.recipients().size())")
    @Mapping(target = "notifications", source = "notifications")
    @Mapping(target = "processedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "successfulNotifications", expression = "java(countSuccessful(notifications))")
    @Mapping(target = "failedNotifications", expression = "java(countFailed(notifications))")
    BulkNotificationEventResponse toBulkResponse(BulkNotificationEventRequest bulkRequest,
                                                 List<NotificationEventResponse> notifications);

    // ========== Helper Methods ==========

    /**
     * Get teacher full name from session DTO
     */
    default String getTeacherFullName(NotificationSessionDto sessionDto) {
        if (sessionDto == null) return "";
        return sessionDto.getTeacherFullName();
    }

    /**
     * Count successful notifications
     */
    default Integer countSuccessful(List<NotificationEventResponse> notifications) {
        if (notifications == null) return 0;
        return (int) notifications.stream()
                .filter(n -> n.status() == NotificationStatus.SENT)
                .count();
    }

    /**
     * Count failed notifications
     */
    default Integer countFailed(List<NotificationEventResponse> notifications) {
        if (notifications == null) return 0;
        return (int) notifications.stream()
                .filter(n -> n.status() == NotificationStatus.FAILED)
                .count();
    }

    /**
     * Map event type to email template name
     */
    @Named("mapEventTypeToTemplate")
    default String mapEventTypeToTemplate(NotificationEventType eventType) {
        return switch (eventType) {
            case USER_ENROLLED -> "enrollment";
            case USER_CANCELLED -> "cancellation";
            case SESSION_CANCELLED -> "session-cancelled";
            case SESSION_MODIFIED -> "session-modified";
            case SESSION_COMPLETED -> "session-completed";
            case SESSION_CREATED -> "session-created";

        };
    }

    /**
     * Map event type to email subject
     */
    @Named("mapEventTypeToSubject")
    default String mapEventTypeToSubject(NotificationEventType eventType) {
        return switch (eventType) {
            case USER_ENROLLED -> "Inscription confirmée";
            case USER_CANCELLED -> "Désinscription confirmée";
            case SESSION_CANCELLED -> "Session annulée";
            case SESSION_MODIFIED -> "Session modifiée";
            case SESSION_COMPLETED -> "Sessions terminées";
            case SESSION_CREATED -> "Nouvelle session créée";
        };
    }

    /**
     * Get full name from notification recipient
     */
    default String getFullName(NotificationRecipient recipient) {
        if (recipient == null) return "";

        String firstName = recipient.getFirstName() != null ? recipient.getFirstName() : "";
        String lastName = recipient.getLastName() != null ? recipient.getLastName() : "";

        return (firstName + " " + lastName).trim();
    }
}
