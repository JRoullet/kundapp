package jroullet.mswebapp.auth;


import jakarta.servlet.http.HttpServletRequest;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.EmailDto;
import jroullet.mswebapp.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final IdentityFeignClient identityFeignClient;
    private final HttpServletRequest request;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public User getCurrentUser() {

        // Try catching active session user
        Object sessionUser = request.getSession().getAttribute("currentUser");
        if (sessionUser instanceof AuthResponseDTO dto){
            logger.info("User found in session {}", dto.getEmail());
            return identityFeignClient.findUserByEmail(new EmailDto(dto.getEmail()));
        }

        // Fallback method SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check authentication validity
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No active session found");
        }

        Object principal = authentication.getPrincipal();
        // Check if principal is a Spring Security user and retrieve username (email)
        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            String email = springUser.getUsername();
            logger.info("Fallback to identityFeignClient with email: {}", email);
            return identityFeignClient.findUserByEmail(new EmailDto(email));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session principal");
    }
}
