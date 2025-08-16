package jroullet.mswebapp.dto.session.credits;

import lombok.Builder;

import java.util.List;

@Builder
public record BatchCreditOperationRequest(
     Long sessionId,
     List<Long> participantIds,
     Integer creditsPerParticipant,
     String reason,
     String internalSecret
) {}
