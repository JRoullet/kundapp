package jroullet.mscoursemgmt.exception;

import lombok.Getter;

@Getter
public class SessionFullException extends RuntimeException {
    private final Long sessionId;
    private final Integer currentCount;
    private final Integer maxCapacity;

    public SessionFullException(Long sessionId, Integer currentCount, Integer maxCapacity) {
        super(String.format("Session %d is full. Current: %d/%d participants",
                sessionId, currentCount, maxCapacity));
        this.sessionId = sessionId;
        this.currentCount = currentCount;
        this.maxCapacity = maxCapacity;
    }
}
