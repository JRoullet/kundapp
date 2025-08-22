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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
                    NotificationEventType.USER_ENROLLED_TO_USER_NOTIFICATION,
                    buildNotificationUserDto(user),
                    buildNotificationSessionDto(session),
                    null,
                    internalSecret
            );
            notificationFeignClient.processNotificationEvent(userRequest);

            // Notify teacher
            NotificationEventRequest teacherRequest = new NotificationEventRequest(
                    NotificationEventType.USER_ENROLLED_TO_TEACHER_NOTIFICATION,
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
                    NotificationEventType.USER_CANCELLED_TO_USER_NOTIFICATION,
                    buildNotificationUserDto(user),
                    buildNotificationSessionDto(session),
                    null,
                    internalSecret
            );
            notificationFeignClient.processNotificationEvent(userRequest);

            // Notify teacher
            NotificationEventRequest teacherRequest = new NotificationEventRequest(
                    NotificationEventType.USER_CANCELED_TO_TEACHER_NOTIFICATION,
                    buildNotificationUserDto(teacher),
                    buildNotificationSessionDto(session),
                    null,
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
        //Notify participants
        if (session.getParticipantIds() != null && !session.getParticipantIds().isEmpty()) {
            try {
                List<UserParticipantDTO> participants = identityFeignClient.getUsersBasicInfo(session.getParticipantIds());
                List<NotificationUserDto> participantDtos = participants.stream()
                        .map(this::buildNotificationUserDto)
                        .toList();

                // Notify users
                BulkNotificationEventRequest request = new BulkNotificationEventRequest(
                        NotificationEventType.SESSION_CANCELLED_TO_USER_NOTIFICATION,
                        buildNotificationSessionDto(session),
                        participantDtos,
                        internalSecret
                );

                notificationFeignClient.processBulkNotificationEvent(request);
            } catch (Exception e) {
                log.warn("Failed to send session cancellation notifications: {}", e.getMessage());
            }
        }
            // Notify teacher
        try {
        UserParticipantDTO teacher = identityFeignClient.getUserBasicInfo(session.getTeacherId());

        List<NotificationUserDto> participantDtos = Collections.emptyList();
        if (session.getParticipantIds() != null && !session.getParticipantIds().isEmpty()) {
            List<UserParticipantDTO> participants = identityFeignClient.getUsersBasicInfo(session.getParticipantIds());
            participantDtos = participants.stream()
                    .map(this::buildNotificationUserDto)
                    .toList();
        }
        NotificationEventRequest teacherRequest = new NotificationEventRequest(
                NotificationEventType.SESSION_CANCELLED_TO_TEACHER_NOTIFICATION,
                buildNotificationUserDto(teacher),
                buildNotificationSessionDto(session),
                participantDtos,
                internalSecret
        );
        notificationFeignClient.processNotificationEvent(teacherRequest);

        } catch (Exception e) {
            log.warn("Failed to send session cancellation notifications: {}", e.getMessage());
        }
    }

    /**
     * Send session modification to all participants
     */
    public void sendSessionModifiedNotifications(SessionWithParticipantsDTO session, String modificationSummary) {
        // Notify participants
        if (session.getParticipantIds() != null && !session.getParticipantIds().isEmpty()) {
            try {
                List<UserParticipantDTO> participants = identityFeignClient.getUsersBasicInfo(session.getParticipantIds());
                List<NotificationUserDto> participantDtos = participants.stream()
                        .map(this::buildNotificationUserDto)
                        .toList();

                BulkNotificationEventRequest request = new BulkNotificationEventRequest(
                        NotificationEventType.SESSION_MODIFIED_TO_USER_NOTIFICATION,
                        buildNotificationSessionWithSummaryDto(session, modificationSummary),
                        participantDtos,
                        internalSecret
                );
                notificationFeignClient.processBulkNotificationEvent(request);
            } catch (Exception e) {
                log.warn("Failed to send session modification notifications to participants: {}", e.getMessage());
            }
        }

        // Notify teacher (separate try/catch)
        try {
            UserParticipantDTO teacher = identityFeignClient.getUserBasicInfo(session.getTeacherId());

            List<NotificationUserDto> participantDtos = Collections.emptyList();
            if (session.getParticipantIds() != null && !session.getParticipantIds().isEmpty()) {
                List<UserParticipantDTO> participants = identityFeignClient.getUsersBasicInfo(session.getParticipantIds());
                participantDtos = participants.stream()
                        .map(this::buildNotificationUserDto)
                        .toList();
            }

            NotificationEventRequest teacherRequest = new NotificationEventRequest(
                    NotificationEventType.SESSION_MODIFIED_TO_TEACHER_NOTIFICATION,
                    buildNotificationUserDto(teacher),
                    buildNotificationSessionWithSummaryDto(session, modificationSummary),
                    participantDtos,
                    internalSecret
            );
            notificationFeignClient.processNotificationEvent(teacherRequest);
        } catch (Exception e) {
            log.warn("Failed to send session modification notification to teacher: {}", e.getMessage());
        }
    }

    /**
     * Send session creation notification to teacher
     */
    public void sendSessionCreatedNotification(Long teacherId, SessionWithParticipantsDTO session) {
        try {
            UserParticipantDTO teacher = identityFeignClient.getUserBasicInfo(teacherId);

            NotificationEventRequest request = new NotificationEventRequest(
                    NotificationEventType.SESSION_CREATED_TO_TEACHER_NOTIFICATION,
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

    /**
     * Send session completion notifications to all participants
     */
    public void sendSessionCompletedNotifications(SessionWithParticipantsDTO session) {
        try {
            if (session.getParticipantIds() == null || session.getParticipantIds().isEmpty()) {
                return;
            }

            List<UserParticipantDTO> participants = identityFeignClient.getUsersBasicInfo(session.getParticipantIds());
            List<NotificationUserDto> participantDtos = participants.stream()
                    .map(this::buildNotificationUserDto)
                    .toList();

            BulkNotificationEventRequest request = new BulkNotificationEventRequest(
                    NotificationEventType.SESSION_COMPLETED_TO_USER_NOTIFICATION,
                    buildNotificationSessionDto(session),
                    participantDtos,
                    internalSecret
            );

            notificationFeignClient.processBulkNotificationEvent(request);

        } catch (Exception e) {
            log.warn("Failed to send session completion notifications: {}", e.getMessage());
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
                session.getCreditsRequired(),
                null
        );
    }

    private NotificationSessionDto buildNotificationSessionWithSummaryDto(SessionWithParticipantsDTO session, String modificationSummary) {
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
                session.getCreditsRequired(),
                modificationSummary
        );
    }

    public String buildModificationSummary(SessionWithParticipantsDTO original, SessionWithParticipantsDTO updated) {
        List<String> changes = new ArrayList<>();

        if (!Objects.equals(original.getStartDateTime(), updated.getStartDateTime())) {
            changes.add("Date et heure");
        }
        if (!Objects.equals(original.getDescription(), updated.getDescription())) {
            changes.add("Description");
        }
        if (!Objects.equals(original.getIsOnline(), updated.getIsOnline())) {
            changes.add("Mode de session");
        }
        if (!Objects.equals(original.getDurationMinutes(), updated.getDurationMinutes())) {
            changes.add("Durée");
        }

        if (Boolean.TRUE.equals(updated.getIsOnline())) {
            if (!Objects.equals(original.getZoomLink(), updated.getZoomLink())) {
                changes.add("Lien Zoom");
            }
        } else {
            if (!Objects.equals(original.getRoomName(), updated.getRoomName())) {
                changes.add("Lieu de la session");
            }
            if (!Objects.equals(original.getGoogleMapsLink(), updated.getGoogleMapsLink())) {
                changes.add("Localisation GPS");
            }
            if (!Objects.equals(original.getBringYourMattress(), updated.getBringYourMattress())) {
                changes.add("Matériel requis");
            }
        }

        return changes.isEmpty() ? null : String.join(", ", changes);
    }
}
