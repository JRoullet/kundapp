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

    /** For each test, check TestDataBuilders for the data we used
     * BeforeAll sets up static test data like testSession, testTeacher, and testUser
     * Each test method uses Mockito to mock dependencies and verify interactions
     * */

    private static final Long TEACHER_ID = 1L;
    private static final Long USER_ID = 2L;
    private static SessionWithParticipantsDTO testSession;
    private static SessionWithParticipantsDTO testSessionWithParticipants;
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
        testSessionWithParticipants = TestDataBuilders.createSessionWithParticipantsDTOWithSpecificParticipantsNoArguments();
        testTeacher = TestDataBuilders.createUserParticipantDTOTestTeacherNoArguments();
        testUser = TestDataBuilders.createUserParticipantDTOTestUserNoArguments();
    }

    @Test
    void sendUserEnrolledNotifications_shouldCallBothUserAndTeacherNotificationsTest() {
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
    void sendUserCancelledNotifications_shouldCallBothUserAndTeacherNotificationsTest() {
        // Given

        when(identityFeignClient.getUserBasicInfo(USER_ID)).thenReturn(testUser);
        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendUserCancelledNotifications(USER_ID, testSessionWithParticipants);

        // Then
        verify(identityFeignClient).getUserBasicInfo(USER_ID);
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient, times(2)).processNotificationEvent(any(NotificationEventRequest.class));
    }

    @Test
    void sendSessionCancelledNotifications_shouldCallBulkAndTeacherNotificationsTest() {
        // Given

        List<UserParticipantDTO> participants = createUserParticipantDTOListTestUsers();

        when(identityFeignClient.getUsersBasicInfo(anyList())).thenReturn(participants);
        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendSessionCancelledNotifications(testSessionWithParticipants);

        // Then
        verify(identityFeignClient, times(2)).getUsersBasicInfo(anyList());
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
        verify(notificationFeignClient).processNotificationEvent(any(NotificationEventRequest.class));
    }

    @Test
    void sendSessionCancelledNotifications_withNoParticipants_shouldOnlyNotifyTeacherTest() {
        // Given

        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendSessionCancelledNotifications(testSession);

        // Then
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient).processNotificationEvent(any(NotificationEventRequest.class));
        verify(notificationFeignClient, times(0)).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
    }

    @Test
    void sendSessionModifiedNotifications_shouldCallBulkAndTeacherNotificationsTest() {
        // Given

        String modificationSummary = "Date et heure, Description";
        List<UserParticipantDTO> participants = createUserParticipantDTOListTestUsers();

        when(identityFeignClient.getUsersBasicInfo(anyList())).thenReturn(participants);
        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendSessionModifiedNotifications(testSessionWithParticipants, modificationSummary);

        // Then
        verify(identityFeignClient, times(2)).getUsersBasicInfo(anyList());
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
        verify(notificationFeignClient).processNotificationEvent(any(NotificationEventRequest.class));
    }

    @Test
    void sendSessionCreatedNotification_shouldCallTeacherNotificationTest() {
        // Given

        when(identityFeignClient.getUserBasicInfo(TEACHER_ID)).thenReturn(testTeacher);

        // When
        notificationService.sendSessionCreatedNotification(TEACHER_ID, testSession);

        // Then
        verify(identityFeignClient).getUserBasicInfo(TEACHER_ID);
        verify(notificationFeignClient).processNotificationEvent(any(NotificationEventRequest.class));
    }

    @Test
    void sendSessionCompletedNotifications_shouldCallBulkNotificationTest() {
        // Given

        List<UserParticipantDTO> participants = createUserParticipantDTOListTestUsers();


        when(identityFeignClient.getUsersBasicInfo(anyList())).thenReturn(participants);

        // When
        notificationService.sendSessionCompletedNotifications(testSessionWithParticipants);

        // Then
        verify(identityFeignClient).getUsersBasicInfo(anyList());
        verify(notificationFeignClient).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
    }

    @Test
    void sendSessionCompletedNotifications_withNoParticipants_shouldNotCallAnyNotificationTest() {
        // Given

        // When
        notificationService.sendSessionCompletedNotifications(testSession);

        // Then
        verify(identityFeignClient, times(0)).getUsersBasicInfo(anyList());
        verify(notificationFeignClient, times(0)).processBulkNotificationEvent(any(BulkNotificationEventRequest.class));
    }

    @Test
    void buildModificationSummary_withNoChanges_shouldReturnNullTest() {
        // Given
        SessionWithParticipantsDTO original = testSession;
        SessionWithParticipantsDTO updated = testSession;

        // When
        String result = notificationService.buildModificationSummary(original, updated);

        // Then
        assertNull(result);
    }

    @Test
    void buildModificationSummary_withChanges_shouldReturnCommaSeparatedStringTest() {
        // Given
        SessionWithParticipantsDTO original = testSession;
        SessionWithParticipantsDTO updated = testSessionWithParticipants;
        updated.setDescription("Updated description");
        updated.setDurationMinutes(90);

        // When
        String result = notificationService.buildModificationSummary(original, updated);

        // Then
        assertEquals("Description, Durée", result);
    }

}
