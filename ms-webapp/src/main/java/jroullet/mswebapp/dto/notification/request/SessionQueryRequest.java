package jroullet.mswebapp.dto.notification.request;

import jakarta.validation.constraints.NotNull;

/**
 * Session query request record
 * Used for endpoints that need session ID and security validation
 */
public record SessionQueryRequest(
        @NotNull(message = "Session ID is required")
        Long sessionId,

        @NotNull(message = "Internal secret is required")
        String internalSecret
) {}