package jroullet.msidentity.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // code 409
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.error("=== EMAIL ALREADY EXISTS HANDLER CALLED ===");
        log.error("Email already exists: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // code 403
    @ExceptionHandler(RoleNotAllowedException.class)
    public ResponseEntity<String> handleRoleNotAllowedException(RoleNotAllowedException e) {
        log.error("Role not allowed : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    // code 409
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        log.error("=== USER ALREADY EXISTS HANDLER CALLED ===");
        log.error("User already exists : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    // code 404
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        log.error("User not found : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // code 422 for business logic errors
    @ExceptionHandler(InsufficientCreditsException.class)
    public ResponseEntity<String> handleInsufficientCreditsException(InsufficientCreditsException e) {
        log.error("Insufficient credits : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }

    // code 403
    @ExceptionHandler(UnauthorizedInternalAccessException.class)
    public ResponseEntity<String> handleUnauthorizedInternalAccessException(UnauthorizedInternalAccessException e) {
        log.error("Unauthorized internal access : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur technique");
    }
}
