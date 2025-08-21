package jroullet.msnotification.dto.response;

import jroullet.msnotification.enums.NotificationEventType;

import java.time.LocalDateTime;
import java.util.List;

public record BulkNotificationEventResponse(
        Long sessionId,
        NotificationEventType eventType,
        Integer totalRecipients,
        Integer successfulNotifications,
        Integer failedNotifications,
        List<NotificationEventResponse> notifications,
        LocalDateTime processedAt
) {}
