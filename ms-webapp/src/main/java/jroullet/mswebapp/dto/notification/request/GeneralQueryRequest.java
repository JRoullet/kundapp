package jroullet.mswebapp.dto.notification.request;


import jakarta.validation.constraints.NotNull;

/**
 * General query request record
 * Used for endpoints that only need security validation
 */
public record GeneralQueryRequest(
        @NotNull(message = "Internal secret is required")
        String internalSecret
) {}
