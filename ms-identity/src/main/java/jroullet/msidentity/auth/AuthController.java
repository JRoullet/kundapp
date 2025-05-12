package jroullet.msidentity.auth;

import jroullet.msidentity.exception.UserAlreadyExistsException;
import jroullet.msidentity.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);


    // Authentication when signing in
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO request) {
        try {
            AuthResponseDTO authResponseDTO = authService.authenticate(request);
            if ((authResponseDTO.isAuthenticated())) {
                return ResponseEntity.ok(authResponseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponseDTO);
            }
        }
        catch (Exception e) {
            logger.error("error authenticating", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponseDTO(false,null,null,null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(@RequestBody RegisterRequestDTO request) {
        try {
            // AuthService manages logic
            User savedUser = authService.registerUser(request);

            RegisterResponseDTO registerResponseDTO = RegisterResponseDTO
                    .builder()
                    .id(savedUser.getId())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole().toString())
                    .build();

            return ResponseEntity.ok(registerResponseDTO);

        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);

        } catch (Exception e) {
            logger.error("Error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}