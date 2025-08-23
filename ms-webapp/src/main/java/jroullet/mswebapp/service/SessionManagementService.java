package jroullet.mswebapp.service;

import feign.FeignException;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.dto.session.SessionCancelDTO;
import jroullet.mswebapp.dto.session.SessionNoParticipantsDTO;
import jroullet.mswebapp.dto.session.SessionUpdateDTO;
import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationResponseDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationWithTeacherDTO;
import jroullet.mswebapp.dto.session.credits.CreditOperationResponse;
import jroullet.mswebapp.dto.session.participant.AddParticipantRequest;
import jroullet.mswebapp.dto.session.participant.ParticipantOperationResponse;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import jroullet.mswebapp.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionManagementService {

    private final CourseManagementFeignClient courseFeignClient;
    private final SessionService sessionService;
    private final CreditService creditService;
    private final NotificationService notificationService;
    private final ValidationService validationService;
    private final UserService userService;


    /**
     *     TEACHER METHODS
      */
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
            // Notify the notification service about the new session creation
            try {
                SessionWithParticipantsDTO session = getSessionDetails(response.getSessionId());
                notificationService.sendSessionCreatedNotification(currentTeacher.getId(),session);
                log.debug("Session creation notification sent for session ID: {}", response.getSessionId());
            } catch (Exception notificationException) {
                log.warn("Failed to send session creation notification for session : {} :  {}", response.getSessionId(), notificationException.getMessage());
            }

            return response;
        } catch (FeignException e) {
            log.error("Error creating session for teacher: {}", e.getMessage());
            throw e; // Re-throw pour que le controller gère l'erreur spécifique
        }
    }
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
    public void updateSessionForCurrentTeacher(Long sessionId, SessionUpdateDTO sessionUpdateDTO) {
        Long currentTeacherId = sessionService.getCurrentUser().getId();
        log.info("Teacher ID {} is attempting to update session {}", currentTeacherId, sessionId);

        // Fetch session details
        SessionWithParticipantsDTO originalSession = getSessionDetails(sessionId);

        // Verify ownership
        validationService.validateSessionOwnership(originalSession, currentTeacherId);

        // Update session
        SessionWithParticipantsDTO updatedSession = courseFeignClient.updateSessionByTeacher(sessionId, currentTeacherId, sessionUpdateDTO);

        // Notify the notification service about the session update
        if (validationService.hasSignificantChanges(originalSession, updatedSession)) {
            try {
                String modificationSummary = notificationService.buildModificationSummary(originalSession, updatedSession);
                notificationService.sendSessionModifiedNotifications(updatedSession, modificationSummary);
                log.info("Session modification notification sent to participants with summary: {}", modificationSummary);
            } catch (Exception notificationException) {
                log.warn("Failed to send session modification notification for session {}: {}", sessionId, notificationException.getMessage());
            }
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
            creditService.batchRefundCredits(sessionId, participantIds, session.getCreditsRequired(),"SESSION_CANCELED_BY_TEACHER");
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
                creditService.batchRollbackCredits(sessionId, participantIds, session.getCreditsRequired());
                log.info("Successfully rolled back all refunds for session {} ", sessionId);
            } catch (FeignException rollbackException) {
                log.error("CRITICAL: Failed to rollback refunds for session {}: {}", sessionId, rollbackException.getMessage());
            }

            throw new SessionCancellationException("Session cancellation failed, refunds rolled back");
        }

            // step 4 : notify participants about cancellation
        try{
            notificationService.sendSessionCancelledNotifications(session);
            log.info("Session cancellation notification sent to participants {} ", participantIds.size());
        } catch (Exception notificationException) {
            log.warn("Failed to send session cancellation notification for session {}: {}", sessionId, notificationException.getMessage());
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
        validationService.validateSessionOwnership(session, currentTeacherId);

        if (session.getParticipantIds() == null || session.getParticipantIds().isEmpty()) {
            return Collections.emptyList();
        }

        return userService.getParticipantsByIdsForTeacher(session.getParticipantIds());
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
        return userService.getUsersByIds(session.getParticipantIds());
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
                creditService.batchRefundCredits(sessionId, participantIds, session.getCreditsRequired(),"SESSION_CANCELED_BY_ADMIN");
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
                    creditService.batchRollbackCredits(sessionId, participantIds, session.getCreditsRequired());
                    log.info("Successfully rolled back all refunds for session {}", sessionId);
                } catch (FeignException rollbackException) {
                    log.error("CRITICAL: Failed to rollback refunds for session {}: {}", sessionId, rollbackException.getMessage());
                }

                throw new SessionCancellationException("Session cancellation failed, refunds rolled back");
            }
            // Step 4: Notify participants about cancellation
            try{
                notificationService.sendSessionCancelledNotifications(session);
                log.info("Session cancellation notification sent to participants {} ", participantIds.size());
            } catch (Exception notificationException) {
                log.warn("Failed to send session cancellation notification for session {}: {}", sessionId, notificationException.getMessage());
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
        validationService.validateUserHasSufficientCredits(user, session.getCreditsRequired());
        validationService.validateSessionAvailability(session);
        validationService.validateUserNotAlreadyRegistered(session, userId);

        log.info("All validations passed for user {} and session {}", userId, sessionId);

        // Credit deduction
        CreditOperationResponse creditResponse = creditService.deductCredits(userId, sessionId, session.getCreditsRequired());
        log.info("Credits deducted successfully for user {}. Previous: {}, New: {}",
                userId, creditResponse.previousCredits(), creditResponse.newCredits());

        try {
            // Participant registration
            ParticipantOperationResponse participantResponse = addUserToSession(sessionId, userId);
            log.info("User {} successfully registered to session {}. Participants: {}/{}",
                    userId, sessionId, participantResponse.currentParticipantCount(),
                    participantResponse.availableSpots());

            // Send notification at successful registration
            try {
                notificationService.sendUserEnrolledNotifications(user.getId(), session);
                log.debug("User enrollment notifications sent successfully");
            } catch (Exception notificationException) {
                log.warn("Failed to send enrollment notifications for user {} and session {}: {}",
                        userId, sessionId, notificationException.getMessage());
            }

            //Returns the new credits after successful registration
            return creditResponse.newCredits();

        } catch (Exception participantException) {
            // Error handling
            log.error("Unexpected failure adding participant despite validations. Rolling back credits for user {} and session {}",
                    userId, sessionId, participantException);

            try {
                creditService.refundCredits(userId, sessionId, session.getCreditsRequired());
                log.info("Credits successfully rolled back for user {} after unexpected failure", userId);
            } catch (Exception rollbackException) {
                throw new CreditRollbackFailedException(userId, sessionId, session.getCreditsRequired(), rollbackException);
            }

            throw new SessionRegistrationException("Unexpected failure during registration", participantException);
        }
    }

    @Transactional
    public int unregisterFromSession(Long sessionId) {
        UserDTO user = sessionService.getCurrentUser();
        Long userId = user.getId();

        log.info("Starting session unregistration for user {} from session {}", userId, sessionId);

        SessionWithParticipantsDTO session = getSessionDetails(sessionId);

        // remove participant from session
        ParticipantOperationResponse participantResponse = removeUserFromSession(sessionId, userId);
        log.info("User {} successfully removed from session {}. Participants: {}/{}",
                userId, sessionId, participantResponse.currentParticipantCount(),
                participantResponse.availableSpots());

        // refund credits
        CreditOperationResponse creditResponse = creditService.refundCredits(userId, sessionId, session.getCreditsRequired());
        log.info("Credits refunded successfully for user {} after cancellation from session {}",
                userId, sessionId);

        // Send notification at successful unregistration
        try {
            notificationService.sendUserCancelledNotifications(user.getId(), session);
            log.debug("User cancellation notifications sent successfully");
        } catch (Exception notificationException) {
            log.warn("Failed to send cancellation notifications for user {} and session {}: {}",
                    userId, sessionId, notificationException.getMessage());
        }

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
    private ParticipantOperationResponse addUserToSession(Long sessionId, Long userId) {
        AddParticipantRequest request = new AddParticipantRequest(userId);
        return courseFeignClient.addParticipantToSession(sessionId, request);

    }
    private ParticipantOperationResponse removeUserFromSession(Long sessionId, Long userId) {
        return courseFeignClient.removeParticipantFromSession(sessionId, userId);
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
