package jroullet.mscoursemgmt.exception;

import lombok.Getter;

@Getter
public class UserNotRegisteredException extends RuntimeException {
    private final Long userId;
    private final Long sessionId;

    public UserNotRegisteredException(Long userId, Long sessionId) {
        super(String.format("User %d is not registered for session %d", userId, sessionId));
        this.userId = userId;
        this.sessionId = sessionId;
    }
}
