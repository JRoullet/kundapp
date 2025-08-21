package jroullet.mswebapp.dto.notification.response;


import jroullet.mswebapp.enums.NotificationEventType;

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
