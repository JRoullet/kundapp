package jroullet.msnotification.enums;

/**
 * Enum representing different types of notification events
 * that can occur in the system.
 * Used to categorize events for processing and notification purposes.
 */
public enum NotificationEventType {
    USER_ENROLLED,        // Client enrolled in session
    USER_CANCELLED,       // Client cancelled enrollment
    SESSION_CANCELLED,    // Teacher/Admin cancelled session
    SESSION_MODIFIED,     // Session details changed
    SESSION_COMPLETED,     // Session completed by teacher
    SESSION_CREATED         // Session created by teacher

}
