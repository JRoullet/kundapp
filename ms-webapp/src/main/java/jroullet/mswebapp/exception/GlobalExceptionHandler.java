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

    //Session Validation Parent Local Exception manager
    @ExceptionHandler(SessionValidationException.class)
    public ResponseEntity<ErrorResponse> handleSessionValidation(SessionValidationException ex) {
        log.error("Session validation error: {}", ex.getMessage());

        HttpStatus status;
        if (ex instanceof InsufficientCreditsException) {
            status = HttpStatus.PAYMENT_REQUIRED;  // 402
        } else if (ex instanceof SessionNotAvailableException) {
            status = HttpStatus.CONFLICT;  // 409
        } else if (ex instanceof UserAlreadyRegisteredException) {
            status = HttpStatus.CONFLICT;  // 409
        } else {
            status = HttpStatus.UNPROCESSABLE_ENTITY;  // 422
        }

        ErrorResponse error = new ErrorResponse(ex.getUserMessage(), "VALIDATION_ERROR", status);
        return ResponseEntity.status(status).body(error);
    }

    // code 403 for forbidden access
    @ExceptionHandler(UnauthorizedSessionAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccess(UnauthorizedSessionAccessException ex) {
        log.error("Unauthorized access: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("Unauthorized access to session", "UNAUTHORIZED_ACCESS", HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    //code 500 for critical system errors
    @ExceptionHandler(CreditRollbackFailedException.class)
    public ResponseEntity<ErrorResponse> handleCriticalError(CreditRollbackFailedException ex) {
        log.error("CRITICAL: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("Critical system error", "ROLLBACK_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    //code 404 for not found
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException ex) {
        log.error("Session not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("Session not found", "SESSION_NOT_FOUND", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    //code 400 for bad request
    @ExceptionHandler(SessionRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleSessionRegistration(SessionRegistrationException ex) {
        log.error("Session registration error: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("Registration failed", "REGISTRATION_FAILED", HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    //code 500 for internal server error
    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("Unexpected error", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
