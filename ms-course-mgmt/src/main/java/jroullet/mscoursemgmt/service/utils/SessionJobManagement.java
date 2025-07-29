package jroullet.mscoursemgmt.service.utils;

import jakarta.persistence.EntityNotFoundException;
import jroullet.mscoursemgmt.dto.SessionCreationWithTeacherDTO;
import jroullet.mscoursemgmt.dto.SessionWithParticipantsDTO;
import jroullet.mscoursemgmt.dto.SessionUpdateDTO;
import jroullet.mscoursemgmt.exception.BusinessException;
import jroullet.mscoursemgmt.exception.SessionOverlappingTimeException;
import jroullet.mscoursemgmt.exception.SessionStartingTimeException;
import jroullet.mscoursemgmt.mapper.SessionMapper;
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
    private final SessionMapper sessionMapper;

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

    /**
     * Session Creation
     */
    public void validateSessionCreation(SessionCreationWithTeacherDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sessionStart = dto.getStartDateTime();
        LocalDateTime sessionEnd = sessionStart.plusMinutes(dto.getDurationMinutes());

        //Verify session is set on a possible day
        if(sessionStart.isBefore(now)) {
            throw new SessionStartingTimeException("La session ne peut pas être dans le passé");
        }
        //Verify session is not overlapping another session
        List<Session> conflictingSessions = sessionRepository
                .findByTeacherIdAndStartDateTimeBetween(dto.getTeacherId(), sessionStart, sessionEnd);

        if (!conflictingSessions.isEmpty()) {
            throw new SessionOverlappingTimeException("Vous avez déjà une session programmée à ces horaires");
        }
    }

    /**
     * Session Update Methods
     */

    public SessionWithParticipantsDTO updateSessionCommon(SessionWithParticipantsDTO sessionWithParticipantsDTO, SessionUpdateDTO dto) {

        if (sessionWithParticipantsDTO.getStatus() != SessionStatus.SCHEDULED) {
            throw new BusinessException("Only scheduled sessions can be updated");
        }
        //Verify session starts in future
        if (dto.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Session start time cannot be in the past");
        }

        //Verify time conflicts with other sessions
        validateTimeConflicts(sessionWithParticipantsDTO.getTeacherId(), dto.getStartDateTime(), dto.getDurationMinutes(), sessionWithParticipantsDTO.getId());

        //Verify spots are not reduced below registered participants
        if (dto.getAvailableSpots() < (sessionWithParticipantsDTO.getRegisteredParticipants() != null ? sessionWithParticipantsDTO.getRegisteredParticipants() : 0)) {
            throw new BusinessException("Cannot reduce spots below registered participants count");
        }

        Session session = sessionRepository.findById(sessionWithParticipantsDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        // Mapping to entity and modifying it
        sessionMapper.updateSessionFromDto(dto, session);

        // session object now contains sessionDTO data
        // Save changes in entity
        Session savedSession = sessionRepository.save(session);
        return sessionMapper.toDTO(savedSession);
    }

    public void validateTimeConflicts(Long teacherId, LocalDateTime startDateTime, Integer durationMinutes, Long excludeSessionId) {
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

        List<Session> conflictingSessions = sessionRepository
                .findByTeacherIdAndStartDateTimeBetween(teacherId, startDateTime, endDateTime)
                .stream()
                .filter(s -> !s.getId().equals(excludeSessionId))
                .filter(s -> s.getStatus() == SessionStatus.SCHEDULED)
                .toList();

        if (!conflictingSessions.isEmpty()) {
            throw new BusinessException("Teacher has overlapping sessions at this time");
        }
    }
}
