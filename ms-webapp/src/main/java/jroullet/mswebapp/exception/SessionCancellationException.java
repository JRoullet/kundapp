package jroullet.mswebapp.exception;

public class SessionCancellationException extends RuntimeException {
    public SessionCancellationException(String message) {
        super(message);
    }

    public SessionCancellationException(String message, Throwable cause) {
        super(message, cause);
    }
}
