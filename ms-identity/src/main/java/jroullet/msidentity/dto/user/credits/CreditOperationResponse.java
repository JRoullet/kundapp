package jroullet.msidentity.dto.user.credits;

public record CreditOperationResponse(
        Long userId,
        Integer previousCredits,
        Integer newCredits,
        String operation,
        Long sessionId
) {}
