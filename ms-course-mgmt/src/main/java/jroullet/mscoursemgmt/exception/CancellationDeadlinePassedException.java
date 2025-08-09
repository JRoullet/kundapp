package jroullet.mscoursemgmt.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CancellationDeadlinePassedException extends RuntimeException {
    private final Long sessionId;
    private final LocalDateTime sessionDateTime;
    private final LocalDateTime currentDateTime;

    public CancellationDeadlinePassedException(Long sessionId, LocalDateTime sessionDateTime, LocalDateTime currentDateTime) {
        super(String.format("Registration deadline passed for session %d. Session: %s, Current: %s",
                sessionId, sessionDateTime, currentDateTime));
        this.sessionId = sessionId;
        this.sessionDateTime = sessionDateTime;
        this.currentDateTime = currentDateTime;
    }
}
