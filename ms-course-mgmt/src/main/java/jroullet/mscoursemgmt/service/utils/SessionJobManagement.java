package jroullet.mscoursemgmt.service.utils;

import jroullet.mscoursemgmt.dto.session.SessionCreationWithTeacherDTO;
import jroullet.mscoursemgmt.dto.session.SessionWithParticipantsDTO;
import jroullet.mscoursemgmt.dto.session.SessionUpdateDTO;
import jroullet.mscoursemgmt.exception.*;
import jroullet.mscoursemgmt.mapper.SessionMapper;
import jroullet.mscoursemgmt.model.session.Session;
import jroullet.mscoursemgmt.model.session.SessionStatus;
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
     * Updates the status of sessions that have ended to 'COMPLETED'.
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
            throw new SessionStartingTimeException("Session start time cannot be in the past");
        }
        //Verify session is not overlapping another session
        List<Session> conflictingSessions = sessionRepository
                .findByTeacherIdAndStartDateTimeBetween(dto.getTeacherId(), sessionStart, sessionEnd);

        if (!conflictingSessions.isEmpty()) {
            throw new SessionOverlappingTimeException("A session already exists at this time for the teacher");
        }
    }

    /**
     * Session Update Method
     */

    public SessionWithParticipantsDTO updateSessionCommon(SessionWithParticipantsDTO sessionWithParticipantsDTO, SessionUpdateDTO dto) {

        if (sessionWithParticipantsDTO.getStatus() != SessionStatus.SCHEDULED) {
            throw new InvalidSessionStateException("Only scheduled sessions can be updated");
        }
        //Verify session starts in future
        if (dto.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new SessionStartingTimeException("Session start time cannot be in the past");
        }

        //Verify time conflicts with other sessions
        validateTimeConflicts(sessionWithParticipantsDTO.getTeacherId(), dto.getStartDateTime(), dto.getDurationMinutes(), sessionWithParticipantsDTO.getId());

        //Verify spots are not reduced below registered participants
        if (dto.getAvailableSpots() < (sessionWithParticipantsDTO.getRegisteredParticipants() != null ? sessionWithParticipantsDTO.getRegisteredParticipants() : 0)) {
            throw new InsufficientSpotsException("Cannot reduce spots below registered participants count");
        }
        //Verify credits are not modified when participants are registered
        validateCreditsModification(sessionWithParticipantsDTO, dto);

        Session session = sessionRepository.findById(sessionWithParticipantsDTO.getId())
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        // Mapping to entity and modifying it
        sessionMapper.updateSessionFromDto(dto, session);
        cleanConflictingSessionFields(session);

        // session object now contains sessionDTO data
        // Save changes in entity
        Session savedSession = sessionRepository.save(session);
        return sessionMapper.toDTO(savedSession);
    }

    private void validateTimeConflicts(Long teacherId, LocalDateTime startDateTime, Integer durationMinutes, Long excludeSessionId) {
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

        List<Session> conflictingSessions = sessionRepository
                .findByTeacherIdAndStartDateTimeBetween(teacherId, startDateTime, endDateTime)
                .stream()
                .filter(s -> !s.getId().equals(excludeSessionId))
                .filter(s -> s.getStatus() == SessionStatus.SCHEDULED)
                .toList();

        if (!conflictingSessions.isEmpty()) {
            throw new SessionOverlappingTimeException("Teacher has overlapping sessions at this time");
        }
    }

    private void cleanConflictingSessionFields(Session session) {
        // Clean fields that are not applicable based on session type
        if (session.getIsOnline()) {
            session.setRoomName(null);
            session.setPostalCode(null);
            session.setGoogleMapsLink(null);
            session.setBringYourMattress(null);
        } else {
            session.setZoomLink(null);
        }
    }

    private void validateCreditsModification(SessionWithParticipantsDTO currentSession, SessionUpdateDTO sessionUpdateDTO) {
        int participantsCount = currentSession.getParticipantIds() != null ?
                currentSession.getParticipantIds().size() : 0;

        if (participantsCount > 0 &&
                !currentSession.getCreditsRequired().equals(sessionUpdateDTO.getCreditsRequired())) {
            throw new InvalidSessionUpdateException(
                    "Cannot modify credits when participants are registered. Cancel session to change credits.");
        }
    }
}
