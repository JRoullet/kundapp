package jroullet.mscoursemgmt.dto.session;

import jroullet.mscoursemgmt.model.session.SessionStatus;
import jroullet.mscoursemgmt.model.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionWithParticipantsDTO {

    private Long id;
    private Subject subject;
    private SessionStatus status;
    private String statusDisplay;
    private String description;

    // Teacher
    private Long teacherId;
    private String teacherFirstName;
    private String teacherLastName;

    // IRL session fields
    private String roomName;
    private String postalCode;
    private String googleMapsLink;

    // Online session fields
    private Boolean isOnline;
    private String zoomLink;

    // Time
    private LocalDateTime startDateTime;
    private Integer durationMinutes;

    // Credits
    private Integer creditsRequired;

    // Mattress (only for IRL sessions)
    private Boolean bringYourMattress;

    // Spots
    private Integer availableSpots;
    private Integer registeredParticipants;

    // Calculated states
    private LocalDateTime endDateTime;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Participants list
    private List<Long> participantIds;

}
