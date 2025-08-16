package jroullet.mswebapp.service;

import feign.FeignException;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.session.*;
import jroullet.mswebapp.dto.session.create.SessionCreationDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationResponseDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationWithTeacherDTO;
import jroullet.mswebapp.dto.session.credits.BatchCreditOperationRequest;
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

import java.util.ArrayList;
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
            Long currentTeacherId = sessionService.getCurrentUser().getId();
            log.info("Teacher ID {} is attempting to cancel session {}", currentTeacherId, sessionId);

        try {
            // step 1 : fetch session details
            SessionWithParticipantsDTO session = getSessionDetails(sessionId);

            List<Long> participantIds = session.getParticipantIds() != null?
                    session.getParticipantIds() : new ArrayList<>();

            if(participantIds.isEmpty()) {
                log.info("No participants found for session {}, proceeding with direct cancellation", sessionId);
                cancelSessionByTeacher(sessionId, currentTeacherId);
                return;
            }

            log.info("Found {} participants for session {}, processing batch refund", participantIds.size(), sessionId);

            // step 2 : batch refund participants (all or nothing)
        try{
            batchRefundParticipants(sessionId, participantIds, session.getCreditsRequired());
            log.info("Successfully refunded {} participants for session {}", participantIds.size(), sessionId);

        } catch (FeignException e){
            log.error("Batch refund failed for session {}: {}", sessionId, e.getMessage());
            throw new SessionCancellationException("Failed to refund participants: " + e.getMessage());
        }

            // step 3: cancel the session
        try {
            cancelSessionByTeacher(sessionId, currentTeacherId);
            log.info("Session {} successfully canceled after refunding {} participants ", sessionId, participantIds.size());
        } catch(FeignException e) {
            log.error("Session cancellation failed AFTER successful refunds for session {}. Rolling back refunds.", sessionId);

            try {
                batchRollbackRefunds(sessionId, participantIds, session.getCreditsRequired());
                log.info("Successfully rolled back all refunds for session {} ", sessionId);
            } catch (FeignException rollbackException) {
                log.error("CRITICAL: Failed to rollback refunds for session {}: {}", sessionId, rollbackException.getMessage());
            }

            throw new SessionCancellationException("Session cancellation failed, refunds rolled back");
        }

        } catch (SessionCancellationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during session {} cancellation: {}", sessionId, e.getMessage());
            throw new SessionCancellationException("Unexpected error during session cancellation");
        }
    }

    public List<UserParticipantDTO> getSessionParticipantsForTeacher(Long sessionId) {

        Long currentTeacherId = sessionService.getCurrentUser().getId();
        log.info("Fetching participants for teacher's session {}", sessionId);

        // verify session belongs to teacher
        SessionWithParticipantsDTO session = courseFeignClient.getSessionById(sessionId);
        if (!session.getTeacherId().equals(currentTeacherId)) {
            throw new SecurityException("Teacher can only view participants of their own sessions");
        }

        if (session.getParticipantIds() == null || session.getParticipantIds().isEmpty()) {
            return Collections.emptyList();
        }

        return identityFeignClient.getParticipantsByIdsForTeacher(session.getParticipantIds());
    }


    /**
     *     ADMIN METHODS
     */

    public List<SessionWithParticipantsDTO> getAllSessionsForAdmin() {
        List<SessionWithParticipantsDTO> sessions = courseFeignClient.getAllSessionsForAdmin();
        log.info("Loaded {} sessions", sessions.size());
        return sessions;
    }

    public List<UserParticipantDTO> getSessionParticipants(Long sessionId) {
        SessionWithParticipantsDTO session = courseFeignClient.getSessionById(sessionId);
        if (session.getParticipantIds().isEmpty()) {
            return Collections.emptyList();
        }
        return identityFeignClient.getUsersByIds(session.getParticipantIds());
    }

    /**
     * Cancel session by admin with automatic participant refund
     */
    public void cancelSessionByAdmin(Long sessionId) {
        log.info("Admin is attempting to cancel session {}", sessionId);

        try {
            // Step 1: Fetch session details
            SessionWithParticipantsDTO session = getSessionDetails(sessionId);

            List<Long> participantIds = session.getParticipantIds() != null ?
                    session.getParticipantIds() : new ArrayList<>();

            if (participantIds.isEmpty()) {
                log.info("No participants found for session {}, proceeding with direct cancellation", sessionId);
                cancelSessionByAdminDirect(sessionId);
                return;
            }

            log.info("Found {} participants for session {}, processing batch refund", participantIds.size(), sessionId);

            // Step 2: Batch refund participants (all or nothing)
            try {
                batchRefundParticipantsForAdmin(sessionId, participantIds, session.getCreditsRequired());
                log.info("Successfully refunded {} participants for session {}", participantIds.size(), sessionId);
            } catch (FeignException e) {
                log.error("Batch refund failed for session {}: {}", sessionId, e.getMessage());
                throw new SessionCancellationException("Failed to refund participants: " + e.getMessage());
            }

            // Step 3: Cancel the session
            try {
                cancelSessionByAdminDirect(sessionId);
                log.info("Session {} successfully canceled by admin after refunding {} participants", sessionId, participantIds.size());
            } catch (FeignException e) {
                log.error("Session cancellation failed AFTER successful refunds for session {}. Rolling back refunds.", sessionId);

                try {
                    batchRollbackRefunds(sessionId, participantIds, session.getCreditsRequired());
                    log.info("Successfully rolled back all refunds for session {}", sessionId);
                } catch (FeignException rollbackException) {
                    log.error("CRITICAL: Failed to rollback refunds for session {}: {}", sessionId, rollbackException.getMessage());
                }

                throw new SessionCancellationException("Session cancellation failed, refunds rolled back");
            }

        } catch (SessionCancellationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during admin session {} cancellation: {}", sessionId, e.getMessage());
            throw new SessionCancellationException("Unexpected error during admin session cancellation");
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


    /**
     *      PRIVATE HELPER METHODS
     */

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

    private ParticipantOperationResponse addUserToSession(Long sessionId, Long userId) {
        AddParticipantRequest request = new AddParticipantRequest(userId);
        return courseFeignClient.addParticipantToSession(sessionId, request);

    }

    private ParticipantOperationResponse removeUserFromSession(Long sessionId, Long userId) {
        return courseFeignClient.removeParticipantFromSession(sessionId, userId);
    }

    private CreditOperationResponse deductUserCredits(Long userId, Long sessionId, Integer creditsRequired) {
        SessionRegistrationDeductRequest request = new SessionRegistrationDeductRequest(
                userId, sessionId, creditsRequired, internalSecret);

        return identityFeignClient.deductCreditsForSessionRegistration(request);
    }

    private void rollbackUserCredits(Long userId, Long sessionId, Integer creditsToRefund) {
        SessionRollbackRefundRequest request = new SessionRollbackRefundRequest(
                userId, sessionId, creditsToRefund, internalSecret);
        log.info("Rolling back credits for user {} for session {} with {} credits",
                userId, sessionId, creditsToRefund);
        identityFeignClient.refundCreditsForSessionRollback(request);

    }

    private CreditOperationResponse refundUserCreditsForCancellation(Long userId, Long sessionId, Integer creditsToRefund) {
        SessionRollbackRefundRequest request = new SessionRollbackRefundRequest(
                userId, sessionId, creditsToRefund, internalSecret);

        return identityFeignClient.refundCreditsForSessionRollback(request);

    }

    private void batchRefundParticipants(Long sessionId, List<Long> participantIds, Integer creditsToRefund) {
        BatchCreditOperationRequest request = BatchCreditOperationRequest.builder()
                .sessionId(sessionId)
                .participantIds(participantIds)
                .creditsPerParticipant(creditsToRefund)
                .reason("SESSION_CANCELED_BY_TEACHER")
                .internalSecret(internalSecret)
                .build();
        identityFeignClient.batchRefundCreditsForCancellation(request);
    }

    private void batchRefundParticipantsForAdmin(Long sessionId, List<Long> participantIds, Integer creditsToRefund) {
        BatchCreditOperationRequest request = BatchCreditOperationRequest.builder()
                .sessionId(sessionId)
                .participantIds(participantIds)
                .creditsPerParticipant(creditsToRefund)
                .reason("SESSION_CANCELED_BY_ADMIN")
                .internalSecret(internalSecret)
                .build();
        identityFeignClient.batchRefundCreditsForCancellation(request);
    }

    private void batchRollbackRefunds(Long sessionId, List<Long> participantIds, Integer creditsToRefund) {
        BatchCreditOperationRequest request = BatchCreditOperationRequest.builder()
                .sessionId(sessionId)
                .participantIds(participantIds)
                .creditsPerParticipant(creditsToRefund)
                .reason("ROLLBACK_REFUND_AFTER_CANCELLATION_FAILURE")
                .internalSecret(internalSecret)
                .build();
        identityFeignClient.batchDeductCreditsForRollback(request);
    }

    private void cancelSessionByTeacher(Long sessionId, Long teacherId) {
        SessionCancelDTO request = SessionCancelDTO.builder()
                .sessionId(sessionId)
                .teacherId(teacherId)
                .build();
        courseFeignClient.cancelSessionByTeacher(request);

    }

    private void cancelSessionByAdminDirect(Long sessionId) {
        courseFeignClient.cancelSessionByAdmin(sessionId);
    }
}
