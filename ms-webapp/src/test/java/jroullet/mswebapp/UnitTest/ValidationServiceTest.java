package jroullet.mswebapp.UnitTest;

import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.exception.InsufficientCreditsException;
import jroullet.mswebapp.exception.SessionNotAvailableException;
import jroullet.mswebapp.exception.UnauthorizedSessionAccessException;
import jroullet.mswebapp.exception.UserAlreadyRegisteredException;
import jroullet.mswebapp.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static jroullet.mswebapp.UnitTest.TestDataBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

    @InjectMocks
    private ValidationService validationService;

    // Test data
    private static final Long TEACHER_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long SESSION_ID = 5L;
    private static final Integer SUFFICIENT_CREDITS = 5;
    private static final Integer INSUFFICIENT_CREDITS = 1;
    private static final Integer CREDITS_REQUIRED = 3;


    /**    * validateUserHasSufficientCredits:
     * - should pass when user has enough credits
     * - should throw InsufficientCreditsException when not enough credits
     */
    @Test
    void validateUserHasSufficientCreditsTest_shouldPassWhenUserHasEnoughCredits() {
        // Given
        UserDTO user = createUserDTOTestUser(USER_ID, SUFFICIENT_CREDITS);

        // When/Then - No exception should be thrown
        assertDoesNotThrow(() ->
                validationService.validateUserHasSufficientCredits(user, CREDITS_REQUIRED)
        );
    }

    @Test
    void validateUserHasSufficientCreditsTest_shouldThrowExceptionWhenInsufficientCredits() {
        // Given
        UserDTO user = createUserDTOTestUser(USER_ID, INSUFFICIENT_CREDITS);

        // When/Then
        InsufficientCreditsException exception = assertThrows(
                InsufficientCreditsException.class,
                () -> validationService.validateUserHasSufficientCredits(user, CREDITS_REQUIRED)
        );

        assertEquals(USER_ID, exception.getUserId());
        assertEquals(INSUFFICIENT_CREDITS, exception.getAvailableCredits());
        assertEquals(CREDITS_REQUIRED, exception.getRequiredCredits());
    }

    /** validateSessionAvailability:
     * - should pass when session has available spots
     * - should throw SessionNotAvailableException when session is full
     */
    @Test
    void validateSessionAvailabilityTest_shouldPassWhenSessionHasSpots() {
        // Given
        SessionWithParticipantsDTO session = createSessionWithParticipantsDTOWithANumberOfParticipants(2, 5);

        // When/Then
        assertDoesNotThrow(() ->
                validationService.validateSessionAvailability(session)
        );
    }

    @Test
    void validateSessionAvailabilityTest_shouldThrowExceptionWhenSessionIsFull() {
        // Given
        SessionWithParticipantsDTO session = createSessionWithParticipantsDTOWithANumberOfParticipants(5, 5); // 5 participants, 5 spots

        // When/Then
        SessionNotAvailableException exception = assertThrows(
                SessionNotAvailableException.class,
                () -> validationService.validateSessionAvailability(session)
        );

        assertEquals(SESSION_ID, exception.getSessionId());
        assertEquals(5, exception.getCurrentParticipants());
        assertEquals(5, exception.getMaxCapacity());
    }

    /** validateUserNotAlreadyRegistered:
     * - should pass when user is not registered
     * - should throw UserAlreadyRegisteredException when user is already registered
     */
    @Test
    void validateUserNotAlreadyRegisteredTest_shouldPassWhenUserNotRegistered() {
        // Given
        SessionWithParticipantsDTO session = createSessionWithParticipantsDTOWithSpecificParticipantsNoArguments();

        // When/Then
        assertDoesNotThrow(() ->
                validationService.validateUserNotAlreadyRegistered(session, USER_ID)
        );
    }

    @Test
    void validateUserNotAlreadyRegisteredTest_shouldThrowExceptionWhenUserAlreadyRegistered() {
        // Given
        SessionWithParticipantsDTO session = createSessionWithParticipantsDTOWithSpecificParticipants(List.of(USER_ID, 2L, 3L));

        // When/Then
        UserAlreadyRegisteredException exception = assertThrows(
                UserAlreadyRegisteredException.class,
                () -> validationService.validateUserNotAlreadyRegistered(session, USER_ID)
        );

        assertEquals(USER_ID, exception.getUserId());
        assertEquals(SESSION_ID, exception.getSessionId());
    }

    /** validateSessionOwnership:
     * - should pass when teacher owns the session
     * - should throw UnauthorizedSessionAccessException when teacher does not own the session
     */
    @Test
    void validateSessionOwnershipTest_shouldPassWhenTeacherOwnsSession() {
        // Given
        SessionWithParticipantsDTO session = createSessionWithParticipantsDTOWithTeacher(TEACHER_ID);

        // When/Then
        assertDoesNotThrow(() ->
                validationService.validateSessionOwnership(session, TEACHER_ID)
        );
    }

    @Test
    void validateSessionOwnershipTest_shouldThrowExceptionWhenTeacherDoesNotOwnSession() {
        // Given
        Long otherTeacherId = 111L;
        SessionWithParticipantsDTO session = createSessionWithParticipantsDTOWithTeacher(TEACHER_ID);

        // When/Then
        UnauthorizedSessionAccessException exception = assertThrows(
                UnauthorizedSessionAccessException.class,
                () -> validationService.validateSessionOwnership(session, otherTeacherId)
        );

        assertEquals("You can only access your own sessions", exception.getMessage());
    }

    /** Significant changes are:
     * - startDateTime
     * - description
     * - isOnline
     * - zoomLink (if isOnline)
     * - roomName (if not isOnline)
     */
    @Test
    void hasSignificantChangesTest_shouldReturnFalseWhenNoChanges() {
        // Given
        SessionWithParticipantsDTO original = createSessionWithNoParticipantsDTOBaseSession();
        SessionWithParticipantsDTO updated = createSessionWithNoParticipantsDTOBaseSession();

        // When/Then
        assertFalse(validationService.hasSignificantChanges(original, updated));
    }

    @Test
    void hasSignificantChangesTest_shouldReturnTrueWhenStartDateTimeChanged() {
        // Given
        SessionWithParticipantsDTO original = createSessionWithNoParticipantsDTOBaseSession();
        SessionWithParticipantsDTO updated = createSessionWithNoParticipantsDTOBaseSession();
        updated.setStartDateTime(LocalDateTime.now().plusHours(1));

        // When/Then
        assertTrue(validationService.hasSignificantChanges(original, updated));
    }

    @Test
    void hasSignificantChangesTest_shouldReturnTrueWhenDescriptionChanged() {
        // Given
        SessionWithParticipantsDTO original = createSessionWithNoParticipantsDTOBaseSession();
        SessionWithParticipantsDTO updated = createSessionWithNoParticipantsDTOBaseSession();
        updated.setDescription("New description");

        // When/Then
        assertTrue(validationService.hasSignificantChanges(original, updated));
    }

    @Test
    void hasSignificantChangesTest_shouldReturnTrueWhenOnlineModeChanged() {
        // Given
        SessionWithParticipantsDTO original = createSessionWithNoParticipantsDTOBaseSession();
        original.setIsOnline(true);
        SessionWithParticipantsDTO updated = createSessionWithNoParticipantsDTOBaseSession();
        updated.setIsOnline(false);

        // When/Then
        assertTrue(validationService.hasSignificantChanges(original, updated));
    }

    @Test
    void hasSignificantChangesTest_shouldReturnTrueWhenZoomLinkChangedForOnlineSession() {
        // Given
        SessionWithParticipantsDTO original = createSessionWithParticipantsDTOOnlineSession("https://zoom.us/old");
        SessionWithParticipantsDTO updated = createSessionWithParticipantsDTOOnlineSession("https://zoom.us/new");

        // When/Then
        assertTrue(validationService.hasSignificantChanges(original, updated));
    }

    @Test
    void hasSignificantChangesTest_shouldReturnTrueWhenRoomNameChangedForOfflineSession() {
        // Given
        SessionWithParticipantsDTO original = createSessionWithParticipantsDTOOfflineSession("Old Room");
        SessionWithParticipantsDTO updated = createSessionWithParticipantsDTOOfflineSession("New Room");

        // When/Then
        assertTrue(validationService.hasSignificantChanges(original, updated));
    }

}
