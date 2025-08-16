package jroullet.mscoursemgmt.service.impl;

import jakarta.transaction.Transactional;
import jroullet.mscoursemgmt.dto.session.*;
import jroullet.mscoursemgmt.exception.SessionNotFoundException;
import jroullet.mscoursemgmt.exception.InvalidSessionStateException;
import jroullet.mscoursemgmt.exception.UnauthorizedSessionAccessException;
import jroullet.mscoursemgmt.mapper.SessionMapper;
import jroullet.mscoursemgmt.model.session.Session;
import jroullet.mscoursemgmt.model.session.SessionStatus;
import jroullet.mscoursemgmt.repository.SessionRepository;
import jroullet.mscoursemgmt.service.SessionService;
import jroullet.mscoursemgmt.service.utils.SessionJobManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionImplService implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final SessionJobManagement sessionJobManagement;

    /**
     * Teacher part
     */
    @Override
    public SessionWithParticipantsDTO getSessionById(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with ID: " + sessionId));
        return sessionMapper.toDTO(session);
    }

    /**
     * Validate that session exists and can be reserved
     */
    @Override
    public Session validateSession(Long sessionId) {
        Optional<Session> sessionOpt = sessionRepository.findById(sessionId);

        if (sessionOpt.isEmpty()) {
            throw new SessionNotFoundException("Session not found with ID: " + sessionId);
        }

        Session session = sessionOpt.get();

        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new InvalidSessionStateException("Cannot reserve session with status: " + session.getStatus());
        }

        return session;
    }

    @Override
    public SessionCreationResponseDTO createSession(SessionCreationWithTeacherDTO dto) {

        sessionJobManagement.validateSessionCreation(dto);

        Session session = sessionMapper.toCreateEntity(dto);

        // Defining missing fields
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        session.setParticipantIds(new ArrayList<>());

        Session savedSession = sessionRepository.save(session);


        return SessionCreationResponseDTO.builder()
                .sessionId(savedSession.getId())
                .createdAt(savedSession.getCreatedAt())
                .build();
    }

    @Override
    public SessionWithParticipantsDTO updateSessionByTeacher(Long sessionId, Long requestingUserId, SessionUpdateDTO sessionUpdateDTO) {
        SessionWithParticipantsDTO sessionWithParticipantsDTO = getSessionById(sessionId);
        // Check if the session belongs to the requesting teacher
        if(!sessionWithParticipantsDTO.getTeacherId().equals(requestingUserId)) {
            throw new UnauthorizedSessionAccessException("You can only update your own sessions");
        }
        return sessionJobManagement.updateSessionCommon(sessionWithParticipantsDTO,sessionUpdateDTO);
    }

    @Override
    public List<SessionWithParticipantsDTO> getUpcomingSessionsByTeacher(Long teacherId) {

        // Update completed sessions before fetching all sessions
        sessionJobManagement.updateCompletedSessions();

        return sessionRepository.findByTeacherIdAndStatusOrderByStartDateTimeAsc(teacherId, SessionStatus.SCHEDULED)
                .stream()
                .map(sessionMapper::toDTO)
                .collect(toList());
    }

    @Override
    public List<SessionWithParticipantsDTO> getHistorySessionsByTeacher(Long teacherId) {

        // Update completed sessions before fetching all sessions
        sessionJobManagement.updateCompletedSessions();

        return sessionRepository.findByTeacherIdOrderByStartDateTimeDesc(teacherId)
                .stream()
                .filter(session -> session.getStatus() != SessionStatus.SCHEDULED)
                .map(sessionMapper::toDTO)
                .collect(toList());
    }

    @Override
    @Transactional
    public void cancelSessionByTeacher(SessionCancelDTO dto) {
        Session session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException("Session not found with ID: " + dto.getSessionId()));

        // check ownership
        if (!session.getTeacherId().equals(dto.getTeacherId())) {
            throw new UnauthorizedSessionAccessException("You can only cancel your own sessions");
        }

        // check session status
        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new InvalidSessionStateException("Only scheduled sessions can be cancelled");
        }

        session.setStatus(SessionStatus.CANCELLED);
        sessionRepository.save(session);
    }

    /**
     * Admin part
     */
    @Override
    public List<SessionWithParticipantsDTO> getAllSessionsForAdmin() {

        // Update completed sessions before fetching all sessions
        sessionJobManagement.updateCompletedSessions();

        List<Session> sessions = sessionRepository.findAllOrderByStartDateTimeDesc();
        return sessions.stream()
                .map(sessionMapper::toDTO)
                .collect(toList());
    }

    @Override
    public SessionWithParticipantsDTO updateSessionByAdmin(Long sessionId, SessionUpdateDTO dto) {
        SessionWithParticipantsDTO sessionWithParticipantsDTO = getSessionById(sessionId);
        return sessionJobManagement.updateSessionCommon(sessionWithParticipantsDTO,dto);
    }

    @Override
    public void cancelSessionByAdmin(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with ID: " + sessionId));

        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new InvalidSessionStateException("Only scheduled sessions can be cancelled");
        }

        session.setStatus(SessionStatus.CANCELLED);

        sessionRepository.save(session);
    }

    /**
     * Client part
     */
    @Override
    public List<SessionNoParticipantsDTO> getAvailableSessionsForClient() {
        LocalDateTime now = LocalDateTime.now();

        List<Session> sessions = sessionRepository
                .findByStatusOrderByStartDateTimeAsc(SessionStatus.SCHEDULED)
                .stream()
                .filter(session -> session.getStartDateTime().isAfter(now))
                .toList();

        return sessions.stream()
                .map(sessionMapper::toSessionGetClientResponseDTO)
                .toList();
    }

    @Override
    public List<SessionNoParticipantsDTO> getUpcomingSessionsForClient(Long participantId) {
        LocalDateTime now = LocalDateTime.now();

        List<Session> sessions = sessionRepository
                .findByParticipantIdOrderByStartDateTimeAsc(participantId)
                .stream()
                .filter(session -> session.getStartDateTime().isAfter(now))
                .toList();

        return sessions.stream()
                .map(sessionMapper::toSessionGetClientResponseDTO)
                .toList();
    }

    @Override
    public List<SessionNoParticipantsDTO> getPastSessionsForClient(Long participantId) {
        List<Session> sessions = sessionRepository.findByParticipantIdOrderByStartDateTimeDesc(participantId);
        return sessions.stream()
                .map(sessionMapper::toSessionGetClientResponseDTO)
                .collect(toList());
    }
}
