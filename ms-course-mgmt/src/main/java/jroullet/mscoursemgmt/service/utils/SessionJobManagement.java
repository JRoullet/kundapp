package jroullet.mscoursemgmt.service.utils;

import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.exception.SessionOverlappingTimeException;
import jroullet.mscoursemgmt.exception.SessionStartingTimeException;
import jroullet.mscoursemgmt.model.Session;
import jroullet.mscoursemgmt.model.SessionStatus;
import jroullet.mscoursemgmt.repository.SessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@AllArgsConstructor
public class SessionJobManagement {

    private final SessionRepository sessionRepository;

    /**
     * Updates the status of sessions that have ended to COMPLETED.
     * This method should is called periodically (e.g., via a scheduled job).
     */
    public void updateCompletedSessions() {
        LocalDateTime now = LocalDateTime.now();
        List<Session> sessionsToComplete = sessionRepository.findAll().stream()
                .filter(s -> s.getStatus() == SessionStatus.SCHEDULED)
                .filter(s -> s.getStartDateTime().plusMinutes(s.getDurationMinutes()).isBefore(now))
                .collect(toList());

        sessionsToComplete.forEach(s -> s.setStatus(SessionStatus.COMPLETED));
        if (!sessionsToComplete.isEmpty()) {
            sessionRepository.saveAll(sessionsToComplete);
        }
    }

    // TODO : Migrate to Utils
    public void validateSessionCreation(SessionCreationDTO dto, Long teacherId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sessionStart = dto.getStartDateTime();
        LocalDateTime sessionEnd = sessionStart.plusMinutes(dto.getDurationMinutes());

        //Verify session is set on a possible day
        if(sessionStart.isBefore(now)) {
            throw new SessionStartingTimeException("La session ne peut pas être dans le passé");
        }
        //Verify session is not overlapping another session
        List<Session> conflictingSessions = sessionRepository
                .findByTeacherIdAndStartDateTimeBetween(teacherId, sessionStart, sessionEnd);

        if (!conflictingSessions.isEmpty()) {
            throw new SessionOverlappingTimeException("Vous avez déjà une session programmée à ces horaires");
        }
    }
}
