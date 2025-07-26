package jroullet.mscoursemgmt.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jroullet.mscoursemgmt.dto.SessionCancelDTO;
import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.dto.SessionDTO;
import jroullet.mscoursemgmt.dto.SessionUpdateDTO;
import jroullet.mscoursemgmt.exception.BusinessException;
import jroullet.mscoursemgmt.mapper.SessionMapper;
import jroullet.mscoursemgmt.model.Session;
import jroullet.mscoursemgmt.model.SessionStatus;
import jroullet.mscoursemgmt.repository.SessionRepository;
import jroullet.mscoursemgmt.service.SessionService;
import jroullet.mscoursemgmt.service.utils.SessionJobManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionImplService implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final SessionJobManagement sessionJobManagement;

    @Override
    public SessionDTO createSession(Long teacherId, SessionCreationDTO dto) {

        sessionJobManagement.validateSessionCreation(dto,teacherId);

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

        // Update completed sessions before fetching all sessions
        sessionJobManagement.updateCompletedSessions();

        return sessionRepository.findByTeacherIdAndStatusOrderByStartDateTimeAsc(teacherId, SessionStatus.SCHEDULED)
                .stream()
                .map(sessionMapper::toDTO)
                .collect(toList());
    }

    @Override
    public List<SessionDTO> getHistorySessionsByTeacher(Long teacherId) {

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
    public void cancelSession(SessionCancelDTO dto) {
        Session session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        // check ownership
        if (!session.getTeacherId().equals(dto.getTeacherId())) {
            throw new SecurityException("You can only cancel your own sessions");
        }

        // check session status
        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new BusinessException("Only scheduled sessions can be cancelled");
        }

        session.setStatus(SessionStatus.CANCELLED);
        sessionRepository.save(session);
    }

    @Override
    public SessionDTO getSessionById(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));
        return sessionMapper.toDTO(session);
    }


    @Override
    public List<SessionDTO> getAllSessionsForAdmin() {

        // Update completed sessions before fetching all sessions
        sessionJobManagement.updateCompletedSessions();

        List<Session> sessions = sessionRepository.findAllOrderByStartDateTimeDesc();
        return sessions.stream()
                .map(sessionMapper::toDTO)
                .collect(toList());
    }

    @Override
    public SessionDTO updateSessionByAdmin(Long sessionId, SessionUpdateDTO dto) {
        SessionDTO sessionDTO = getSessionById(sessionId);
        return sessionJobManagement.updateSessionCommon(sessionDTO,dto);
    }

    @Override
    public void cancelSessionByAdmin(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new BusinessException("Only scheduled sessions can be cancelled");
        }

        session.setStatus(SessionStatus.CANCELLED);

        sessionRepository.save(session);
    }

    @Override
    public SessionDTO updateSessionByTeacher(Long sessionId, Long requestingUserId, SessionUpdateDTO sessionUpdateDTO) {
        SessionDTO sessionDTO = getSessionById(sessionId);
        // Check if the session belongs to the requesting teacher
        if(!sessionDTO.getTeacherId().equals(requestingUserId)) {
           throw new SecurityException("You can only update your own sessions");
        }
        return sessionJobManagement.updateSessionCommon(sessionDTO,sessionUpdateDTO);
    }

}
