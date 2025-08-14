package jroullet.msidentity.service;

import jroullet.msidentity.dto.user.credits.CreditOperationResponse;
import jroullet.msidentity.dto.user.credits.SessionRegistrationDeductRequest;
import jroullet.msidentity.dto.user.credits.SessionRollbackRefundRequest;
import jroullet.msidentity.exception.UnauthorizedInternalAccessException;

public interface InternalCreditService {

    CreditOperationResponse deductCreditsForSessionRegistration(SessionRegistrationDeductRequest request);
    CreditOperationResponse refundCreditsForSessionRollback(SessionRollbackRefundRequest request);
    void validateInternalSecret(String internalSecret) throws UnauthorizedInternalAccessException;
}
