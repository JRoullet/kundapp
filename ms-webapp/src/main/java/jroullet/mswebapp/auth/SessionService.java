package jroullet.mswebapp.auth;


import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.EmailDto;
import jroullet.mswebapp.dto.UserDTO;
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

    public UserDTO getCurrentUser() {

        // Try catching active session user
        Object sessionUser = request.getSession().getAttribute("currentUserDTO");
        if (sessionUser instanceof UserDTO dto) {
            logger.info("UserDTO found in session: {}", dto.getEmail());
            return dto;
        }

        // Get User from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check authentication validity
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No active session found");
        }

        String email = authentication.getName();
        logger.info("Fetching user DTO from ms-identity for: {}", email);

        try{
            UserDTO userDto = identityFeignClient.findUserDtoByEmail(new EmailDto(email));
            request.getSession().setAttribute("currentUserDTO", userDto);
            return userDto;

        }
        catch(FeignException.NotFound e){
            logger.error("User not found in ms-identity: {}", email);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email);
        }
        catch(FeignException e){
            logger.error("Error calling ms-identity : {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Unable to retrieve user information");
        }

    }

    public void clearCurrentUser() {
        request.getSession().removeAttribute("currentUserDTO");
    }

    public UserDTO refreshCurrentUser() {
        clearCurrentUser();
        return getCurrentUser();
    }
}
