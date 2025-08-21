package jroullet.mswebapp.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jroullet.mswebapp.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Map<String, String> ROLE_REDIRECT_MAP = Map.of(
            Role.ADMIN.getAuthority(), "/admin",
            Role.CLIENT.getAuthority(), "/client",
            Role.TEACHER.getAuthority(), "/teacher"
    );

    // Manages each possible route when connecting as a user
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String authority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_UNKNOWN");

        String redirectUrl = ROLE_REDIRECT_MAP.getOrDefault(authority, "/signin");

        logger.info("Authority: {}, Redirect to: {}", authority, redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
