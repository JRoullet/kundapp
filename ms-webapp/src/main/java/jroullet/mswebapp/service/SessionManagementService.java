package jroullet.mswebapp.service;

import feign.FeignException;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.session.*;
import jroullet.mswebapp.dto.session.create.SessionCreationDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationResponseDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationWithTeacherDTO;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementService {

    private final IdentityFeignClient identityFeignClient;
    private final CourseManagementFeignClient courseFeignClient;
    private final SessionService sessionService;

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
            // Fetch available sessions from the course management service
            List<SessionNoParticipantsDTO> availableSessions = courseFeignClient.getAvailableSessionsForClient();
            log.info("Loaded {} available sessions for client", availableSessions.size());
            return availableSessions;
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


}
