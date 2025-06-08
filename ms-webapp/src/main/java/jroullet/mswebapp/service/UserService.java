package jroullet.mswebapp.service;

import jroullet.mswebapp.auth.RegisterRequestDTO;
import jroullet.mswebapp.auth.RegisterResponseDTO;
import jroullet.mswebapp.clients.IdentityFeignClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IdentityFeignClient identityFeignClient;
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    public void registration(RegisterRequestDTO form) {
        logger.info("Processing registration for email: {}", form.getEmail());
        try {
            // ms-identity management
            // ATTENTION : password still visible !
            RegisterResponseDTO savedUser = identityFeignClient.registerUser(form).getBody();
            assert savedUser != null;
            logger.info("User successfully registered: {}", savedUser.getEmail());
        } catch (Exception e) {
            logger.error("Error during registration: {}", form.getEmail(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }
}

