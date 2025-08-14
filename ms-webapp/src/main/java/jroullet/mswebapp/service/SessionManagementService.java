package jroullet.mswebapp.service;

import feign.FeignException;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.session.*;
import jroullet.mswebapp.dto.session.create.SessionCreationDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationResponseDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationWithTeacherDTO;
import jroullet.mswebapp.dto.session.credits.CreditOperationResponse;
import jroullet.mswebapp.dto.session.credits.SessionRegistrationDeductRequest;
import jroullet.mswebapp.dto.session.credits.SessionRollbackRefundRequest;
import jroullet.mswebapp.dto.session.participant.AddParticipantRequest;
import jroullet.mswebapp.dto.session.participant.ParticipantOperationResponse;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import jroullet.mswebapp.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SessionManagementService {

    private final IdentityFeignClient identityFeignClient;
    private final CourseManagementFeignClient courseFeignClient;
    private final SessionService sessionService;
    private final String internalSecret;

    public SessionManagementService(IdentityFeignClient identityFeignClient,
                                    CourseManagementFeignClient courseManagementFeignClient, SessionService sessionService,
                                    @Value("${app.internal.secret}") String internalSecret) {
        this.identityFeignClient = identityFeignClient;
        this.sessionService = sessionService;
        this.courseFeignClient = courseManagementFeignClient;
        this.internalSecret = internalSecret;
    }


    /**
     *      Common Methods
     */
    public List<UserParticipantDTO> getSessionParticipants(Long sessionId) {
        SessionWithParticipantsDTO session = courseFeignClient.getSessionById(sessionId);
        if (session.getParticipantIds().isEmpty()) {
            return Collections.emptyList();
        }
        return identityFeignClient.getUsersByIds(session.getParticipantIds());
    }


    /**
     *     TEACHER METHODS
      */

    public List<SessionWithParticipantsDTO> getUpcomingSessionsForCurrentTeacher(Long teacherId) {
        try {
            List<SessionWithParticipantsDTO> upcomingSessions = courseFeignClient.getUpcomingSessionsByTeacher(teacherId);
            log.info("Loaded {} upcoming sessions for teacher ID: {}", upcomingSessions.size(), teacherId);
            return upcomingSessions;

        } catch (FeignException e) {
            log.error("Error fetching sessions from ms-course-mgmt for teacher {}: {}", teacherId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<SessionWithParticipantsDTO> getPastSessionsForCurrentTeacher(Long teacherId) {
        try {
            List<SessionWithParticipantsDTO> pastSessions = courseFeignClient.getPastSessionsByTeacher(teacherId);
            log.info("Loaded {} past sessions for teacher ID: {}", pastSessions.size(), teacherId);
            return pastSessions;
        } catch (FeignException e) {
            log.error("Error fetching past sessions for teacher {}: {}", teacherId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public SessionCreationResponseDTO createSessionForCurrentTeacher(SessionCreationDTO dto) {
        try {
            UserDTO currentTeacher = sessionService.getCurrentUser();
            SessionCreationWithTeacherDTO enrichedDto = SessionCreationWithTeacherDTO.builder()
                    // session fields
                    .subject(dto.getSubject())
                    .description(dto.getDescription())
                    .isOnline(dto.getIsOnline())
                    .roomName(dto.getRoomName())
                    .postalCode(dto.getPostalCode())
                    .googleMapsLink(dto.getGoogleMapsLink())
                    .zoomLink(dto.getZoomLink())
                    .availableSpots(dto.getAvailableSpots())
                    .startDateTime(dto.getStartDateTime())
                    .durationMinutes(dto.getDurationMinutes())
                    .creditsRequired(dto.getCreditsRequired())
                    .bringYourMattress(dto.getBringYourMattress())
                    // teacher fields
                    .teacherId(currentTeacher.getId())
                    .teacherFirstName(currentTeacher.getFirstName())
                    .teacherLastName(currentTeacher.getLastName())
                    .build();

            SessionCreationResponseDTO response = courseFeignClient.createSession(enrichedDto);
            log.info("Session created successfully for teacher ID: {}", enrichedDto.getTeacherId());
            return response;
        } catch (FeignException e) {
            log.error("Error creating session for teacher: {}", e.getMessage());
            throw e; // Re-throw pour que le controller gère l'erreur spécifique
        }
    }

    public void cancelSessionForCurrentTeacher(Long sessionId) {
        try {
            Long currentUserId = sessionService.getCurrentUser().getId();
            SessionCancelDTO dto = SessionCancelDTO.builder()
                    .sessionId(sessionId)
                    .teacherId(currentUserId)
                    .build();

            courseFeignClient.cancelSessionByTeacher(dto);
            log.info("Session {} canceled successfully by teacher ID: {}", sessionId, currentUserId);
        } catch (FeignException e) {
            log.error("Error canceling session {} : {}", sessionId, e.getMessage());
            throw e;
        }
    }


    /**
     *     ADMIN METHODS
     */

    public List<SessionWithParticipantsDTO> getAllSessionsForAdmin() {
        List<SessionWithParticipantsDTO> sessions = courseFeignClient.getAllSessionsForAdmin();
        log.info("Loaded {} sessions", sessions.size());
        return sessions;
    }

    public void cancelSessionByAdmin(Long sessionId) {
        try {
            courseFeignClient.cancelSessionByAdmin(sessionId);
            log.info("Session {} canceled successfully", sessionId);
        } catch (FeignException e) {
            log.error("Error canceling session  {} by admin : {}", sessionId, e.getMessage());
            throw e;
        }
    }


    /**
     *      User Methods
     */
    public List<SessionNoParticipantsDTO> getAvailableSessionsForClient() {
        try {
            UserDTO currentUser = sessionService.getCurrentUser();
            // Fetch available sessions from the course management service
            List<SessionNoParticipantsDTO> availableSessions = courseFeignClient.getAvailableSessionsForClient();
            log.info("Loaded {} available sessions for client", availableSessions.size());
            List<SessionNoParticipantsDTO> userSessions = courseFeignClient.getUpcomingSessionsForClient(currentUser.getId());
            Set<Long> userSessionIds = userSessions.stream()
                    .map(SessionNoParticipantsDTO::getId)
                    .collect(Collectors.toSet());
            return availableSessions.stream().filter(session -> !userSessionIds.contains(session.getId()))
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error fetching available sessions: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<SessionNoParticipantsDTO> getUpcomingSessionsForClient() {
        try {
            Long currentUserId = sessionService.getCurrentUser().getId();
            // Fetch available sessions from the course management service
            List<SessionNoParticipantsDTO> upcomingSessions = courseFeignClient.getUpcomingSessionsForClient(currentUserId);
            log.info("Loaded {} upcoming sessions for client", upcomingSessions.size());
            return upcomingSessions;
        } catch (FeignException e) {
            log.error("Error fetching upcoming sessions for current user : {}" , e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<SessionNoParticipantsDTO> getPastSessionsForClient() {
        try {
            Long currentUserId = sessionService.getCurrentUser().getId();
            List<SessionNoParticipantsDTO> pastSessions = courseFeignClient.getPastSessionsForClient(currentUserId);
            log.info("Loaded {} past sessions for client ID: {}", pastSessions.size(), currentUserId);

            return pastSessions;

        } catch (FeignException e) {
            log.error("Error fetching past sessions for currentUser : {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public int registerToSession(Long sessionId) {
        UserDTO user = sessionService.getCurrentUser();
        Long userId = user.getId();

        log.info("Starting session registration for user {} to session {}", userId, sessionId);

        SessionWithParticipantsDTO session = getSessionDetails(sessionId);

        // Validations
        validateUserHasSufficientCredits(user, session.getCreditsRequired());
        validateSessionAvailability(session);
        validateUserNotAlreadyRegistered(session, userId);

        log.info("All validations passed for user {} and session {}", userId, sessionId);

        // Credit deduction
        CreditOperationResponse creditResponse = deductUserCredits(userId, sessionId, session.getCreditsRequired());
        log.info("Credits deducted successfully for user {}. Previous: {}, New: {}",
                userId, creditResponse.previousCredits(), creditResponse.newCredits());

        try {
            // Participant registration
            ParticipantOperationResponse participantResponse = addUserToSession(sessionId, userId);
            log.info("User {} successfully registered to session {}. Participants: {}/{}",
                    userId, sessionId, participantResponse.currentParticipantCount(),
                    participantResponse.availableSpots());

            //Returns the new credits after successful registration
            return creditResponse.newCredits();

        } catch (Exception participantException) {
            // Error handling
            log.error("Unexpected failure adding participant despite validations. Rolling back credits for user {} and session {}",
                    userId, sessionId, participantException);

            try {
                rollbackUserCredits(userId, sessionId, session.getCreditsRequired());
                log.info("Credits successfully rolled back for user {} after unexpected failure", userId);
            } catch (Exception rollbackException) {
                throw new CreditRollbackFailedException(userId, sessionId, session.getCreditsRequired(), rollbackException);
            }

            throw new SessionRegistrationException("Unexpected failure during registration", participantException);
        }
    }

    /**
     * 🎯 SESSION UNREGISTRATION
     */
    @Transactional
    public int unregisterFromSession(Long sessionId) {
        Long userId = sessionService.getCurrentUser().getId();

        log.info("Starting session unregistration for user {} from session {}", userId, sessionId);

        SessionWithParticipantsDTO session = getSessionDetails(sessionId);

        // remove participant from session
        ParticipantOperationResponse participantResponse = removeUserFromSession(sessionId, userId);
        log.info("User {} successfully removed from session {}. Participants: {}/{}",
                userId, sessionId, participantResponse.currentParticipantCount(),
                participantResponse.availableSpots());

        // refund credits
        CreditOperationResponse creditResponse = refundUserCreditsForCancellation(userId, sessionId, session.getCreditsRequired());
        log.info("Credits refunded successfully for user {} after cancellation from session {}",
                userId, sessionId);

        return creditResponse.newCredits();

    }


    private SessionWithParticipantsDTO getSessionDetails(Long sessionId) {
        SessionWithParticipantsDTO sessionResponse = courseFeignClient.getSessionById(sessionId);
        if (sessionResponse == null) {
            throw new SessionNotFoundException(sessionId);
        }
        return sessionResponse;
    }

    private void validateSessionAvailability(SessionWithParticipantsDTO session) {
        int currentParticipants = session.getParticipantIds() != null ? session.getParticipantIds().size() : 0;
        int maxCapacity = session.getAvailableSpots();

        if (currentParticipants >= maxCapacity) {
            throw new SessionNotAvailableException(session.getId(), currentParticipants, maxCapacity);
        }
    }

    private void validateUserHasSufficientCredits(UserDTO user, Integer creditsRequired) {
        if (user.getCredits() < creditsRequired) {
            log.warn("User {} has insufficient credits. Available: {}, Required: {}",
                    user.getId(), user.getCredits(), creditsRequired);
            throw new InsufficientCreditsException(user.getId(), user.getCredits(), creditsRequired);
        }
        log.debug("User {} has sufficient credits. Available: {}, Required: {}",
                user.getId(), user.getCredits(), creditsRequired);
    }

    private void validateUserNotAlreadyRegistered(SessionWithParticipantsDTO session, Long userId) {
        if (session.getParticipantIds() != null && session.getParticipantIds().contains(userId)) {
            log.warn("User {} is already registered for session {}", userId, session.getId());
            throw new UserAlreadyRegisteredException(userId, session.getId());
        }
        log.debug("User {} is not yet registered for session {}", userId, session.getId());
    }

    private CreditOperationResponse deductUserCredits(Long userId, Long sessionId, Integer creditsRequired) {
        SessionRegistrationDeductRequest request = new SessionRegistrationDeductRequest(
                userId, sessionId, creditsRequired, internalSecret);

        return identityFeignClient.deductCreditsForSessionRegistration(request);
        // ✅ Exceptions FeignException remontent (crédits insuffisants, utilisateur inexistant, etc.)
    }

    private void rollbackUserCredits(Long userId, Long sessionId, Integer creditsToRefund) {
        SessionRollbackRefundRequest request = new SessionRollbackRefundRequest(
                userId, sessionId, creditsToRefund, internalSecret);

        identityFeignClient.refundCreditsForSessionRollback(request);

    }

    private CreditOperationResponse refundUserCreditsForCancellation(Long userId, Long sessionId, Integer creditsToRefund) {
        SessionRollbackRefundRequest request = new SessionRollbackRefundRequest(
                userId, sessionId, creditsToRefund, internalSecret);

        return identityFeignClient.refundCreditsForSessionRollback(request);

    }

    private ParticipantOperationResponse addUserToSession(Long sessionId, Long userId) {
        AddParticipantRequest request = new AddParticipantRequest(userId);
        return courseFeignClient.addParticipantToSession(sessionId, request);

    }

    private ParticipantOperationResponse removeUserFromSession(Long sessionId, Long userId) {
        return courseFeignClient.removeParticipantFromSession(sessionId, userId);
    }

}
