package jroullet.mscoursemgmt.model;

public enum SessionStatus {
    SCHEDULED("À venir"),
    CANCELLED("Annulée"),
    COMPLETED("Terminée");

    private final String displayName;

    SessionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
