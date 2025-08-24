package jroullet.mswebapp.service;

import feign.FeignException;
import jroullet.mswebapp.auth.RegisterRequestDTO;
import jroullet.mswebapp.auth.RegisterResponseDTO;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IdentityFeignClient identityFeignClient;
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Job based methods called directly from controller need to call this service :
     * - registration : Process user registration (PUBLIC)
     * - getUserById (ADMIN)
     */
    public void registration(RegisterRequestDTO form) {
        logger.info("Processing registration for email: {}", form.getEmail());
        try {
            // ms-identity management
            RegisterResponseDTO savedUser = identityFeignClient.registerUser(form).getBody();
            assert savedUser != null;
            logger.info("User successfully registered: " +
                    "firstName: {}," +
                    "lastName: {}," +
                    "role : {}," +
                    "email : {}", savedUser.getFirstName(), savedUser.getLastName(),savedUser.getRole(), savedUser.getEmail());
        } catch (FeignException e) {
            logger.error("FeignException during registration for email: {}, HTTP: {}, Message: {}",
                    form.getEmail(), e.status(), e.getMessage());
            throw e; // transfers feign exception as it is, to controller
        } catch (Exception e) {
            logger.error("Unexpected error during registration for email: {}", form.getEmail(), e);
            throw new RuntimeException("Erreur technique lors de l'inscription", e);
        }
    }

    /**
     * Get full user details by ID (ADMIN)
     */
    public UserDTO getUserById(Long userId) {
        return identityFeignClient.getUserById(userId);
    }

}

