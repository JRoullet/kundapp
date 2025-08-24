package jroullet.mswebapp.UnitTest;

import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.clients.NotificationFeignClient;
import jroullet.mswebapp.dto.notification.request.BulkNotificationEventRequest;
import jroullet.mswebapp.dto.notification.request.NotificationEventRequest;
import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import jroullet.mswebapp.service.NotificationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static jroullet.mswebapp.UnitTest.TestDataBuilders.createUserParticipantDTOListTestUsers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    private static final Long TEACHER_ID = 1L;
    private static final Long USER_ID = 2L;
    private static SessionWithParticipantsDTO testSession;
    private static UserParticipantDTO testTeacher;
    private static UserParticipantDTO testUser;

    @Mock
    private NotificationFeignClient notificationFeignClient;

    @Mock
    private IdentityFeignClient identityFeignClient;

    @InjectMocks
    NotificationService notificationService;

    @BeforeAll
    static void setUpAll(){
        testSession = TestDataBuilders.createSessionWithNoParticipantsDTOBaseSession();
        testTeacher = TestDataBuilders.createUserParticipantDTOTestTeacherNoArguments();
        testUser = TestDataBuilders.createUserParticipantDTOTestUserNoArguments();
    }

    private SessionWithParticipantsDTO createTestSessionWithParticipants() {
        return TestDataBuilders.createSessionWithParticipantsDTOWithANumberOfParticipants(2, 10); // 2 participants, 10 spots
    }


    @Test
    void sendUserEnrolledNotifications_shouldCallBothUserAndTeacherNotifications() {
        // Given

        when(identityFeignClient.getUserBasicInfo(USER_ID)).thenReturn(testUser);
        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendUserEnrolledNotifications(USER_ID, testSession);

        // Then
        verify(identityFeignClient).getUserBasicInfo(USER_ID);
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient, times(2)).processNotificationEvent(any(NotificationEventRequest.class));
    }

    @Test
    void sendUserCancelledNotifications_shouldCallBothUserAndTeacherNotifications() {
        // Given

        SessionWithParticipantsDTO session = createTestSessionWithParticipants();

        when(identityFeignClient.getUserBasicInfo(USER_ID)).thenReturn(testUser);
        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendUserCancelledNotifications(USER_ID, session);

        // Then
        verify(identityFeignClient).getUserBasicInfo(USER_ID);
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient, times(2)).processNotificationEvent(any(NotificationEventRequest.class));
    }

    //REFACTOR createParticipantsListTest
    @Test
    void sendSessionCancelledNotifications_shouldCallBulkAndTeacherNotifications() {
        // Given

        SessionWithParticipantsDTO session = createTestSessionWithParticipants();
        List<UserParticipantDTO> participants = createUserParticipantDTOListTestUsers();

        when(identityFeignClient.getUsersBasicInfo(anyList())).thenReturn(participants);
        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendSessionCancelledNotifications(session);

        // Then
        verify(identityFeignClient, times(2)).getUsersBasicInfo(anyList());
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
        verify(notificationFeignClient).processNotificationEvent(any(NotificationEventRequest.class));
    }

    @Test
    void sendSessionCancelledNotifications_withNoParticipants_shouldOnlyNotifyTeacher() {
        // Given

        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendSessionCancelledNotifications(testSession);

        // Then
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient).processNotificationEvent(any(NotificationEventRequest.class));
        verify(notificationFeignClient, times(0)).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
    }

    //REFACTOR createParticipantsListTest
    @Test
    void sendSessionModifiedNotifications_shouldCallBulkAndTeacherNotifications() {
        // Given

        SessionWithParticipantsDTO session = createTestSessionWithParticipants();
        String modificationSummary = "Date et heure, Description";
        List<UserParticipantDTO> participants = createUserParticipantDTOListTestUsers();

        when(identityFeignClient.getUsersBasicInfo(anyList())).thenReturn(participants);
        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendSessionModifiedNotifications(session, modificationSummary);

        // Then
        verify(identityFeignClient, times(2)).getUsersBasicInfo(anyList());
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
        verify(notificationFeignClient).processNotificationEvent(any(NotificationEventRequest.class));
    }

    @Test
    void sendSessionCreatedNotification_shouldCallTeacherNotification() {
        // Given

        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendSessionCreatedNotification(TEACHER_ID, testSession);

        // Then
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient).processNotificationEvent(any(NotificationEventRequest.class));
    }

    @Test
    void sendSessionCompletedNotifications_shouldCallBulkNotification() {
        // Given

        SessionWithParticipantsDTO session = createTestSessionWithParticipants();
        List<UserParticipantDTO> participants = createUserParticipantDTOListTestUsers();


        when(identityFeignClient.getUsersBasicInfo(anyList())).thenReturn(participants);

        // When
        notificationService.sendSessionCompletedNotifications(session);

        // Then
        verify(identityFeignClient).getUsersBasicInfo(anyList());
        verify(notificationFeignClient).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
    }

    @Test
    void sendSessionCompletedNotifications_withNoParticipants_shouldNotCallAnyNotification() {
        // Given

        // When
        notificationService.sendSessionCompletedNotifications(testSession);

        // Then
        verify(identityFeignClient, times(0)).getUsersBasicInfo(anyList());
        verify(notificationFeignClient, times(0)).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
    }

    @Test
    void buildModificationSummary_withNoChanges_shouldReturnNull() {
        // Given
        SessionWithParticipantsDTO original = testSession;
        SessionWithParticipantsDTO updated = testSession;

        // When
        String result = notificationService.buildModificationSummary(original, updated);

        // Then
        assertNull(result);
    }

    @Test
    void buildModificationSummaryTest_withChanges_shouldReturnCommaSeparatedString() {
        // Given
        SessionWithParticipantsDTO original = testSession;
        SessionWithParticipantsDTO updated = createTestSessionWithParticipants();
        updated.setDescription("Updated description");
        updated.setDurationMinutes(90);

        // When
        String result = notificationService.buildModificationSummary(original, updated);

        // Then
        assertEquals("Description, Durée", result);
    }




}
