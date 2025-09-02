package jroullet.mswebapp.UnitTest;

import feign.FeignException;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.session.SessionUpdateDTO;
import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationResponseDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationWithTeacherDTO;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import jroullet.mswebapp.exception.SessionCancellationException;
import jroullet.mswebapp.service.CreditService;
import jroullet.mswebapp.service.NotificationService;
import jroullet.mswebapp.service.SessionManagementService;
import jroullet.mswebapp.service.ValidationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static jroullet.mswebapp.UnitTest.TestDataBuilders.createSessionCreationDTO;
import static jroullet.mswebapp.UnitTest.TestDataBuilders.createSessionUpdateDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionManagementServiceTeacherTest {

    @Mock
    private CourseManagementFeignClient courseFeignClient;

    @Mock
    private IdentityFeignClient identityFeignClient;

    @Mock
    private SessionService sessionService;

    @Mock
    private CreditService creditService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private SessionManagementService sessionManagementService;

    private static final Long TEACHER_ID = TestDataBuilders.DEFAULT_TEACHER_ID;
    private static final Long SESSION_ID = TestDataBuilders.DEFAULT_SESSION_ID;
    private static UserDTO testTeacher;
    private static SessionWithParticipantsDTO testSession;
    private static SessionWithParticipantsDTO testSessionWithParticipants;

    @BeforeAll
    static void setUpAll() {
        testTeacher = TestDataBuilders.createUserDTOTestUser(TEACHER_ID, 10);
        testSession = TestDataBuilders.createSessionWithNoParticipantsDTOBaseSession();
        testSessionWithParticipants = TestDataBuilders.createSessionWithParticipantsDTOWithSpecificParticipantsNoArguments();
    }

    @Test
    void createSessionForCurrentTeacherSuccess_shouldCreateSessionAndSendNotificationTest() {
        // Given
        SessionCreationDTO creationDto = createSessionCreationDTO();
        SessionCreationResponseDTO responseDto = new SessionCreationResponseDTO();
        responseDto.setSessionId(SESSION_ID);

        when(sessionService.getCurrentUser()).thenReturn(testTeacher);
        when(courseFeignClient.createSession(any(SessionCreationWithTeacherDTO.class))).thenReturn(responseDto);
        when(courseFeignClient.getSessionById(SESSION_ID)).thenReturn(testSession);
        doNothing().when(notificationService).sendSessionCreatedNotification(anyLong(), any());

        // When
        SessionCreationResponseDTO result = sessionManagementService.createSessionForCurrentTeacher(creationDto);

        // Then
        verify(sessionService).getCurrentUser();
        verify(courseFeignClient).createSession(any(SessionCreationWithTeacherDTO.class));
        verify(courseFeignClient).getSessionById(SESSION_ID);
        verify(notificationService).sendSessionCreatedNotification(TEACHER_ID, testSession);
        assertEquals(SESSION_ID, result.getSessionId());
    }

    @Test
    void createSessionForCurrentTeacher_withFeignException_shouldRethrowExceptionTest() {
        // Given
        SessionCreationDTO creationDto = createSessionCreationDTO();
        FeignException feignException = mock(FeignException.class);

        when(sessionService.getCurrentUser()).thenReturn(testTeacher);
        when(courseFeignClient.createSession(any(SessionCreationWithTeacherDTO.class))).thenThrow(feignException);

        // When & Then
        FeignException thrown = assertThrows(FeignException.class, () -> {
            sessionManagementService.createSessionForCurrentTeacher(creationDto);
        });

        assertEquals(feignException, thrown);
        verify(courseFeignClient).createSession(any(SessionCreationWithTeacherDTO.class));
        verify(notificationService, never()).sendSessionCreatedNotification(anyLong(), any());
    }

    @Test
    void getUpcomingSessionsForCurrentTeacherSuccess_shouldReturnSessionsTest() {
        // Given
        List<SessionWithParticipantsDTO> expectedSessions = List.of(testSession);
        when(courseFeignClient.getUpcomingSessionsByTeacher(TEACHER_ID)).thenReturn(expectedSessions);

        // When
        List<SessionWithParticipantsDTO> result = sessionManagementService.getUpcomingSessionsForCurrentTeacher(TEACHER_ID);

        // Then
        verify(courseFeignClient).getUpcomingSessionsByTeacher(TEACHER_ID);
        assertEquals(expectedSessions, result);
    }

    @Test
    void getUpcomingSessionsForCurrentTeacher_withFeignException_shouldReturnEmptyListTest() {
        // Given
        FeignException feignException = mock(FeignException.class);
        when(courseFeignClient.getUpcomingSessionsByTeacher(TEACHER_ID)).thenThrow(feignException);

        // When
        List<SessionWithParticipantsDTO> result = sessionManagementService.getUpcomingSessionsForCurrentTeacher(TEACHER_ID);

        // Then
        verify(courseFeignClient).getUpcomingSessionsByTeacher(TEACHER_ID);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void getPastSessionsForCurrentTeacherSuccess_shouldReturnSessionsTest() {
        // Given
        List<SessionWithParticipantsDTO> expectedSessions = List.of(testSession);
        when(courseFeignClient.getPastSessionsByTeacher(TEACHER_ID)).thenReturn(expectedSessions);

        // When
        List<SessionWithParticipantsDTO> result = sessionManagementService.getPastSessionsForCurrentTeacher(TEACHER_ID);

        // Then
        verify(courseFeignClient).getPastSessionsByTeacher(TEACHER_ID);
        assertEquals(expectedSessions, result);
    }

    @Test
    void updateSessionForCurrentTeacherSuccess_shouldUpdateAndNotifyTest() {
        // Given
        SessionUpdateDTO updateDto = createSessionUpdateDTO();
        SessionWithParticipantsDTO updatedSession = TestDataBuilders.createSessionWithNoParticipantsDTOBaseSession();
        updatedSession.setDescription("Updated description");

        when(sessionService.getCurrentUser()).thenReturn(testTeacher);
        when(courseFeignClient.getSessionById(SESSION_ID)).thenReturn(testSession);
        when(courseFeignClient.updateSessionByTeacher(SESSION_ID, TEACHER_ID, updateDto)).thenReturn(updatedSession);
        when(validationService.hasSignificantChanges(testSession, updatedSession)).thenReturn(true);
        when(notificationService.buildModificationSummary(testSession, updatedSession)).thenReturn("Description");
        doNothing().when(validationService).validateSessionOwnership(testSession, TEACHER_ID);
        doNothing().when(notificationService).sendSessionModifiedNotifications(updatedSession, "Description");

        // When
        sessionManagementService.updateSessionForCurrentTeacher(SESSION_ID, updateDto);

        // Then
        verify(validationService).validateSessionOwnership(testSession, TEACHER_ID);
        verify(courseFeignClient).updateSessionByTeacher(SESSION_ID, TEACHER_ID, updateDto);
        verify(validationService).hasSignificantChanges(testSession, updatedSession);
        verify(notificationService).buildModificationSummary(testSession, updatedSession);
        verify(notificationService).sendSessionModifiedNotifications(updatedSession, "Description");
    }

    @Test
    void cancelSessionForCurrentTeacherSuccess_withNoParticipants_shouldCancelDirectlyTest() {
        // Given
        when(sessionService.getCurrentUser()).thenReturn(testTeacher);
        when(courseFeignClient.getSessionById(SESSION_ID)).thenReturn(testSession);
        doNothing().when(courseFeignClient).cancelSessionByTeacher(any());

        // When
        sessionManagementService.cancelSessionForCurrentTeacher(SESSION_ID);

        // Then
        verify(courseFeignClient).getSessionById(SESSION_ID);
        verify(courseFeignClient).cancelSessionByTeacher(any());
        verify(creditService, never()).batchRefundCredits(anyLong(), anyList(), any(), any());
        verify(notificationService, never()).sendSessionCancelledNotifications(any());
    }

    @Test
    void cancelSessionForCurrentTeacherSuccess_withParticipants_shouldRefundAndCancelTest() {
        // Given
        when(sessionService.getCurrentUser()).thenReturn(testTeacher);
        when(courseFeignClient.getSessionById(SESSION_ID)).thenReturn(testSessionWithParticipants);
        doNothing().when(creditService).batchRefundCredits(anyLong(), anyList(), any(), any());
        doNothing().when(courseFeignClient).cancelSessionByTeacher(any());
        doNothing().when(notificationService).sendSessionCancelledNotifications(any());

        // When
        sessionManagementService.cancelSessionForCurrentTeacher(SESSION_ID);

        // Then
        verify(courseFeignClient).getSessionById(SESSION_ID);
        verify(creditService).batchRefundCredits(SESSION_ID, testSessionWithParticipants.getParticipantIds(),
                testSessionWithParticipants.getCreditsRequired(), "SESSION_CANCELED_BY_TEACHER");
        verify(courseFeignClient).cancelSessionByTeacher(any());
        verify(notificationService).sendSessionCancelledNotifications(testSessionWithParticipants);
    }

    @Test
    void cancelSessionForCurrentTeacher_withRefundFailure_shouldThrowExceptionTest() {
        // Given
        FeignException refundException = mock(FeignException.class);
        when(sessionService.getCurrentUser()).thenReturn(testTeacher);
        when(courseFeignClient.getSessionById(SESSION_ID)).thenReturn(testSessionWithParticipants);
        doThrow(refundException).when(creditService).batchRefundCredits(anyLong(), anyList(), any(), any());

        // When & Then
        SessionCancellationException thrown = assertThrows(SessionCancellationException.class, () -> {
            sessionManagementService.cancelSessionForCurrentTeacher(SESSION_ID);
        });

        assertEquals("Failed to refund participants: " + refundException.getMessage(), thrown.getMessage());
        verify(courseFeignClient, never()).cancelSessionByTeacher(any());
    }

    @Test
    void getSessionParticipantsForTeacherSuccess_shouldReturnParticipantsTest() {
        // Given
        List<UserParticipantDTO> expectedParticipants = TestDataBuilders.createUserParticipantDTOListTestUsers();
        when(sessionService.getCurrentUser()).thenReturn(testTeacher);
        when(courseFeignClient.getSessionById(SESSION_ID)).thenReturn(testSessionWithParticipants);
        when(identityFeignClient.getParticipantsByIdsForTeacher(testSessionWithParticipants.getParticipantIds()))
                .thenReturn(expectedParticipants);
        doNothing().when(validationService).validateSessionOwnership(testSessionWithParticipants, TEACHER_ID);

        // When
        List<UserParticipantDTO> result = sessionManagementService.getSessionParticipantsForTeacher(SESSION_ID);

        // Then
        verify(validationService).validateSessionOwnership(testSessionWithParticipants, TEACHER_ID);
        verify(identityFeignClient).getParticipantsByIdsForTeacher(testSessionWithParticipants.getParticipantIds());
        assertEquals(expectedParticipants, result);
    }

    @Test
    void getSessionParticipantsForTeacher_withNoParticipants_shouldReturnEmptyListTest() {
        // Given
        when(sessionService.getCurrentUser()).thenReturn(testTeacher);
        when(courseFeignClient.getSessionById(SESSION_ID)).thenReturn(testSession);
        doNothing().when(validationService).validateSessionOwnership(testSession, TEACHER_ID);

        // When
        List<UserParticipantDTO> result = sessionManagementService.getSessionParticipantsForTeacher(SESSION_ID);

        // Then
        verify(validationService).validateSessionOwnership(testSession, TEACHER_ID);
        verify(identityFeignClient, never()).getParticipantsByIdsForTeacher(anyList());
        assertEquals(Collections.emptyList(), result);
    }
}
