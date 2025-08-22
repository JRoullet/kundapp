package jroullet.mswebapp.service;

import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.session.credits.BatchCreditOperationRequest;
import jroullet.mswebapp.dto.session.credits.CreditOperationResponse;
import jroullet.mswebapp.dto.session.credits.SessionRegistrationDeductRequest;
import jroullet.mswebapp.dto.session.credits.SessionRollbackRefundRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {

    private final IdentityFeignClient identityFeignClient;

    @Value("${app.internal.secret}")
    private String internalSecret;

    public CreditOperationResponse deductCredits(Long userId, Long sessionId, Integer credits) {
        SessionRegistrationDeductRequest request = new SessionRegistrationDeductRequest(
                userId, sessionId, credits, internalSecret);
        return identityFeignClient.deductCreditsForSessionRegistration(request);
    }

    public CreditOperationResponse refundCredits(Long userId, Long sessionId, Integer credits) {
        SessionRollbackRefundRequest request = new SessionRollbackRefundRequest(
                userId, sessionId, credits, internalSecret);
        return identityFeignClient.refundCreditsForSessionRollback(request);
    }

    public void batchRefundCredits(Long sessionId, List<Long> participantIds, Integer creditsPerParticipant, String reason) {
        BatchCreditOperationRequest request = BatchCreditOperationRequest.builder()
                .sessionId(sessionId)
                .participantIds(participantIds)
                .creditsPerParticipant(creditsPerParticipant)
                .reason(reason)
                .internalSecret(internalSecret)
                .build();
        identityFeignClient.batchRefundCreditsForCancellation(request);
    }

    public void batchRollbackCredits(Long sessionId, List<Long> participantIds, Integer creditsPerParticipant) {
        BatchCreditOperationRequest request = BatchCreditOperationRequest.builder()
                .sessionId(sessionId)
                .participantIds(participantIds)
                .creditsPerParticipant(creditsPerParticipant)
                .reason("ROLLBACK_REFUND_AFTER_CANCELLATION_FAILURE")
                .internalSecret(internalSecret)
                .build();
        identityFeignClient.batchDeductCreditsForRollback(request);
    }
}
