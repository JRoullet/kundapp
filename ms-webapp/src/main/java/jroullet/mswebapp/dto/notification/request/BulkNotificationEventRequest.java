package jroullet.mswebapp.dto.notification.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jroullet.mswebapp.dto.notification.NotificationSessionDto;
import jroullet.mswebapp.dto.notification.NotificationUserDto;
import jroullet.mswebapp.enums.NotificationEventType;


import java.util.List;

public record BulkNotificationEventRequest(
        @NotNull(message = "Event type is required")
        NotificationEventType eventType,

        @NotNull(message = "Session information is required")
        @Valid
        NotificationSessionDto session,

        @NotEmpty(message = "Recipients list cannot be empty")
        @Valid
        List<NotificationUserDto> recipients,

        @NotNull(message = "Internal secret is required")
        String internalSecret
) {}
