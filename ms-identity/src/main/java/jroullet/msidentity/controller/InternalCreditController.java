package jroullet.msidentity.controller;

import jakarta.validation.Valid;
import jroullet.msidentity.dto.user.credits.*;
import jroullet.msidentity.service.InternalCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/internal/credits")
public class InternalCreditController {

    private final InternalCreditService internalCreditService;

    public InternalCreditController(InternalCreditService internalCreditService) {
        this.internalCreditService = internalCreditService;
    }

    /**
     * Deducts credits for session registration (internal microservice call only)
     */
    @PostMapping("/session-registration-deduct")
    public ResponseEntity<CreditOperationResponse> deductForSessionRegistration(
            @Valid @RequestBody SessionRegistrationDeductRequest request) {

        CreditOperationResponse response = internalCreditService
                .deductCreditsForSessionRegistration(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Refunds credits for failed session registration (rollback scenario)
     */
    @PostMapping("/session-rollback-refund")
    public ResponseEntity<CreditOperationResponse> refundForSessionRollback(
            @Valid @RequestBody SessionRollbackRefundRequest request) {

        CreditOperationResponse response = internalCreditService
                .refundCreditsForSessionRollback(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Batch operations for rollback and cancellation scenarios
     * This endpoint is used when a session is cancelled by a teacher and an error occurs while credits are refunded
     */
    @PostMapping("/batch-deduct")
    public ResponseEntity<Void> batchDeductCreditsForRollback(
            @Valid @RequestBody BatchCreditOperationRequest request) {
        try{
            internalCreditService.batchDeductCreditsForRollback(request);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            log.error("Batch deduct failed : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Batch refund for cancellation scenarios
     * This endpoint is used when a session is cancelled by a teacher and credits need to be refunded
     */
    @PostMapping("/batch-refund")
    public ResponseEntity<Void> batchRefundCreditsForCancellation(
            @Valid @RequestBody BatchCreditOperationRequest request) {
        try{
            internalCreditService.batchRefundCreditsForCancellation(request);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            log.error("Batch refund failed : {}", e.getMessage());
            throw e;
        }
    }

}
