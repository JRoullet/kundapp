package jroullet.mswebapp.UnitTest;

import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.enums.Role;
import jroullet.mswebapp.enums.SessionStatus;
import jroullet.mswebapp.enums.Subject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TestDataBuilders {

    // Constants for common test data
    public static final Long DEFAULT_USER_ID = 1L;
    public static final Long DEFAULT_SESSION_ID = 2L;
    public static final Long DEFAULT_TEACHER_ID = 3L;
    public static final LocalDateTime DEFAULT_START_TIME = LocalDateTime.of(2025, 1, 1, 10, 0);

    // ========================================
    // USER BUILDERS
    // ========================================

    public static UserDTO createUser(Long id, Integer credits) {
        UserDTO user = new UserDTO();
        user.setId(id);
        user.setEmail("user" + id + "@test.com");
        user.setFirstName("User");
        user.setLastName("Test");
        user.setCredits(credits);
        user.setRole(Role.CLIENT);
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return user;
    }

    // ========================================
    // SESSION BUILDERS
    // ========================================

    public static SessionWithParticipantsDTO createBaseSession() {
        SessionWithParticipantsDTO session = new SessionWithParticipantsDTO();
        session.setId(DEFAULT_SESSION_ID);
        session.setSubject(Subject.YOGA);
        session.setStatus(SessionStatus.SCHEDULED);
        session.setDescription("Base description");
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

    public static SessionWithParticipantsDTO createSessionWithParticipants(int participantCount, int availableSpots) {
        SessionWithParticipantsDTO session = createBaseSession();
        session.setAvailableSpots(availableSpots);
        session.setRegisteredParticipants(participantCount);

        List<Long> participantIds = IntStream.range(1, participantCount + 1)
                .mapToLong(i -> (long) i)
                .boxed()
                .collect(toList());
        session.setParticipantIds(participantIds);

        return session;
    }

    public static SessionWithParticipantsDTO createSessionWithSpecificParticipants(List<Long> participantIds) {
        SessionWithParticipantsDTO session = createBaseSession();
        session.setParticipantIds(participantIds);
        session.setRegisteredParticipants(participantIds.size());
        return session;
    }

    public static SessionWithParticipantsDTO createSessionWithTeacher(Long teacherId) {
        SessionWithParticipantsDTO session = createBaseSession();
        session.setTeacherId(teacherId);
        return session;
    }

    public static SessionWithParticipantsDTO createOnlineSession(String zoomLink) {
        SessionWithParticipantsDTO session = createBaseSession();
        session.setIsOnline(true);
        session.setZoomLink(zoomLink);
        session.setRoomName(null);
        session.setGoogleMapsLink(null);
        session.setBringYourMattress(null);
        return session;
    }

    public static SessionWithParticipantsDTO createOfflineSession(String roomName) {
        SessionWithParticipantsDTO session = createBaseSession();
        session.setIsOnline(false);
        session.setRoomName(roomName);
        session.setZoomLink(null);
        return session;
    }
}
