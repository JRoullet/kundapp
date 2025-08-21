package jroullet.msidentity.service;

import jroullet.msidentity.dto.user.credits.BatchCreditOperationRequest;
import jroullet.msidentity.dto.user.credits.CreditOperationResponse;
import jroullet.msidentity.dto.user.credits.SessionRegistrationDeductRequest;
import jroullet.msidentity.dto.user.credits.SessionRollbackRefundRequest;

public interface InternalCreditService {

    CreditOperationResponse deductCreditsForSessionRegistration(SessionRegistrationDeductRequest request);
    CreditOperationResponse refundCreditsForSessionRollback(SessionRollbackRefundRequest request);
    void batchDeductCreditsForRollback(BatchCreditOperationRequest request);
    void batchRefundCreditsForCancellation(BatchCreditOperationRequest request);
}
