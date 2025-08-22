package jroullet.mswebapp.enums;

/**
 * Enum representing different types of notification events
 * that can occur in the system.
 * Used to categorize events for processing and notification purposes.
 */
public enum NotificationEventType {
    USER_ENROLLED_TO_USER_NOTIFICATION,        // Client enrolled in session
    USER_CANCELLED_TO_USER_NOTIFICATION,       // Client cancelled a session
    USER_ENROLLED_TO_TEACHER_NOTIFICATION,     //TEACHER NOTIFICATION WHEN CLIENT ENROLLED
    USER_CANCELED_TO_TEACHER_NOTIFICATION,     // TEACHER NOTIFICATION WHEN CLIENT CANCELLED SESSION

    SESSION_CANCELLED_TO_USER_NOTIFICATION,
    SESSION_CANCELLED_TO_TEACHER_NOTIFICATION,
    SESSION_MODIFIED_TO_USER_NOTIFICATION,
    SESSION_MODIFIED_TO_TEACHER_NOTIFICATION,
    SESSION_COMPLETED_TO_USER_NOTIFICATION,
    SESSION_CREATED_TO_TEACHER_NOTIFICATION
}
