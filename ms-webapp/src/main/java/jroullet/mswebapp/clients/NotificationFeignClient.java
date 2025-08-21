package jroullet.mswebapp.clients;

import jakarta.validation.Valid;
import jroullet.mswebapp.dto.notification.request.*;
import jroullet.mswebapp.dto.notification.response.BulkNotificationEventResponse;
import jroullet.mswebapp.dto.notification.response.NotificationEventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * FeignClient for ms-notification communication
 * Handles all notification operations between ms-webapp and ms-notification
 */
@FeignClient(name = "ms-notification", path = "/api/notifications")
public interface NotificationFeignClient {

    /**
     * Process single notification event
     * USAGE: User enrollment/cancellation
     */
    @PostMapping("/single")
    NotificationEventResponse processNotificationEvent(@Valid @RequestBody NotificationEventRequest request);

    /**
     * Process bulk notification events
     * USAGE: Session cancellation/modification affecting multiple users
     */
    @PostMapping("/bulk")
    BulkNotificationEventResponse processBulkNotificationEvent(@Valid @RequestBody BulkNotificationEventRequest request);

    /**
     * Get notification history for session
     * USAGE: Admin interface to view sent notifications
     */
    @PostMapping("/session/history")
    List<NotificationEventResponse> getNotificationHistory(@Valid @RequestBody SessionQueryRequest request);

    /**
     * Get failed notifications
     * USAGE: Admin monitoring of email delivery issues
     */
    @PostMapping("/failed")
    List<NotificationEventResponse> getFailedNotifications(@Valid @RequestBody GeneralQueryRequest request);

    /**
     * Retry specific failed notification
     * USAGE: Admin manual retry after SMTP issues resolved
     */
    @PostMapping("/retry")
    NotificationEventResponse retryNotification(@Valid @RequestBody NotificationRetryRequest request);

    /**
     * Retry all failed notifications
     * USAGE: Bulk retry after service recovery
     */
    @PostMapping("/retry-all")
    BulkNotificationEventResponse retryAllFailedNotifications(@Valid @RequestBody GeneralQueryRequest request);

    /**
     * Get enrollment notifications for session
     * USAGE: When teacher modifies session, get list of enrolled users
     */
    @PostMapping("/enrollments")
    List<NotificationEventResponse> getEnrollmentNotifications(@Valid @RequestBody SessionQueryRequest request);

}
