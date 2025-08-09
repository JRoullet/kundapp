package jroullet.msidentity.controller;

import jakarta.validation.Valid;
import jroullet.msidentity.dto.user.credits.CreditOperationResponse;
import jroullet.msidentity.dto.user.credits.SessionRegistrationDeductRequest;
import jroullet.msidentity.dto.user.credits.SessionRollbackRefundRequest;
import jroullet.msidentity.service.InternalCreditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
