package jroullet.mswebapp.dto.session.credits;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SessionRegistrationDeductRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Session ID is required")
        Long sessionId,

        @Min(value = 1, message = "Credits required must be at least 1")
        @NotNull(message = "Credits required is mandatory")
        Integer creditsRequired,

        @NotNull(message = "Internal secret is required")
        String internalSecret
) {}
