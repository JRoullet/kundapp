package jroullet.mswebapp.dto.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jroullet.mswebapp.enums.Subject;

import java.time.LocalDateTime;

/**
 * Session information for notifications
 * Basé sur SessionNoParticipantsDTO + champs nécessaires pour emails
 */
public record NotificationSessionDto(
        @NotNull(message = "Session ID is required")
        Long id,

        @NotNull(message = "Subject is required")
        Subject subject,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Start date/time is required")
        LocalDateTime startDateTime,

        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be positive")
        Integer durationMinutes,

        // Teacher info
        @NotBlank(message = "Teacher first name is required")
        String teacherFirstName,

        @NotBlank(message = "Teacher last name is required")
        String teacherLastName,

        // Session type
        @NotNull(message = "Online flag is required")
        Boolean isOnline,

        // IRL session fields (when isOnline = false)
        String roomName,
        String postalCode,
        String googleMapsLink,
        Boolean bringYourMattress,

        // Online session fields (when isOnline = true)
        String zoomLink,

        // Additional info
        Integer creditsRequired
) {

    /**
     * Helper method - Get teacher full name
     */
    public String getTeacherFullName() {
        return (teacherFirstName + " " + teacherLastName).trim();
    }
}
