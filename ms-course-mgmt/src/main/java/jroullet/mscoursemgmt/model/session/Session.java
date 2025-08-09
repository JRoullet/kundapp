package jroullet.mscoursemgmt.model.session;

import jakarta.persistence.*;
import jroullet.mscoursemgmt.model.Subject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "session")
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long teacherId;
    private String teacherFirstName;
    private String teacherLastName;

    @Enumerated(EnumType.STRING)
    private Subject subject;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.SCHEDULED;

    // Location fields - IRL sessions
    private String roomName;
    private String postalCode;
    private String googleMapsLink;

    // Online session
    @Column(name = "is_online", nullable = false)
    private Boolean isOnline = false;
    @Column(name = "zoom_link")
    private String zoomLink;

    private Integer availableSpots;
    private LocalDateTime startDateTime;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer creditsRequired;

    @ElementCollection
    @CollectionTable(
            name = "session_participants",
            joinColumns = @JoinColumn(name = "session_id")
    )
    @Column(name = "participant_id")
    private List<Long> participantIds;

    private Boolean bringYourMattress;
}
