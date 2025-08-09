package jroullet.mscoursemgmt.dto.session;

import jroullet.mscoursemgmt.model.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionNoParticipantsDTO {
    private Long            id;
    private Subject         subject;
    private String          description;
    private LocalDateTime   startDateTime;
    private Integer         durationMinutes;
    private Integer         creditsRequired;
    private Boolean         bringYourMattress;

    // IRL
    private String roomName;
    private String postalCode;
    private String googleMapsLink;

    //Online
    private Boolean isOnline;
    private String  zoomLink;

    // Teacher
    private Long   teacherId;
    private String teacherFirstName;
    private String teacherLastName;

    // Participants (calculation: availableSpots - registeredParticipants)
    private Integer availableSpots;
    private Integer registeredParticipants;

    // Client state
    private Boolean isUserRegistered;
}
