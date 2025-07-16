package jroullet.mscoursemgmt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
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

    @Enumerated(EnumType.STRING)
    private Subject subject;
    private String description;
    private String roomName;
    private String googleMapsLink;
    private Integer availableSpots;
    private LocalDateTime startDateTime;
    private Duration duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer creditsRequired;
    @ElementCollection
    private List<Long> participantIds;
    private Boolean bringYourMattress;

}
