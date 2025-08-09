package jroullet.mswebapp.exception;

import lombok.Getter;

@Getter
public class UserAlreadyRegisteredException extends RuntimeException {
  private final Long userId;
  private final Long sessionId;

  public UserAlreadyRegisteredException(Long userId, Long sessionId) {
    super(String.format("User %d is already registered for session %d", userId, sessionId));
    this.userId = userId;
    this.sessionId = sessionId;
  }}
