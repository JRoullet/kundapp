package jroullet.mscoursemgmt.dto.session;

import jakarta.validation.constraints.*;
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
public class SessionCreationWithTeacherDTO {

    private Long teacherId;
    private String teacherFirstName;
    private String teacherLastName;

    @NotNull(message = "Le sujet est obligatoire")
    private Subject subject;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    // IRL session fields - conditional validation needed
    private String roomName;
    private String postalCode;
    @Pattern(regexp = "^https://(maps\\.google\\.(com|fr)|maps\\.app\\.goo\\.gl)/.*",
            message = "Le lien doit être un lien Google Maps valide")
    private String googleMapsLink;

    // Online session fields
    @NotNull(message = "Précisez si la session est en ligne ou en présentiel")
    @Builder.Default private Boolean isOnline = false;

    @Pattern(regexp = "^https://(.*\\.)?zoom\\.(us|com)/j/\\d+.*$",
            message = "Le lien doit être un lien Zoom valide")
    private String zoomLink;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins 1 place disponible")
    @Max(value = 50, message = "Maximum 50 places par session")
    private Integer availableSpots;

    @NotNull(message = "La date et heure de début sont obligatoires")
    @Future(message = "La session doit être dans le futur")
    private LocalDateTime startDateTime;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 15, message = "Durée minimum 15 minutes")
    @Max(value = 300, message = "Durée maximum 5 heures")
    private Integer durationMinutes;

    @Builder.Default private Integer creditsRequired = 1;

    // Mattress only relevant for IRL sessions
    private Boolean bringYourMattress;
}