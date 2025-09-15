package jroullet.mscoursemgmt.dto.session;

import jakarta.validation.constraints.*;
import jroullet.mscoursemgmt.model.Subject;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionUpdateDTO {

    @NotNull(message = "Le type de séance est obligatoire")
    private Subject subject;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le type de session est obligatoire")
    private Boolean isOnline;

    private String roomName;
    private String postalCode;
    private String googleMapsLink;
    private String zoomLink;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins 1 place disponible")
    @Max(value = 50, message = "Maximum 50 places par session")
    private Integer availableSpots;

    @NotNull(message = "La date et heure de début sont obligatoires")
    private LocalDateTime startDateTime;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 15, message = "Durée minimum 15 minutes")
    @Max(value = 300, message = "Durée maximum 5 heures")
    private Integer durationMinutes;

    private Integer creditsRequired;
    private Boolean bringYourMattress;
}
