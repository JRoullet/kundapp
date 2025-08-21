package jroullet.mswebapp.dto.notification.response;



import jroullet.mswebapp.enums.NotificationEventType;
import jroullet.mswebapp.enums.NotificationStatus;

import java.time.LocalDateTime;

/**
 * RESPONSE DTO: notification status sent back to ms-webapp
 */
public record NotificationEventResponse(
        String notificationId,
        NotificationEventType eventType,
        Long sessionId,
        String recipientEmail,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime sentAt
) {}
