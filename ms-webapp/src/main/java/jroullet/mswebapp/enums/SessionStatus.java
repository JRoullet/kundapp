package jroullet.mswebapp.enums;

import lombok.Getter;

@Getter
public enum SessionStatus {
    SCHEDULED("À venir"),
    CANCELLED("Annulée"),
    COMPLETED("Terminée");

    private final String displayName;

    SessionStatus(String displayName) {
        this.displayName = displayName;
    }

}
