package jroullet.msnotification.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jroullet.msnotification.document.notificationSubObjects.NotificationRecipient;
import jroullet.msnotification.dto.NotificationSessionDto;
import jroullet.msnotification.dto.NotificationUserDto;
import jroullet.msnotification.enums.NotificationEventType;

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

    @NotNull(message = "Internal secret is required")
    String internalSecret,

    List<NotificationRecipient> additionalParticipants

){}
