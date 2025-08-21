package jroullet.mswebapp.service;

import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.clients.NotificationFeignClient;
import jroullet.mswebapp.dto.notification.NotificationSessionDto;
import jroullet.mswebapp.dto.notification.NotificationUserDto;
import jroullet.mswebapp.dto.notification.request.BulkNotificationEventRequest;
import jroullet.mswebapp.dto.notification.request.NotificationEventRequest;
import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import jroullet.mswebapp.enums.NotificationEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationFeignClient notificationFeignClient;
    private final IdentityFeignClient identityFeignClient;

    @Value("${app.internal.secret}")
    private String internalSecret;

    /**
     * Send user enrollment notifications (user + teacher)
     */
    public void sendUserEnrolledNotifications(Long userId, SessionWithParticipantsDTO session) {
        try {
            UserParticipantDTO user = identityFeignClient.getUserBasicInfo(userId);
            UserParticipantDTO teacher = identityFeignClient.getUserBasicInfo(session.getTeacherId());

            // Notify user
            NotificationEventRequest userRequest = new NotificationEventRequest(
                    NotificationEventType.USER_ENROLLED,
                    buildNotificationUserDto(user),
                    buildNotificationSessionDto(session),
                    null,
                    internalSecret
            );
            notificationFeignClient.processNotificationEvent(userRequest);

            // Notify teacher
            NotificationEventRequest teacherRequest = new NotificationEventRequest(
                    NotificationEventType.USER_ENROLLED,
                    buildNotificationUserDto(teacher),
                    buildNotificationSessionDto(session),
                    List.of(buildNotificationUserDto(user)),
                    internalSecret
            );
            notificationFeignClient.processNotificationEvent(teacherRequest);

        } catch (Exception e) {
            log.warn("Failed to send enrollment notifications: {}", e.getMessage());
        }
    }

    /**
     * Send user cancellation notifications (user + teacher)
     */
    public void sendUserCancelledNotifications(Long userId, SessionWithParticipantsDTO session) {
        try {
            UserParticipantDTO user = identityFeignClient.getUserBasicInfo(userId);
            UserParticipantDTO teacher = identityFeignClient.getUserBasicInfo(session.getTeacherId());

            // Notify user
            NotificationEventRequest userRequest = new NotificationEventRequest(
                    NotificationEventType.USER_CANCELLED,
                    buildNotificationUserDto(user),
                    buildNotificationSessionDto(session),
                    null,
                    internalSecret
            );
            notificationFeignClient.processNotificationEvent(userRequest);

            // Notify teacher
            NotificationEventRequest teacherRequest = new NotificationEventRequest(
                    NotificationEventType.USER_CANCELLED,
                    buildNotificationUserDto(teacher),
                    buildNotificationSessionDto(session),
                    List.of(buildNotificationUserDto(user)),
                    internalSecret
            );
            notificationFeignClient.processNotificationEvent(teacherRequest);

        } catch (Exception e) {
            log.warn("Failed to send cancellation notifications: {}", e.getMessage());
        }
    }

    /**
     * Send session cancellation to all participants
     */
    public void sendSessionCancelledNotifications(SessionWithParticipantsDTO session) {
        try {
            if (session.getParticipantIds() == null || session.getParticipantIds().isEmpty()) {
                return;
            }

            List<UserParticipantDTO> participants = identityFeignClient.getUsersBasicInfo(session.getParticipantIds());
            List<NotificationUserDto> participantDtos = participants.stream()
                    .map(this::buildNotificationUserDto)
                    .toList();

            BulkNotificationEventRequest request = new BulkNotificationEventRequest(
                    NotificationEventType.SESSION_CANCELLED,
                    buildNotificationSessionDto(session),
                    participantDtos,
                    internalSecret
            );

            notificationFeignClient.processBulkNotificationEvent(request);

        } catch (Exception e) {
            log.warn("Failed to send session cancellation notifications: {}", e.getMessage());
        }
    }

    /**
     * Send session modification to all participants
     */
    public void sendSessionModifiedNotifications(SessionWithParticipantsDTO session) {
        try {
            if (session.getParticipantIds() == null || session.getParticipantIds().isEmpty()) {
                return;
            }

            List<UserParticipantDTO> participants = identityFeignClient.getUsersBasicInfo(session.getParticipantIds());
            List<NotificationUserDto> participantDtos = participants.stream()
                    .map(this::buildNotificationUserDto)
                    .toList();

            BulkNotificationEventRequest request = new BulkNotificationEventRequest(
                    NotificationEventType.SESSION_MODIFIED,
                    buildNotificationSessionDto(session),
                    participantDtos,
                    internalSecret
            );

            notificationFeignClient.processBulkNotificationEvent(request);

        } catch (Exception e) {
            log.warn("Failed to send session modification notifications: {}", e.getMessage());
        }
    }

    /**
     * Send session creation notification to teacher
     */
    public void sendSessionCreatedNotification(Long teacherId, SessionWithParticipantsDTO session) {
        try {
            UserParticipantDTO teacher = identityFeignClient.getUserBasicInfo(teacherId);

            NotificationEventRequest request = new NotificationEventRequest(
                    NotificationEventType.SESSION_CREATED,
                    buildNotificationUserDto(teacher),
                    buildNotificationSessionDto(session),
                    null,
                    internalSecret
            );

            notificationFeignClient.processNotificationEvent(request);

        } catch (Exception e) {
            log.warn("Failed to send session creation notification: {}", e.getMessage());
        }
    }

    // Helper methods
    private NotificationUserDto buildNotificationUserDto(UserParticipantDTO user) {
        return new NotificationUserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    private NotificationSessionDto buildNotificationSessionDto(SessionWithParticipantsDTO session) {
        return new NotificationSessionDto(
                session.getId(),
                session.getSubject(),
                session.getDescription(),
                session.getStartDateTime(),
                session.getDurationMinutes(),
                session.getTeacherFirstName(),
                session.getTeacherLastName(),
                session.getIsOnline(),
                session.getRoomName(),
                session.getPostalCode(),
                session.getGoogleMapsLink(),
                session.getBringYourMattress(),
                session.getZoomLink(),
                session.getCreditsRequired()
        );
    }
}
