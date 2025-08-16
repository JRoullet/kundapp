package jroullet.msidentity.service.impl;

import jroullet.msidentity.dto.user.credits.*;
import jroullet.msidentity.exception.InsufficientCreditsException;
import jroullet.msidentity.exception.UnauthorizedInternalAccessException;
import jroullet.msidentity.exception.UserNotFoundException;
import jroullet.msidentity.model.User;
import jroullet.msidentity.repository.UserRepository;
import jroullet.msidentity.service.InternalCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InternalCreditServiceImpl implements InternalCreditService {

    private final UserRepository userRepository;
    private final String internalSecret;

    public InternalCreditServiceImpl(UserRepository userRepository,
                                 @Value("${app.internal.secret}") String internalSecret) {
        this.userRepository = userRepository;
        this.internalSecret = internalSecret;
    }

    /**
     * Deducts credits for session registration with security validation
     */
    @Transactional
    public CreditOperationResponse deductCreditsForSessionRegistration(SessionRegistrationDeductRequest request) {
        log.info("Processing credit deduction for user {} and session {}",
                request.userId(), request.sessionId());

        validateInternalSecret(request.internalSecret());

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.userId()));

        Integer previousCredits = user.getCredits();

        if (previousCredits < request.creditsRequired()) {
            log.warn("Insufficient credits for user {}. Available: {}, Required: {}",
                    request.userId(), previousCredits, request.creditsRequired());
            throw new InsufficientCreditsException(request.userId(), previousCredits, request.creditsRequired());
        }

        Integer newCredits = previousCredits - request.creditsRequired();
        user.setCredits(newCredits);
        userRepository.save(user);

        log.info("Credits deducted successfully for user {}. Previous: {}, New: {}",
                request.userId(), previousCredits, newCredits);

        return new CreditOperationResponse(
                request.userId(),
                previousCredits,
                newCredits,
                "SESSION_REGISTRATION_DEDUCT",
                request.sessionId()
        );
    }

    /**
     * Refunds credits after failed session registration (rollback)
     */
    @Transactional
    public CreditOperationResponse refundCreditsForSessionRollback(SessionRollbackRefundRequest request) {
        log.info("Processing credit rollback refund for user {} and session {}",
                request.userId(), request.sessionId());

        validateInternalSecret(request.internalSecret());

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.userId()));

        Integer previousCredits = user.getCredits();
        Integer newCredits = previousCredits + request.creditsToRefund();

        user.setCredits(newCredits);
        userRepository.save(user);

        log.info("Credits refunded successfully for user {}. Previous: {}, New: {}",
                request.userId(), previousCredits, newCredits);

        return new CreditOperationResponse(
                request.userId(),
                previousCredits,
                newCredits,
                "SESSION_ROLLBACK_REFUND",
                request.sessionId()
        );
    }

    @Transactional
    public void batchDeductCreditsForRollback(BatchCreditOperationRequest request) {
        log.info("Processing batch credit deduction for {} users and session {}",
                request.participantIds().size(), request.sessionId());

        validateInternalSecret(request.internalSecret());

        List<Long> participantIds = request.participantIds();

        for (Long participantId : participantIds) {
            User user = userRepository.findById(participantId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + participantId));

            user.setCredits(user.getCredits() - request.creditsPerParticipant());
            userRepository.save(user);
        }

        log.info("Credits deducted successfully for {} users", participantIds.size());
    }

    @Transactional
    public void batchRefundCreditsForCancellation(BatchCreditOperationRequest request) {
        log.info("Processing batch credit refund for {} users and session {}",
                request.participantIds().size(), request.sessionId());

        validateInternalSecret(request.internalSecret());

        for (Long participantId : request.participantIds()) {
            User user = userRepository.findById(participantId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + participantId));

            user.setCredits(user.getCredits() + request.creditsPerParticipant());
            userRepository.save(user);
        }

        log.info("Credits refunded successfully for {} users", request.participantIds().size());
    }

    public void validateInternalSecret(String providedSecret) {
        if (!internalSecret.equals(providedSecret)) {
            log.warn("Invalid internal secret provided for credit operation");
            throw new UnauthorizedInternalAccessException("Unauthorized internal access: Invalid secret provided");
        }
    }
}

