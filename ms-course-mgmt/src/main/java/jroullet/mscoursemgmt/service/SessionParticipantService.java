package jroullet.mscoursemgmt.service;

import jroullet.mscoursemgmt.dto.participant.ParticipantOperationResponse;

public interface SessionParticipantService {

    public ParticipantOperationResponse addParticipantToSession(Long sessionId, Long userId);
    public ParticipantOperationResponse removeParticipantFromSession(Long sessionId, Long userId);
}
