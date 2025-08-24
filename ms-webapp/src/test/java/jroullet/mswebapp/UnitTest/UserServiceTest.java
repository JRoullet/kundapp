package jroullet.mswebapp.UnitTest;

import feign.FeignException;
import jroullet.mswebapp.auth.RegisterRequestDTO;
import jroullet.mswebapp.auth.RegisterResponseDTO;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static jroullet.mswebapp.UnitTest.TestDataBuilders.createRegisterRequest;
import static jroullet.mswebapp.UnitTest.TestDataBuilders.createRegisterResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private IdentityFeignClient identityFeignClient;

    @InjectMocks
    private UserService userService;

    private static final Long USER_ID = 2L;

    @Test
    void registrationSuccess_shouldCallFeignClientTest() {
        // Given
        RegisterRequestDTO request = createRegisterRequest();
        RegisterResponseDTO response = createRegisterResponse();
        ResponseEntity<RegisterResponseDTO> responseEntity = ResponseEntity.ok(response);

        //Behaviour of mock
        when(identityFeignClient.registerUser(any(RegisterRequestDTO.class)))
                .thenReturn(responseEntity);

        // When
        userService.registration(request);

        // Then
        verify(identityFeignClient).registerUser(request);
    }

    @Test
    void registration_withFeignException_shouldRethrowFeignExceptionTest() {
        // Given
        RegisterRequestDTO request = createRegisterRequest();
        FeignException feignException = mock(FeignException.class);
        //Behaviour of mock
        when(feignException.status()).thenReturn(400);
        when(feignException.getMessage()).thenReturn("Bad request");

        when(identityFeignClient.registerUser(any(RegisterRequestDTO.class)))
                .thenThrow(feignException);

        // When & Then
        FeignException thrown = assertThrows(FeignException.class, () -> {
            userService.registration(request);
        });

        assertEquals(feignException, thrown);
        verify(identityFeignClient).registerUser(request);
    }

    @Test
    void registration_withUnexpectedException_shouldThrowRuntimeExceptionTest() {
        // Given
        RegisterRequestDTO request = createRegisterRequest();
        // No need to mock the exception, just create a new one
        RuntimeException unexpectedException = new RuntimeException("Database error");

        when(identityFeignClient.registerUser(any(RegisterRequestDTO.class)))
                .thenThrow(unexpectedException);

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.registration(request);
        });

        assertEquals("Erreur technique lors de l'inscription", thrown.getMessage());
        verify(identityFeignClient).registerUser(request);
    }

    @Test
    void getUserByIdSuccess_shouldReturnFeignClientResponseTest() {
        // Given
        UserDTO mockUser = TestDataBuilders.createUserDTOTestUser(USER_ID, 5);
        when(identityFeignClient.getUserById(USER_ID)).thenReturn(mockUser);

        // When
        UserDTO result = userService.getUserById(USER_ID);

        // Then
        verify(identityFeignClient).getUserById(USER_ID);
        assertEquals(mockUser, result);
    }

}
