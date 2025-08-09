package jroullet.mscoursemgmt.service.impl;

import jroullet.mscoursemgmt.dto.participant.ParticipantOperationResponse;
import jroullet.mscoursemgmt.exception.*;
import jroullet.mscoursemgmt.model.session.Session;
import jroullet.mscoursemgmt.repository.SessionRepository;
import jroullet.mscoursemgmt.service.SessionParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionParticipantImplService implements SessionParticipantService {

    private final SessionRepository sessionRepository;
    private static final int CANCELLATION_CUTOFF_HOURS = 48;

    /**
     * Adds participant to session with business validations
     */
    @Transactional
    public ParticipantOperationResponse addParticipantToSession(Long sessionId, Long userId) {
        log.info("Adding participant {} to session {}", userId, sessionId);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with ID: " + sessionId));

        validateSessionNotFull(session);
        validateUserNotAlreadyRegistered(session, userId);

        // Initialize participantIds if null (for existing sessions)
        if (session.getParticipantIds() == null) {
            session.setParticipantIds(new ArrayList<>());
        }

        session.getParticipantIds().add(userId);
        sessionRepository.save(session);

        log.info("Participant {} successfully added to session {}. New count: {}/{}",
                userId, sessionId, session.getParticipantIds().size(), session.getAvailableSpots());

        return new ParticipantOperationResponse(
                sessionId,
                userId,
                "ADD_PARTICIPANT",
                LocalDateTime.now(),
                session.getParticipantIds().size(),
                session.getAvailableSpots(),
                new ArrayList<>(session.getParticipantIds())
        );
    }

    /**
     * Removes participant from session (for cancellation flow)
     */
    @Transactional
    public ParticipantOperationResponse removeParticipantFromSession(Long sessionId, Long userId) {
        log.info("Removing participant {} from session {}", userId, sessionId);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with ID: " + sessionId));

        validateCancellationDeadline(session);
        validateUserIsRegistered(session, userId);

        session.getParticipantIds().remove(userId);
        sessionRepository.save(session);

        log.info("Participant {} successfully removed from session {}. New count: {}/{}",
                userId, sessionId, session.getParticipantIds().size(), session.getAvailableSpots());

        return new ParticipantOperationResponse(
                sessionId,
                userId,
                "REMOVE_PARTICIPANT",
                LocalDateTime.now(),
                session.getParticipantIds().size(),
                session.getAvailableSpots(),
                new ArrayList<>(session.getParticipantIds())
        );
    }

    private void validateCancellationDeadline(Session session) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoffDateTime = session.getStartDateTime().minusHours(CANCELLATION_CUTOFF_HOURS);

        if (now.isAfter(cutoffDateTime)) {
            log.warn("Cancellation deadline passed for session {}. Cutoff: {}, Current: {}",
                    session.getId(), cutoffDateTime, now);
            throw new CancellationDeadlinePassedException(session.getId(), session.getStartDateTime(), now);
        }
    }

    private void validateSessionNotFull(Session session) {
        int currentCount = session.getParticipantIds() != null ? session.getParticipantIds().size() : 0;

        if (currentCount >= session.getAvailableSpots()) {
            log.warn("Session {} is full. Current: {}/{}",
                    session.getId(), currentCount, session.getAvailableSpots());
            throw new SessionFullException(session.getId(), currentCount, session.getAvailableSpots());
        }
    }

    private void validateUserNotAlreadyRegistered(Session session, Long userId) {
        if (session.getParticipantIds() != null && session.getParticipantIds().contains(userId)) {
            log.warn("User {} already registered for session {}", userId, session.getId());
            throw new UserAlreadyRegisteredException(userId, session.getId());
        }
    }

    private void validateUserIsRegistered(Session session, Long userId) {
        if (session.getParticipantIds() == null || !session.getParticipantIds().contains(userId)) {
            log.warn("User {} is not registered for session {}", userId, session.getId());
            throw new UserNotRegisteredException(userId, session.getId());
        }
    }
}
