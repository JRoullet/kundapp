package jroullet.mswebapp.exception;

public abstract class SessionValidationException extends RuntimeException {
  protected SessionValidationException(String message) {
    super(message);
  }

  public abstract String getUserMessage();
}
