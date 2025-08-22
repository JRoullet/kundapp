package jroullet.mscoursemgmt.exception;

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

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public record ErrorResponse(
            String message,
            String errorCode,
            LocalDateTime timestamp,
            int status,
            Map<String, Object> details
    ) {
        public ErrorResponse(String message, String errorCode, HttpStatus status) {
            this(message, errorCode, LocalDateTime.now(), status.value(), new HashMap<>());
        }

        public ErrorResponse(String message, String errorCode, HttpStatus status, Map<String, Object> details) {
            this(message, errorCode, LocalDateTime.now(), status.value(), details);
        }
    }

    // 403 FORBIDDEN
    @ExceptionHandler(UnauthorizedSessionAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedSessionAccessException(UnauthorizedSessionAccessException e) {
        log.error("Unauthorized session access: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Unauthorized access to session",
                "UNAUTHORIZED_ACCESS",
                HttpStatus.FORBIDDEN
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // 409 CONFLICT
    @ExceptionHandler(InvalidSessionStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSessionStateException(InvalidSessionStateException e) {
        log.error("Invalid session state: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Invalid session state",
                "INVALID_SESSION_STATE",
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 409 CONFLICT
    @ExceptionHandler(InvalidSessionUpdateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSessionUpdateException(InvalidSessionUpdateException e) {
        log.error("Invalid session update: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Invalid session update request",
                "INVALID_SESSION_UPDATE",
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 409 CONFLICT
    @ExceptionHandler(InsufficientSpotsException.class)
    public ResponseEntity<ErrorResponse> handleSessionAlreadyReservedSpotsException(InsufficientSpotsException e) {
        log.error("Session spots conflict: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Insufficient spots available for session",
                "INSUFFICIENT_SPOTS",
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 404 NOT FOUND
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFoundException(SessionNotFoundException e) {
        log.error("Session not found: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Session not found",
                "SESSION_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 409 CONFLICT
    @ExceptionHandler(SessionOverlappingTimeException.class)
    public ResponseEntity<ErrorResponse> handleSessionOverlappingTimeException(SessionOverlappingTimeException e) {
        log.error("Session time conflict: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Session time conflict",
                "SESSION_TIME_CONFLICT",
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 400 BAD REQUEST
    @ExceptionHandler(SessionStartingTimeException.class)
    public ResponseEntity<ErrorResponse> handleSessionStartingTimeException(SessionStartingTimeException e) {
        log.error("Invalid session start time: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Invalid session start time",
                "INVALID_START_TIME",
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 409 CONFLICT
    @ExceptionHandler(SessionFullException.class)
    public ResponseEntity<ErrorResponse> handleSessionFullException(SessionFullException e) {
        log.error("Session full: {}", e.getMessage());

        Map<String, Object> details = new HashMap<>();
         details.put("currentParticipants", e.getCurrentCount());
         details.put("maxCapacity", e.getMaxCapacity());

        ErrorResponse error = new ErrorResponse(
                "Session capacity exceeded",
                "SESSION_FULL",
                HttpStatus.CONFLICT,
                details
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 409 CONFLICT
    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException e) {
        log.error("User already registered: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "User already enrolled",
                "USER_ALREADY_REGISTERED",
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 422 UNPROCESSABLE_ENTITY
    @ExceptionHandler(CancellationDeadlinePassedException.class)
    public ResponseEntity<ErrorResponse> handleCancellationDeadlinePassedException(CancellationDeadlinePassedException e) {
        log.error("Cancellation deadline passed: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Cancellation deadline exceeded",
                "CANCELLATION_DEADLINE_PASSED",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
        return ResponseEntity.status(422).body(error);
    }

    // 409 CONFLICT
    @ExceptionHandler(UserNotRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleUserNotRegisteredException(UserNotRegisteredException e) {
        log.error("User not registered: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                "User not enrolled in session",
                "USER_NOT_REGISTERED",
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 400 BAD REQUEST
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> details = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            details.put(fieldName, errorMessage);
        });

        ErrorResponse error = new ErrorResponse(
                "Validation failed",
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST,
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error: ", e);
        ErrorResponse error = new ErrorResponse(
                "An unexpected error occurred",
                "INTERNAL_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
