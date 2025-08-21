package jroullet.msnotification.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Validate internal secret for microservice security
 * This component checks the provided secret against the configured internal secret.
 * @throws SecurityException if secret is invalid (handled by exception handler)
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
