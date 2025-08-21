package jroullet.msnotification.repository;

import jroullet.msnotification.document.Notification;
import jroullet.msnotification.enums.NotificationEventType;
import jroullet.msnotification.enums.NotificationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    /**
     * Find all notifications for a specific session
     * Uses sessionId index for fast queries - primary use case
     *
     * @param sessionId The session ID to search for
     * @return List of notifications for the session
     */
    List<Notification> findBySessionId(Long sessionId);

    /**
     * Find notifications by session ID and event type
     * Useful for finding specific types like ENROLLMENT notifications
     *
     * @param sessionId The session ID
     * @param eventType The type of notification event
     * @return List of matching notifications
     */
    List<Notification> findBySessionIdAndEventType(Long sessionId, NotificationEventType eventType);

    /**
     * Find failed notifications for retry processing
     *
     * @return List of notifications with FAILED status
     */
    List<Notification> findByStatus(NotificationStatus status);

}
