package jroullet.mswebapp.service;

import feign.FeignException;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.dto.session.SessionCreationDTO;
import jroullet.mswebapp.dto.session.SessionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementService {

    private final CourseManagementFeignClient courseFeignClient;

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
            return courseFeignClient.getPastSessionsByTeacher(teacherId);
        } catch (FeignException e) {
            log.error("Error fetching past sessions for teacher {}: {}", teacherId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public SessionDTO createSessionForCurrentTeacher(Long teacherId, SessionCreationDTO dto) {
        try {
            return courseFeignClient.createSession(teacherId, dto);
        } catch (FeignException e) {
            log.error("Error creating session for teacher {}: {}", teacherId, e.getMessage());
            throw e; // Re-throw pour que le controller gère l'erreur spécifique
        }
    }
}
