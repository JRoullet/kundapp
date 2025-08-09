package jroullet.mswebapp.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public record ErrorResponse(String message, String errorCode, LocalDateTime timestamp, int status) {
        public ErrorResponse(String message, String errorCode, HttpStatus status) {
            this(message, errorCode, LocalDateTime.now(), status.value());
        }
    }
    @ExceptionHandler(InsufficientCreditsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientCredits(InsufficientCreditsException ex) {
        log.error("Insufficient credits: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("Insufficient credits", "INSUFFICIENT_CREDITS", HttpStatus.PAYMENT_REQUIRED);
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(error);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyRegistered(UserAlreadyRegisteredException ex) {
        log.error("User already registered: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("Already registered for this session", "USER_ALREADY_REGISTERED", HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    @ExceptionHandler(CreditRollbackFailedException.class)
    public ResponseEntity<ErrorResponse> handleCriticalError(CreditRollbackFailedException ex) {
        log.error("CRITICAL: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("Critical system error", "ROLLBACK_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException ex) {
        log.error("Session not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("Session not found", "SESSION_NOT_FOUND", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SessionNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotAvailable(SessionNotAvailableException ex) {
        log.error("Session not available: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("Session is full", "SESSION_NOT_AVAILABLE", HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(SessionRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleSessionRegistration(SessionRegistrationException ex) {
        log.error("Session registration error: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("Registration failed", "REGISTRATION_FAILED", HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Feign Client error handling
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        log.error("External service error: HTTP {} - {}", ex.status(), ex.getMessage());

        String message = switch (ex.status()) {
            case 409 -> "Conflict - session full or already registered";
            case 422 -> "Insufficient credits";
            case 403 -> "Cannot cancel within 48h";
            case 404 -> "Resource not found";
            default -> "Service error";
        };

        ErrorResponse error = new ErrorResponse(message, "EXTERNAL_ERROR", HttpStatus.valueOf(ex.status()));
        return ResponseEntity.status(ex.status()).body(error);
    }

    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("Unexpected error", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
