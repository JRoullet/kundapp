package jroullet.mscoursemgmt.dto.participant;

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
