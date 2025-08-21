package jroullet.mscoursemgmt.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * USELESS FOR NOW, HAS TO BE IMPLEMENTED LATER
 *
 * Security validator for internal service communication
 * Validates the internal secret used for inter-service requests
 *
 * Used by: ms-webapp to send notifications for session events
 * Error handling: Throws SecurityException if validation fails
 */
@Component
@Slf4j
public class SecurityValidator {

    @Value("${app.internal.secret}")
    private String internalSecret;

    public void validateInternalSecret(String providedSecret) {
        if (!internalSecret.equals(providedSecret)) {
            log.warn("Invalid internal secret provided");
            throw new SecurityException("Invalid internal secret");
        }
    }
}
