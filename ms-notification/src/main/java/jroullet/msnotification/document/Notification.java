package jroullet.msnotification.document;

import jroullet.msnotification.enums.NotificationEventType;
import jroullet.msnotification.enums.NotificationStatus;
import jroullet.msnotification.document.notificationSubObjects.NotificationRecipient;
import jroullet.msnotification.document.notificationSubObjects.NotificationSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;

    /**
     * Session ID - indexed for fast queries when cancelling sessions
     * Most queries will be: "find all notifications for session X"
     */
    @Indexed
    private Long sessionId;

    /**
     * Type of event that triggered this notification
     */
    private NotificationEventType eventType;

    /**
     * Email recipient information
     */
    private NotificationRecipient recipient;

    /**
     * Session details at the time of notification
     * Stored as snapshot in case session data changes later
     */
    private NotificationSession session;

    /**
     * Current delivery status
     */
    private NotificationStatus status;

    /**
     * When the notification was created
     */
    private LocalDateTime createdAt;

    /**
     * When the email was actually sent (null if failed)
     */
    private LocalDateTime sentAt;

    /**
     * Error message if delivery failed
     */
    private String errorMessage;

    /**
     * Email template used for this notification
     */
    private String templateName;

    /**
     * Email subject line that was sent
     */
    private String emailSubject;

    /**
     * Additional participants info for teacher notifications
     * Contains student info when teacher is notified of enrollment/cancellation
     */
    private List<NotificationRecipient> additionalParticipants;

}
