package jroullet.msnotification.enums;

/**
 * Enum representing the status of a notification.
 * Used to track the lifecycle of notifications in the system.
 */
public enum NotificationStatus {
    PENDING,    // Created but not yet sent
    SENT,       // Successfully delivered
    FAILED      // Delivery failed
}
