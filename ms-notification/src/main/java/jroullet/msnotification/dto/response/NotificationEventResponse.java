package jroullet.msnotification.dto.response;

import jroullet.msnotification.enums.NotificationEventType;
import jroullet.msnotification.enums.NotificationStatus;
import lombok.Builder;

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
