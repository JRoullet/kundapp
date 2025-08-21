package jroullet.msnotification.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jroullet.msnotification.dto.NotificationSessionDto;
import jroullet.msnotification.dto.NotificationUserDto;
import jroullet.msnotification.enums.NotificationEventType;

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
