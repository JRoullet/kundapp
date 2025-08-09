package jroullet.mswebapp.exception;

import lombok.Getter;

@Getter
public class CreditRollbackFailedException extends RuntimeException {
    private final Long userId;
    private final Long sessionId;
    private final Integer creditsAmount;

    public CreditRollbackFailedException(Long userId, Long sessionId, Integer creditsAmount, Throwable cause) {
        super(String.format("CRITICAL: Failed to rollback %d credits for user %d and session %d. Manual intervention required!",
                creditsAmount, userId, sessionId), cause);
        this.userId = userId;
        this.sessionId = sessionId;
        this.creditsAmount = creditsAmount;
    }
}
