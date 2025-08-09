package jroullet.mswebapp.exception;

import lombok.Getter;

@Getter
public class SessionNotAvailableException extends RuntimeException {
    private final Long sessionId;
    private final Integer currentParticipants;
    private final Integer maxCapacity;

    public SessionNotAvailableException(Long sessionId, Integer currentParticipants, Integer maxCapacity) {
        super(String.format("Session %d is not available. Current: %d/%d participants",
                sessionId, currentParticipants, maxCapacity));
        this.sessionId = sessionId;
        this.currentParticipants = currentParticipants;
        this.maxCapacity = maxCapacity;
    }
}
