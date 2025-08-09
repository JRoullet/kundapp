package jroullet.mswebapp.dto.session.participant;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public record ParticipantOperationResponse(
        Long sessionId,
        Long userId,
        String operation,
        LocalDateTime operationTimestamp,
        Integer currentParticipantCount,
        Integer availableSpots,
        List<Long> participantIds
) {}
