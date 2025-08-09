package jroullet.mswebapp.dto.session.credits;

public record CreditOperationResponse(
        Long userId,
        Integer previousCredits,
        Integer newCredits,
        String operation,
        Long sessionId
) {}
