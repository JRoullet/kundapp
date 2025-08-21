package jroullet.msnotification.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for ms-notification
 * Centralizes error handling and maps exceptions to appropriate HTTP status codes
 *
 * HTTP Status Code Strategy:
 * - 400: Validation errors (invalid request data)
 * - 401: Authentication errors (invalid internal secret)
 * - 404: Resource not found (notification not found)
 * - 422: Business logic errors (notification processing issues)
 * - 500: Unexpected system errors
 */
@RestControllerAdvice
@Slf4j
public class NotificationExceptionHandler {


    /**
     * Standard error response structure
     */
    public record ErrorResponse(
            String errorCode,
            String message,
            LocalDateTime timestamp
    ) {}

    /**
     * Validation error response with field details
     */
    public record ValidationErrorResponse(
            String errorCode,
            String message,
            Map<String, String> fieldErrors,
            LocalDateTime timestamp
    ) {}

    /**
     * Handle security validation errors
     * Triggered when internal secret validation fails
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e) {
        log.warn("Security validation failed: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "UNAUTHORIZED",
                "Invalid authentication credentials",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle notification not found errors
     * Triggered when trying to access non-existent notifications
     */
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotificationNotFound(NotificationNotFoundException e) {
        log.warn("Notification not found: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "NOTIFICATION_NOT_FOUND",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle notification processing business logic errors
     * Triggered when notification processing fails due to business rules
     */
    @ExceptionHandler(NotificationProcessingException.class)
    public ResponseEntity<ErrorResponse> handleNotificationProcessing(NotificationProcessingException e) {
        log.error("Notification processing failed: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "NOTIFICATION_PROCESSING_ERROR",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Handle email sending errors
     * Triggered when email delivery fails
     */
    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponse> handleEmailSending(EmailSendingException e) {
        log.error("Email sending failed: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "EMAIL_SENDING_ERROR",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Handle validation errors from request DTOs
     * Triggered when @Valid annotation fails on request objects
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.warn("Validation error occurred: {}", e.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "VALIDATION_ERROR",
                "Request validation failed",
                fieldErrors,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle illegal argument exceptions
     * Triggered when invalid parameters are provided
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Invalid argument provided: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_ARGUMENT",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle unexpected system errors
     * Fallback for any unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);

        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}