package jroullet.msidentity.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
