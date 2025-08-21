package jroullet.msnotification.document.notificationSubObjects;

import jroullet.msnotification.enums.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * Session information
 * Snapshot of session at notification time
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSession {
    private Long sessionId;
    private String description;
    private Subject subject;
    private LocalDateTime startDateTime;
    private Integer durationMinutes;
    private String teacherName;

    // Session type and location
    private Boolean isOnline;

    // IRL session fields (when isOnline = false)
    private String roomName;
    private String googleMapsLink;
    private Boolean bringYourMattress;

    // Online session fields (when isOnline = true)
    private String zoomLink;
}
