package jroullet.msnotification.service.notification;

import jroullet.msnotification.dto.request.BulkNotificationEventRequest;
import jroullet.msnotification.dto.request.NotificationEventRequest;
import jroullet.msnotification.dto.response.BulkNotificationEventResponse;
import jroullet.msnotification.dto.response.NotificationEventResponse;

import java.util.List;

public interface NotificationService {

    /**
     * Process a single notification event
     * USAGE :
     * - USER_ENROLLED : Client registers to a session
     * - USER_CANCELLED : Client cancels his participation to a session
     *
     * @param request The notification event from ms-webapp
     * @return Response with notification status and details
     */
    NotificationEventResponse processNotificationEvent(NotificationEventRequest request);

    /**
     * Process bulk notification events
     * USAGE :
     * - SESSION_CANCELLED/MODIFIED/COMPLETED : Teacher cancels/modify/completes a session → notify every participant
     *
     * @param bulkRequest Bulk notification request with multiple recipients
     * @return Bulk response with individual notification statuses
     */
    BulkNotificationEventResponse processBulkNotificationEvent(BulkNotificationEventRequest bulkRequest);

    /**
     * Get notification history for a specific session
     * USAGE :
     * - Admin interface : recaps all sent emails for a session
     * - Debugging : check notifications have been sent
     *
     * @param sessionId The session ID to query
     * @return List of notification responses for the session
     */
    List<NotificationEventResponse> getNotificationHistory(Long sessionId);

    /**
     * Get failed notifications for retry processing
     * USAGE :
     * - To automate retry of failed notifications
     * - Monitoring failed sent notifications
     *
     * @return List of failed notification responses
     */
    List<NotificationEventResponse> getFailedNotifications();

    /**
     * Retry sending a specific notification
     * USAGE :
     * - Manual retry by admin in case of SMTP issues
     * - Automate retry job
     *
     * @param notificationId The ID of the notification to retry
     * @return Updated notification response after retry attempt
     */
    NotificationEventResponse retryNotification(String notificationId);

    /**
     * Retry all failed notifications
     * USAGE :
     * - Retry all notifications that failed to send
     *
     * @return Bulk response with retry results
     */
    BulkNotificationEventResponse retryAllFailedNotifications();

    /**
     * Get enrollment notifications for a session
     * USAGE  :
     * - When teacher modifies or cancels a session, send notifications to all enrolled users
     * - Send back a new session link to all enrolled users
     *
     * @param sessionId The session ID to query
     * @return List of enrollment notifications
     */
    List<NotificationEventResponse> getEnrollmentNotifications(Long sessionId);

}
