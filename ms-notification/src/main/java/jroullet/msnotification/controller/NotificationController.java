package jroullet.msnotification.controller;

import jakarta.validation.Valid;
import jroullet.msnotification.dto.request.*;
import jroullet.msnotification.dto.response.BulkNotificationEventResponse;
import jroullet.msnotification.dto.response.NotificationEventResponse;
import jroullet.msnotification.security.SecurityValidator;
import jroullet.msnotification.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for notification management
 * Handles all notification events between microservices
 *
 * Security: All requests contain internalSecret in body for inter-service communication
 * Used by: ms-webapp to send notifications for session events
 * Error handling: Delegated to NotificationExceptionHandler
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityValidator securityValidator;

    /**
     * Process single notification event
     *
     * USAGE:
     * - USER_ENROLLED: Client registers to session
     * - USER_CANCELLED: Client cancels participation
     */
    @PostMapping("/single")
    public ResponseEntity<NotificationEventResponse> processNotificationEvent(
            @Valid @RequestBody NotificationEventRequest request) {

        securityValidator.validateInternalSecret(request.internalSecret());

        NotificationEventResponse response = notificationService.processNotificationEvent(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Process bulk notification events
     *
     * USAGE:
     * - SESSION_CANCELLED: Teacher/Admin cancels session → notify all participants
     * - SESSION_MODIFIED: Session details changed → notify all participants
     * - SESSION_COMPLETED: Session finished → notify all participants
     */
    @PostMapping("/bulk")
    public ResponseEntity<BulkNotificationEventResponse> processBulkNotificationEvent(
            @Valid @RequestBody BulkNotificationEventRequest bulkRequest) {

        securityValidator.validateInternalSecret(bulkRequest.internalSecret());

        BulkNotificationEventResponse response = notificationService.processBulkNotificationEvent(bulkRequest);

        return ResponseEntity.ok(response);
    }

    /**
     * Get notification history for specific session
     *
     * USAGE:
     * - Admin interface: Review all sent emails for session
     * - Debugging: Verify notifications were sent
     */
    @PostMapping("/session/history")
    public ResponseEntity<List<NotificationEventResponse>> getNotificationHistory(
            @Valid @RequestBody SessionQueryRequest request) {

        securityValidator.validateInternalSecret(request.internalSecret());

        List<NotificationEventResponse> history = notificationService.getNotificationHistory(request.sessionId());

        return ResponseEntity.ok(history);
    }

    /**
     * Get all failed notifications
     *
     * USAGE:
     * - Monitor failed email deliveries
     * - Prepare for bulk retry operations
     */
    @PostMapping("/failed")
    public ResponseEntity<List<NotificationEventResponse>> getFailedNotifications(
            @Valid @RequestBody GeneralQueryRequest request) {

        securityValidator.validateInternalSecret(request.internalSecret());

        List<NotificationEventResponse> failedNotifications = notificationService.getFailedNotifications();

        return ResponseEntity.ok(failedNotifications);
    }

    /**
     * Retry specific failed notification
     *
     * USAGE:
     * - Manual retry by admin after SMTP issues resolved
     * - Targeted retry for specific recipients
     */
    @PostMapping("/retry")
    public ResponseEntity<NotificationEventResponse> retryNotification(
            @Valid @RequestBody NotificationRetryRequest request) {

        securityValidator.validateInternalSecret(request.internalSecret());

        NotificationEventResponse response = notificationService.retryNotification(request.notificationId());

        return ResponseEntity.ok(response);
    }

    /**
     * Retry all failed notifications
     *
     * USAGE:
     * - Bulk retry after SMTP service recovery
     * - Scheduled retry job for failed deliveries
     */
    @PostMapping("/retry-all")
    public ResponseEntity<BulkNotificationEventResponse> retryAllFailedNotifications(
            @Valid @RequestBody GeneralQueryRequest request) {

        securityValidator.validateInternalSecret(request.internalSecret());

        BulkNotificationEventResponse response = notificationService.retryAllFailedNotifications();

        return ResponseEntity.ok(response);
    }

    /**
     * Get enrollment notifications for session
     *
     * USAGE:
     * - Teacher modifies/cancels session → get enrolled users for notification
     * - Send updated session details to participants only
     */
    @PostMapping("/enrollments")
    public ResponseEntity<List<NotificationEventResponse>> getEnrollmentNotifications(
            @Valid @RequestBody SessionQueryRequest request) {

        securityValidator.validateInternalSecret(request.internalSecret());

        List<NotificationEventResponse> enrollments = notificationService.getEnrollmentNotifications(request.sessionId());

        return ResponseEntity.ok(enrollments);
    }

}