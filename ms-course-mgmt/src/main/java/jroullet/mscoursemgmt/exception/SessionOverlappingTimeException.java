package jroullet.mscoursemgmt.exception;

public class SessionOverlappingTimeException extends RuntimeException {
    public SessionOverlappingTimeException(String message) {
        super(message);
    }
}
