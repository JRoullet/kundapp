package jroullet.mswebapp.UnitTest;

import jroullet.mswebapp.auth.RegisterRequestDTO;
import jroullet.mswebapp.auth.RegisterResponseDTO;
import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import jroullet.mswebapp.enums.Role;
import jroullet.mswebapp.enums.SessionStatus;
import jroullet.mswebapp.enums.Subject;
import jroullet.mswebapp.service.NotificationService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TestDataBuilders {

    // Constants for common test data
    public static final Long DEFAULT_TEACHER_ID = 1L;
    public static final Long DEFAULT_USER_ID = 2L;
    public static final Long DEFAULT_SESSION_ID = 5L;
    public static final LocalDateTime DEFAULT_START_TIME = LocalDateTime.of(2025, 1, 1, 10, 0);

    // ========================================
    // USER BUILDERS
    // ========================================

    public static UserDTO createUserDTOTestUser(Long id, Integer credits) {
        UserDTO user = new UserDTO();
        user.setId(DEFAULT_USER_ID);
        user.setEmail("some email");
        user.setFirstName("some firstname");
        user.setLastName("some lastname");
        user.setCredits(credits);
        user.setRole(Role.CLIENT);
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return user;
    }

    public static UserParticipantDTO createUserParticipantDTOTestUserWithArguments(Long id, String firstName, String lastName) {
        UserParticipantDTO user = new UserParticipantDTO();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.com");
        return user;
    }
    public static UserParticipantDTO createUserParticipantDTOTestUserNoArguments() {
        UserParticipantDTO user = new UserParticipantDTO();
        user.setId(DEFAULT_USER_ID);
        user.setFirstName("some user firstname");
        user.setLastName("some user lastname");
        user.setEmail("some user email");
        return user;
    }

    public static UserParticipantDTO createUserParticipantDTOTestTeacherWithArguments(Long id, String firstName, String lastName) {
        UserParticipantDTO user = new UserParticipantDTO();
        user.setId(DEFAULT_TEACHER_ID);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.com");
        return user;
    }
    public static UserParticipantDTO createUserParticipantDTOTestTeacherNoArguments() {
        UserParticipantDTO user = new UserParticipantDTO();
        user.setId(DEFAULT_TEACHER_ID);
        user.setFirstName("some teacher firstname");
        user.setLastName("some teacher lastname");
        user.setEmail("some teacher email");
        return user;
    }

    public static List<UserParticipantDTO> createUserParticipantDTOListTestUsers() {
        return List.of(
                createUserParticipantDTOTestUserWithArguments(3L, "John", "Doe"),
                createUserParticipantDTOTestUserWithArguments(4L, "Alice", "Johnson")
        );
    }
    // ========================================
    // SESSION BUILDERS
    // ========================================

    public static SessionWithParticipantsDTO createSessionWithNoParticipantsDTOBaseSession() {
        SessionWithParticipantsDTO session = new SessionWithParticipantsDTO();
        session.setId(DEFAULT_SESSION_ID);
        session.setSubject(Subject.YOGA);
        session.setStatus(SessionStatus.SCHEDULED);
        session.setDescription("Some description");
        session.setTeacherId(DEFAULT_TEACHER_ID);
        session.setTeacherFirstName("Teacher");
        session.setTeacherLastName("Test");
        session.setStartDateTime(DEFAULT_START_TIME);
        session.setDurationMinutes(60);
        session.setCreditsRequired(1);
        session.setAvailableSpots(10);
        session.setRegisteredParticipants(0);
        session.setIsOnline(false);
        session.setRoomName("Test Room");
        session.setBringYourMattress(false);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        return session;
    }

    public static SessionWithParticipantsDTO createSessionWithParticipantsDTOWithANumberOfParticipants(int participantCount, int availableSpots) {
        SessionWithParticipantsDTO session = createSessionWithNoParticipantsDTOBaseSession();
        session.setAvailableSpots(availableSpots);
        session.setRegisteredParticipants(participantCount);

        List<Long> participantIds = IntStream.range(1, participantCount + 1)
                .mapToLong(i -> (long) i)
                .boxed()
                .collect(toList());
        session.setParticipantIds(participantIds);

        return session;
    }

    public static SessionWithParticipantsDTO createSessionWithParticipantsDTOWithSpecificParticipants(List<Long> participantIds) {
        SessionWithParticipantsDTO session = createSessionWithNoParticipantsDTOBaseSession();
        session.setParticipantIds(participantIds);
        session.setRegisteredParticipants(participantIds.size());
        return session;
    }
    public static SessionWithParticipantsDTO createSessionWithParticipantsDTOWithSpecificParticipantsNoArguments() {
        SessionWithParticipantsDTO session = createSessionWithNoParticipantsDTOBaseSession();
        List<Long> participantIds = List.of(3L, 4L);
        session.setParticipantIds(participantIds);
        session.setRegisteredParticipants(participantIds.size());
        return session;
    }

    public static SessionWithParticipantsDTO createSessionWithParticipantsDTOWithTeacher(Long teacherId) {
        SessionWithParticipantsDTO session = createSessionWithNoParticipantsDTOBaseSession();
        session.setTeacherId(teacherId);
        return session;
    }

    public static SessionWithParticipantsDTO createSessionWithParticipantsDTOOnlineSession(String zoomLink) {
        SessionWithParticipantsDTO session = createSessionWithNoParticipantsDTOBaseSession();
        session.setIsOnline(true);
        session.setZoomLink(zoomLink);
        session.setRoomName(null);
        session.setGoogleMapsLink(null);
        session.setBringYourMattress(null);
        return session;
    }

    public static SessionWithParticipantsDTO createSessionWithParticipantsDTOOfflineSession(String roomName) {
        SessionWithParticipantsDTO session = createSessionWithNoParticipantsDTOBaseSession();
        session.setIsOnline(false);
        session.setRoomName(roomName);
        session.setZoomLink(null);
        return session;
    }


    public static RegisterRequestDTO createRegisterRequest() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("some email");
        request.setFirstName("some firstname");
        request.setLastName("some lastname");
        request.setPassword("some password");
        return request;
    }

    public static RegisterResponseDTO createRegisterResponse() {
        RegisterResponseDTO response = new RegisterResponseDTO();
        response.setId(DEFAULT_USER_ID);
        response.setEmail("some email");
        response.setFirstName("some firstname");
        response.setLastName("some lastname");
        response.setRole("CLIENT");
        return response;
    }
    /**
     * Setup NotificationService with internal secret for testing
     */
    public static void setupNotificationService(NotificationService service) {
        ReflectionTestUtils.setField(service, "internalSecret", "test-secret");
    }
}
