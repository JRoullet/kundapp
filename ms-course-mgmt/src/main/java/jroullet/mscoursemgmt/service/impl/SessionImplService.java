package jroullet.mscoursemgmt.service.impl;

import jakarta.transaction.Transactional;
import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.dto.SessionDTO;
import jroullet.mscoursemgmt.dto.SessionUpdateDTO;
import jroullet.mscoursemgmt.exception.SessionStartingTimeException;
import jroullet.mscoursemgmt.exception.SessionOverlappingTimeException;
import jroullet.mscoursemgmt.mapper.SessionMapper;
import jroullet.mscoursemgmt.model.Session;
import jroullet.mscoursemgmt.repository.SessionRepository;
import jroullet.mscoursemgmt.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionImplService implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final Logger logger = LoggerFactory.getLogger(SessionImplService.class);

    @Override
    public SessionDTO createSession(Long teacherId, SessionCreationDTO dto) {

        validateSessionCreation(dto,teacherId);

        Session session = sessionMapper.toEntity(dto);

        // Defining missing fields
        session.setTeacherId(teacherId);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        session.setParticipantIds(new ArrayList<>());

        Session savedSession = sessionRepository.save(session);

        return sessionMapper.toDTO(savedSession);
    }

    @Override
    public List<SessionDTO> getUpcomingSessionsByTeacher(Long teacherId) {
        LocalDateTime now = LocalDateTime.now();
        List<Session> upcomingSessions = sessionRepository
                .findByTeacherIdAndStartDateTimeAfterOrderByStartDateTimeAsc(teacherId, now);
        logger.info("Loaded {} sessions: {}", upcomingSessions.size(), upcomingSessions);
        return upcomingSessions.stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionDTO> getPastSessionsByTeacher(Long teacherId) {
        LocalDateTime now = LocalDateTime.now();
        List<Session> pastSessions = sessionRepository
                .findByTeacherIdAndStartDateTimeBeforeOrderByStartDateTimeDesc(teacherId, now);
        return pastSessions.stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionDTO> getAllSessionsByTeacher(Long teacherId) {
        List<Session> allSessions = sessionRepository
                .findByTeacherIdOrderByStartDateTimeDesc(teacherId);
        return allSessions.stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public SessionDTO updateSession(Long id, SessionUpdateDTO dto) {
        return null;
    }

    @Override
    public void deleteSession(Long id) {

    }

    // TODO : Migrate to Utils
    private void validateSessionCreation(SessionCreationDTO dto, Long teacherId) {
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
