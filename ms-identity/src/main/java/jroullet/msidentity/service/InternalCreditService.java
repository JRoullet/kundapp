package jroullet.msidentity.service;

import jroullet.msidentity.dto.user.credits.CreditOperationResponse;
import jroullet.msidentity.dto.user.credits.SessionRegistrationDeductRequest;
import jroullet.msidentity.dto.user.credits.SessionRollbackRefundRequest;
import jroullet.msidentity.exception.UnauthorizedInternalAccessException;

public interface InternalCreditService {

    public CreditOperationResponse deductCreditsForSessionRegistration(SessionRegistrationDeductRequest request);
    public CreditOperationResponse refundCreditsForSessionRollback(SessionRollbackRefundRequest request);
    public void validateInternalSecret(String internalSecret) throws UnauthorizedInternalAccessException;
}
