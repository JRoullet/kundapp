package jroullet.msnotification.document.notificationSubObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Recipient information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecipient {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
}
