package jroullet.mswebapp.dto.notification.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jroullet.mswebapp.dto.notification.NotificationSessionDto;
import jroullet.mswebapp.dto.notification.NotificationUserDto;
import jroullet.mswebapp.enums.NotificationEventType;


import java.util.List;

/**
 * REQUEST : Notification events received from ms-webapp
 * Contains complete payload to avoid additional service calls
 */
public record NotificationEventRequest (

    @NotNull(message = "Event type is required")
    NotificationEventType eventType,

    @NotNull(message = "User information is required")
    @Valid
    NotificationUserDto user,

    @NotNull(message = "Session information is required")
    @Valid
    NotificationSessionDto session,

    /**
     * Additional participants for session-level events
     * Used when SESSION_CANCELLED or SESSION_MODIFIED
     */
     List<NotificationUserDto > additionalParticipants,

    @NotNull(message = "Internal secret is required")
    String internalSecret
){}
