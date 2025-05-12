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
            logger.info("Authenticated user : {}", userOpt.map(User::getEmail).orElse(null));
            User user = userOpt.get();
            // verify password matches with encrypted database password
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new AuthResponseDTO(
                        true,
                        user.getId(),
                        user.getEmail(),
                        user.getRole().name()
                );
            }
        }
        // Authentication failed
        logger.info("User with email {} not found", request.getEmail());
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
