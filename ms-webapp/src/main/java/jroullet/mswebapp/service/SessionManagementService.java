package jroullet.mswebapp.service;

import feign.FeignException;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.session.AdminSessionDTO;
import jroullet.mswebapp.dto.session.SessionCancelDTO;
import jroullet.mswebapp.dto.session.SessionCreationDTO;
import jroullet.mswebapp.dto.session.SessionDTO;
import jroullet.mswebapp.dto.teacher.TeacherDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementService {

    private final IdentityFeignClient identityFeignClient;
    private final CourseManagementFeignClient courseFeignClient;
    private final SessionService sessionService;


    /**
     *     TEACHER METHODS
      */

    public List<SessionDTO> getUpcomingSessionsForCurrentTeacher(Long teacherId) {
        try {
            List<SessionDTO> upcomingSessions = courseFeignClient.getUpcomingSessionsByTeacher(teacherId);
            log.info("Loaded {} upcoming sessions for teacher ID: {}", upcomingSessions.size(), teacherId);
            return upcomingSessions;

        } catch (FeignException e) {
            log.error("Error fetching sessions from ms-course-mgmt for teacher {}: {}", teacherId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<SessionDTO> getPastSessionsForCurrentTeacher(Long teacherId) {
        try {
            List<SessionDTO> pastSessions = courseFeignClient.getPastSessionsByTeacher(teacherId);
            log.info("Loaded {} past sessions for teacher ID: {}", pastSessions.size(), teacherId);
            return pastSessions;
        } catch (FeignException e) {
            log.error("Error fetching past sessions for teacher {}: {}", teacherId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public SessionDTO createSessionForCurrentTeacher(Long teacherId, SessionCreationDTO dto) {
        try {
            SessionDTO session = courseFeignClient.createSession(teacherId, dto);
            log.info("Session created successfully for teacher ID: {}", teacherId);
            return session;
        } catch (FeignException e) {
            log.error("Error creating session for teacher {}: {}", teacherId, e.getMessage());
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

            courseFeignClient.cancelSession(dto);
            log.info("Session {} canceled successfully by teacher ID: {}", sessionId, currentUserId);
        } catch (FeignException e) {
            log.error("Error canceling session {} : {}", sessionId, e.getMessage());
            throw e;
        }
    }


    /**
     *     ADMIN METHODS
     */

    public List<AdminSessionDTO> getAllSessionsForAdmin() {
        List<SessionDTO> sessions = courseFeignClient.getAllSessionsForAdmin();
        log.info("Loaded {} sessions", sessions.size());
        return sessions.stream()
                .map(this::enrichWithTeacher)
                .collect(toList());
    }

    // Enrich session with teacher information for admin view and filtering
    private AdminSessionDTO enrichWithTeacher(SessionDTO session) {
        TeacherDTO teacher = identityFeignClient.getTeacherById(session.getTeacherId());
        return AdminSessionDTO.builder()
                .session(session)
                .teacherFirstName(teacher.getFirstName())
                .teacherLastName(teacher.getLastName())
                .build();
    }


    public void cancelSessionForAdmin(Long sessionId) {
        try {
            courseFeignClient.cancelSessionByAdmin(sessionId);
            log.info("Session {} canceled successfully", sessionId);
        } catch (FeignException e) {
            log.error("Error canceling session {} : {}", sessionId, e.getMessage());
            throw e;
        }
    }

    /**
     *      Common Methods
     */
    public List<UserParticipantDTO> getSessionParticipants(Long sessionId) {
        SessionDTO session = courseFeignClient.getSessionById(sessionId);
        if (session.getParticipantIds().isEmpty()) {
            return Collections.emptyList();
        }
        return identityFeignClient.getUsersByIds(session.getParticipantIds());
    }

}
