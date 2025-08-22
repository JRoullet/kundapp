package jroullet.mswebapp.UnitTest;

import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.session.credits.CreditOperationResponse;
import jroullet.mswebapp.service.CreditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreditServiceTest {

    @Mock
    private IdentityFeignClient identityFeignClient;

    @InjectMocks
    CreditService creditService;

    private static final Long USER_ID = 1L;
    private static final Long SESSION_ID = 2L;
    private static final Integer CREDITS = 1;

    // Only testing that the service calls the feign client and returns its response, request is
    @Test
    void deductCreditsTest_shouldReturnFeignClientResponse() {
        // Given
        CreditOperationResponse mockResponse = new CreditOperationResponse(
                USER_ID, 5, 4, "DEDUCT", SESSION_ID
        );
        when(identityFeignClient.deductCreditsForSessionRegistration(any()))
                .thenReturn(mockResponse);

        // When
        CreditOperationResponse result = creditService.deductCredits(USER_ID, SESSION_ID, CREDITS);

        // Then
        verify(identityFeignClient).deductCreditsForSessionRegistration(any());
        assertEquals(mockResponse, result);
    }

    @Test
    void refundCreditsTest_shouldCallFeignClient() {
        // Given
        CreditOperationResponse mockResponse = new CreditOperationResponse(
                USER_ID, 4, 5, "REFUND", SESSION_ID
        );
        when(identityFeignClient.refundCreditsForSessionRollback(any()))
                .thenReturn(mockResponse);

        // When
        CreditOperationResponse result = creditService.refundCredits(USER_ID, SESSION_ID, CREDITS);

        // Then
        verify(identityFeignClient).refundCreditsForSessionRollback(any());
        assertEquals(mockResponse, result);
    }

    @Test
    void batchRefundCreditsTest_shouldCallFeignClient() {
        // Given
        List<Long> participantIds = List.of(1L, 2L);

        // When
        creditService.batchRefundCredits(SESSION_ID, participantIds, CREDITS, "cancelled");

        // Then
        verify(identityFeignClient).batchRefundCreditsForCancellation(any());
    }

    @Test
    void batchRollbackCreditsTest_shouldCallFeignClient() {
        // Given
        List<Long> participantIds = List.of(1L, 2L);

        // When
        creditService.batchRollbackCredits(SESSION_ID, participantIds, CREDITS);

        // Then
        verify(identityFeignClient).batchDeductCreditsForRollback(any());
    }

}
