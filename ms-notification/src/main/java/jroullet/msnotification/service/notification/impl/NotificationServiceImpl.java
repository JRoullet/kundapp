package jroullet.msnotification.service.notification.impl;

import jroullet.msnotification.document.Notification;
import jroullet.msnotification.dto.request.BulkNotificationEventRequest;
import jroullet.msnotification.dto.request.NotificationEventRequest;
import jroullet.msnotification.dto.response.BulkNotificationEventResponse;
import jroullet.msnotification.dto.response.NotificationEventResponse;
import jroullet.msnotification.enums.NotificationEventType;
import jroullet.msnotification.enums.NotificationStatus;
import jroullet.msnotification.exception.NotificationNotFoundException;
import jroullet.msnotification.exception.NotificationProcessingException;
import jroullet.msnotification.mapper.NotificationMapper;
import jroullet.msnotification.repository.NotificationRepository;
import jroullet.msnotification.service.email.EmailUtilityService;
import jroullet.msnotification.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailUtilityService emailUtilityService;

    // Single Notification Event Processing
    @Override
    @Transactional
    public NotificationEventResponse processNotificationEvent(NotificationEventRequest request) {
        log.info("Processing notification event - Type: {}, User: {}, Session: {}",
                request.eventType(),
                request.user().email(),
                request.session().id()
        );
        try {
            // 1. Convert DTO to Entity
            Notification notification = notificationMapper.toEntity(request);

            // 2. Save (status is PENDING)
            notification = notificationRepository.save(notification);
            log.debug("Notification saved with ID: {}", notification.getId());

            // 3. Send email with retry logic
            boolean emailSent = emailUtilityService.sendEmailWithRetry(notification);

            // 4. Update notification status to SENT or FAILED
            notification.setStatus(emailSent ? NotificationStatus.SENT : NotificationStatus.FAILED);
            if (emailSent) {
                notification.setSentAt(LocalDateTime.now());
            }
            notification = notificationRepository.save(notification);

            // 5. Return response
            NotificationEventResponse response = notificationMapper.toResponseDto(notification);

            log.info("Notification processed successfully - ID: {}, Status: {}",
                    notification.getId(), notification.getStatus());

            return response;

        } catch (Exception e) {
            log.error("Failed to process notification event: {}", e.getMessage(), e);
            throw new NotificationProcessingException("Failed to process notification event");
        }
    }

    //Multiple Notification Event Processing
    @Override
    @Transactional
    public BulkNotificationEventResponse processBulkNotificationEvent(BulkNotificationEventRequest bulkRequest) {
        log.info("📬 Processing bulk notification event - Type: {}, recipients(): {}, Session: {}",
                bulkRequest.eventType(),
                bulkRequest.recipients().size(),
                bulkRequest.session().id()
        );

        List<NotificationEventResponse> responses = new ArrayList<>();
        try {
            // Process each recipient individually
            bulkRequest.recipients().forEach(recipient -> {
                try {
                    // Convert bulk request to individual request
                    NotificationEventRequest individualRequest = notificationMapper.toSingleBulkEventDto(bulkRequest, recipient);

                    // Process individual notification
                    NotificationEventResponse response = processNotificationEvent(individualRequest);
                    responses.add(response);

                } catch (Exception e) {
                    log.error("Failed to process notification for recipient: {}", recipient.email(), e);

                    // Create failed response for this recipient
                    NotificationEventResponse failedResponse = new NotificationEventResponse(
                            null,
                            bulkRequest.eventType(),
                            bulkRequest.session().id(),
                            recipient.email(),
                            NotificationStatus.FAILED,
                            LocalDateTime.now(),
                            null
                            );
                    responses.add(failedResponse);
                }
            });
            BulkNotificationEventResponse bulkResponse = notificationMapper.toBulkResponse(bulkRequest, responses);
            log.info("Bulk notification processed successfully - Total recipients(): {}, Successful: {}, Failed: {}",
                    bulkRequest.recipients().size(),
                    bulkResponse.successfulNotifications(),
                    bulkResponse.failedNotifications());

            return bulkResponse;

        } catch (Exception e) {
            log.error("Failed to process bulk notification event: {}", e.getMessage(), e);
            throw new NotificationProcessingException("Failed to process bulk notification event");
        }

    }


    @Override
    public List<NotificationEventResponse> getNotificationHistory (Long sessionId){
        log.debug("Getting notification history for session: {}", sessionId);

        List<Notification> notifications = notificationRepository.findBySessionId(sessionId);
        return notificationMapper.toResponseDtos(notifications);
    }

    @Override
    public List<NotificationEventResponse> getFailedNotifications () {
        log.debug("Getting failed notifications for retry processing");

        List<Notification> failedNotifications = notificationRepository.findByStatus(NotificationStatus.FAILED);
        return notificationMapper.toResponseDtos(failedNotifications);
    }

    @Override
    @Transactional
    public NotificationEventResponse retryNotification (String notificationId){
        log.info("Retrying notification: {}", notificationId);

        // Find notification by ID
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isEmpty()) {
            throw new NotificationNotFoundException("Notification not found: " + notificationId);
        }

        // Make local variable for notification if exists
        Notification notification = optionalNotification.get();

        // Ensure notification is in FAILED state
        if (notification.getStatus() != NotificationStatus.FAILED) {
            log.warn("⚠️ Cannot retry notification {} - Status: {}", notificationId, notification.getStatus());
            return notificationMapper.toResponseDto(notification);
        }

        // Reset status and retry sending
        notification.setStatus(NotificationStatus.PENDING);
        boolean emailSent = emailUtilityService.sendEmailWithRetry(notification);

        // Update status based on result
        notification.setStatus(emailSent ? NotificationStatus.SENT : NotificationStatus.FAILED);
        if (emailSent) {
            notification.setSentAt(LocalDateTime.now());
        }

        notification = notificationRepository.save(notification);

        log.info("Notification retry completed - ID: {}, Status: {}",
                notification.getId(), notification.getStatus());

        return notificationMapper.toResponseDto(notification);
    }

    @Override
    @Transactional
    public BulkNotificationEventResponse retryAllFailedNotifications () {
        List<Notification> failedNotifications = notificationRepository.findByStatus(NotificationStatus.FAILED);
        List<NotificationEventResponse> responses = new ArrayList<>();

        log.info("Retrying all failed notifications - Total: {}", failedNotifications.size());

        //retrying each failed notification
        failedNotifications.forEach(notification -> {
            try {
                NotificationEventResponse failedResponse = retryNotification(notification.getId());
                responses.add(failedResponse);
            } catch (Exception e) {
                log.error("Failed to retry notification: {}", notification.getId(), e);
            }
        });

        // making 2 responses categories: successful and failed
            long successfulRetries = responses.stream()
                    .filter(r -> r.status() == NotificationStatus.SENT)
                    .count();
            long failedRetries = responses.size() - successfulRetries;

            // Building bulk response with retry results to return
            BulkNotificationEventResponse bulkResponse = new BulkNotificationEventResponse(
                    null,
                    NotificationEventType.SESSION_CANCELLED,
                    responses.size(),
                    (int) successfulRetries,
                    (int) failedRetries,
                    responses,
                    LocalDateTime.now()
            );

            log.info("Bulk retry completed - Total: {}, Successful: {}, Failed: {}",
                    responses.size(), successfulRetries, failedRetries);

            return bulkResponse;
    }

    @Override
    public List<NotificationEventResponse> getEnrollmentNotifications (Long sessionId){
        log.debug("Getting enrollment notifications for session: {}", sessionId);

        List<Notification> notifications = notificationRepository.findBySessionIdAndEventType(
                sessionId, NotificationEventType.USER_ENROLLED);
        return notificationMapper.toResponseDtos(notifications);
    }


}
