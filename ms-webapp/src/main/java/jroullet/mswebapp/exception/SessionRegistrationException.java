package jroullet.mswebapp.exception;

public class SessionRegistrationException extends RuntimeException {
    public SessionRegistrationException(String message) {
        super(message);
    }
    public SessionRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
