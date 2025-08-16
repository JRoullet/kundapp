package jroullet.msidentity.service;

import jroullet.msidentity.dto.user.credits.*;
import jroullet.msidentity.exception.UnauthorizedInternalAccessException;

public interface InternalCreditService {

    CreditOperationResponse deductCreditsForSessionRegistration(SessionRegistrationDeductRequest request);
    CreditOperationResponse refundCreditsForSessionRollback(SessionRollbackRefundRequest request);
    void batchDeductCreditsForRollback(BatchCreditOperationRequest request);
    void batchRefundCreditsForCancellation(BatchCreditOperationRequest request);

    void validateInternalSecret(String internalSecret) throws UnauthorizedInternalAccessException;
}
