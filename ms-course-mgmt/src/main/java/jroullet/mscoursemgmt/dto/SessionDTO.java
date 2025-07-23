package jroullet.mscoursemgmt.dto;

import jroullet.mscoursemgmt.model.SessionStatus;
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
public class SessionDTO {

    private Long id;
    private Long teacherId;
    private Subject subject;
    private SessionStatus status;
    private String statusDisplay;
    private String description;

    // Room
    private String roomName;
    private String postalCode;
    private String googleMapsLink;

    // Time
    private LocalDateTime startDateTime;
    private Integer durationMinutes;

    // Credits
    private Integer creditsRequired;

    //Mattress
    private Boolean bringYourMattress;

    // Spots
    private Integer availableSpots;
    private Integer registeredParticipants;

    // Calculated states
    private LocalDateTime endDateTime;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Participants list is for the teacher
    private List<Long> participantIds;
}
