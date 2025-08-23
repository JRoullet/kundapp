package jroullet.mswebapp.exception;

import lombok.Getter;

@Getter
public class UserAlreadyRegisteredException extends SessionValidationException {
  private final Long userId;
  private final Long sessionId;

  public UserAlreadyRegisteredException(Long userId, Long sessionId) {
    super(String.format("User %d is already registered for session %d", userId, sessionId));
    this.userId = userId;
    this.sessionId = sessionId;
  }

  @Override
  public String getUserMessage() {
    return "Vous êtes déjà inscrit à cette session";
  }
}
