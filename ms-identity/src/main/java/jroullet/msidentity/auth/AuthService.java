package jroullet.msidentity.auth;

import jroullet.msidentity.exception.UserAlreadyExistsException;
import jroullet.msidentity.model.Role;
import jroullet.msidentity.model.User;
import jroullet.msidentity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("User found: {}", user.getEmail());

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                logger.info("Authentication successful for: {}", user.getEmail());
                return new AuthResponseDTO(
                        true,
                        user.getId(),
                        user.getEmail(),
                        user.getRole()
                );
            } else {
                logger.warn("Invalid password for user: {}", user.getEmail());
            }
        } else {
            logger.warn("User not found: {}", request.getEmail());
        }

        logger.info("Authentication failed for: {}", request.getEmail());
        return new AuthResponseDTO(false, null, null, null);
    }

    public User registerUser(RegisterRequestDTO registerRequestDTO) {
        // check email existence
        if (userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        // create new user
        User newUser = new User();
        newUser.setEmail(registerRequestDTO.getEmail());

        // encode password
        newUser.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));

        // define default role
        newUser.setRole(Role.CLIENT);

        // save
        return userRepository.save(newUser);
    }
}
