package jroullet.mswebapp.exception;

import lombok.Getter;

@Getter
public class SessionNotFoundException extends RuntimeException {
  private final Long sessionId;

  public SessionNotFoundException(Long sessionId) {
    super(String.format("Session not found with ID: %d", sessionId));
    this.sessionId = sessionId;
  }
}
