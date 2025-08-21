package jroullet.msnotification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Notification retry request record
 * Used for retrying specific notifications
 */
public record NotificationRetryRequest(
        @NotBlank(message = "Notification ID is required")
        String notificationId,

        @NotNull(message = "Internal secret is required")
        String internalSecret
) {}
