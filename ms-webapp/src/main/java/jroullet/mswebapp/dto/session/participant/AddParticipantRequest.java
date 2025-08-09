package jroullet.mswebapp.dto.session.participant;

import jakarta.validation.constraints.NotNull;

public record AddParticipantRequest(
        @NotNull(message = "User ID is required")
        Long userId
) {}
