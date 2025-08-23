package jroullet.mswebapp.service;

import feign.FeignException;
import jroullet.mswebapp.auth.RegisterRequestDTO;
import jroullet.mswebapp.auth.RegisterResponseDTO;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IdentityFeignClient identityFeignClient;
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Process user registration (PUBLIC)
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

    /**
     * Get multiple users by IDs (ADMIN)
     */
    public List<UserParticipantDTO> getUsersByIds(List<Long> userIds) {
        return identityFeignClient.getUsersByIds(userIds);
    }

    /**
     * Get participants for teacher view (TEACHER)
     */
    public List<UserParticipantDTO> getParticipantsByIdsForTeacher(List<Long> participantIds) {
        return identityFeignClient.getParticipantsByIdsForTeacher(participantIds);
    }

    /**
     * Get basic user information by ID (USER)
     */
    public UserParticipantDTO getUserBasicInfoById(Long id) {
        return identityFeignClient.getUserBasicInfo(id);
    }
    /**
     * Get multiple users basic info (USER)
     */
    public List<UserParticipantDTO> getUsersBasicInfo(List<Long> userIds) {
        return identityFeignClient.getUsersBasicInfo(userIds);
    }



}

