package jroullet.mscoursemgmt.service;

import jroullet.mscoursemgmt.dto.participant.ParticipantOperationResponse;

public interface SessionParticipantService {

    ParticipantOperationResponse addParticipantToSession(Long sessionId, Long userId);
    ParticipantOperationResponse removeParticipantFromSession(Long sessionId, Long userId);
}
