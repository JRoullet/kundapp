package jroullet.mswebapp.dto.session.credits;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SessionRollbackRefundRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Session ID is required")
        Long sessionId,

        @Min(value = 1, message = "Credits to refund must be at least 1")
        @NotNull(message = "Credits to refund is mandatory")
        Integer creditsToRefund,

        @NotNull(message = "Internal secret is required")
        String internalSecret
) {}
