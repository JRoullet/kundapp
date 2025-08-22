package jroullet.mswebapp.service;

import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.exception.InsufficientCreditsException;
import jroullet.mswebapp.exception.SessionNotAvailableException;
import jroullet.mswebapp.exception.UnauthorizedSessionAccessException;
import jroullet.mswebapp.exception.UserAlreadyRegisteredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class ValidationService {

    public void validateUserHasSufficientCredits(UserDTO user, Integer creditsRequired) {
        if (user.getCredits() < creditsRequired) {
            log.warn("User {} has insufficient credits. Available: {}, Required: {}",
                    user.getId(), user.getCredits(), creditsRequired);
            throw new InsufficientCreditsException(user.getId(), user.getCredits(), creditsRequired);
        }
    }
    public void validateSessionAvailability(SessionWithParticipantsDTO session) {
        int currentParticipants = session.getParticipantIds() != null ? session.getParticipantIds().size() : 0;
        int maxCapacity = session.getAvailableSpots();

        if (currentParticipants >= maxCapacity) {
            throw new SessionNotAvailableException(session.getId(), currentParticipants, maxCapacity);
        }
    }
    public void validateUserNotAlreadyRegistered(SessionWithParticipantsDTO session, Long userId) {
        if (session.getParticipantIds() != null && session.getParticipantIds().contains(userId)) {
            log.warn("User {} is already registered for session {}", userId, session.getId());
            throw new UserAlreadyRegisteredException(userId, session.getId());
        }
    }
    public void validateSessionOwnership(SessionWithParticipantsDTO session, Long teacherId) {
        if (!session.getTeacherId().equals(teacherId)) {
            throw new UnauthorizedSessionAccessException("You can only access your own sessions");
        }
    }
    public boolean hasSignificantChanges(SessionWithParticipantsDTO original, SessionWithParticipantsDTO updated) {
        if (!Objects.equals(original.getStartDateTime(), updated.getStartDateTime()) || !Objects.equals(original.getDescription(), updated.getDescription())) {
            return true;
        }

        if (!Objects.equals(original.getIsOnline(), updated.getIsOnline())) {
            return true;
        }

        if (!Objects.equals(original.getDurationMinutes(), updated.getDurationMinutes())) {
            return true;
        }

        if (Boolean.TRUE.equals(updated.getIsOnline())) {
            if (!Objects.equals(original.getZoomLink(), updated.getZoomLink())) {
                return true;
            }
        }

        if (Boolean.FALSE.equals(updated.getIsOnline())) {
            if (!Objects.equals(original.getRoomName(), updated.getRoomName())) {
                return true;
            }
            if (!Objects.equals(original.getGoogleMapsLink(), updated.getGoogleMapsLink())) {
                return true;
            }
            return !Objects.equals(original.getBringYourMattress(), updated.getBringYourMattress());
        }

        return false;
    }
}
