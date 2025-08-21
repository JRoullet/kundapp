package jroullet.mswebapp.dto.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * User information for notifications
 * Réutilise UserParticipantDTO de ms-identity (même structure)
 */
public record NotificationUserDto(
        @NotNull(message = "User ID is required")
        Long id,

        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
) {}
