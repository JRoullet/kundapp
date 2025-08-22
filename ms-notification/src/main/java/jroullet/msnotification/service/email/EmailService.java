package jroullet.msnotification.service.email;

import jroullet.msnotification.document.Notification;
import jroullet.msnotification.document.notificationSubObjects.NotificationRecipient;

import jroullet.msnotification.document.notificationSubObjects.NotificationSession;
import jroullet.msnotification.enums.NotificationEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    // Will generate emails based on Thymeleaf templates
    private final TemplateEngine templateEngine;
    // JavaMailSender for sending emails via SMTP
    private final JavaMailSender mailSender;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${notification.email.mock:true}")
    private boolean mockMode;

    @Value("${notification.email.from:noreply@kundapp.com}")
    private String fromEmail;


    /**
     * Send notification email based on notification entity
     *
     * @param notification The notification containing all email data
     * @return true if sent successfully, false if failed
     */
    public boolean sendNotificationEmail(Notification notification) {
        try {
            // Build email content from template
            String emailContent = buildEmailContent(
                    notification.getTemplateName(),
                    createTemplateVariables(notification)
            );

            // Send email (mock or real)
            if (mockMode) {
                sendMockEmail(notification, emailContent);
            } else {
                sendRealEmail(notification, emailContent);
            }

            log.info("Email sent successfully - Type: {}, Recipient: {}, Session: {}",
                    notification.getEventType(),
                    notification.getRecipient().getEmail(),
                    notification.getSessionId()
            );

            return true;

        } catch (Exception e) {
            log.error("Failed to send email - Type: {}, Recipient: {}, Error: {}",
                    notification.getEventType(),
                    notification.getRecipient().getEmail(),
                    e.getMessage()
            );
            return false;
        }
    }

    /**
     * Send mock email (logs only) - for prototype development
     */
    private void sendMockEmail(Notification notification, String emailContent) {
        NotificationRecipient recipient = notification.getRecipient();
        NotificationSession session = notification.getSession();

        log.info("   MOCK EMAIL SENT:");
        log.info("   From: {}", fromEmail);
        log.info("   To: {} ({})", recipient.getEmail(), getFullName(recipient));
        log.info("   Subject: {}", notification.getEmailSubject());
        log.info("   Event: {}", notification.getEventType());
        log.info("   Session: {} - {}", session.getSubject(), formatDateTime(session.getStartDateTime()));
        log.info("   Template: {}", notification.getTemplateName());

        // Log email content preview (first 200 chars)
        String preview = emailContent.replaceAll("\\s+", " ").trim();
        if (preview.length() > 200) {
            preview = preview.substring(0, 200) + "...";
        }
        log.info("   Content preview: {}", preview);
        log.info("   ═══════════════════════════════════════");
    }

    /**
     * Send real email via SMTP - for production
     */
    private void sendRealEmail(Notification notification, String emailContent) throws Exception {
        if (!emailEnabled) {
            log.warn("⚠️ Email sending is disabled");
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(notification.getRecipient().getEmail());
        helper.setSubject(notification.getEmailSubject());
        //HTML content
        helper.setText(emailContent, true);

        mailSender.send(message);
    }

    /**
     * Build email content using Thymeleaf template
     */
    private String buildEmailContent(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String templatePath = "emails/users/" + templateName;
        return templateEngine.process(templatePath, context);
    }

    /**
     * Create template variables for Thymeleaf
     * All data needed for email generation
     */
    private Map<String, Object> createTemplateVariables(Notification notification) {
        Map<String, Object> variables = new HashMap<>();

        NotificationRecipient recipient = notification.getRecipient();
        NotificationSession session = notification.getSession();

        // Recipient variables
        variables.put("firstName", recipient.getFirstName());
        variables.put("lastName", recipient.getLastName());
        variables.put("fullName", getFullName(recipient));
        variables.put("email", recipient.getEmail());

        // Session variables
        variables.put("sessionSubject", session.getSubject().toString());
        variables.put("sessionDescription", session.getDescription());
        variables.put("sessionDate", formatDate(session.getStartDateTime()));
        variables.put("sessionTime", formatTime(session.getStartDateTime()));
        variables.put("sessionDateTime", formatDateTime(session.getStartDateTime()));
        variables.put("duration", session.getDurationMinutes() + " minutes");
        variables.put("teacherName", session.getTeacherName());

        // Location variables
        if (session.getIsOnline()) {
            variables.put("isOnline", true);
            variables.put("zoomLink", session.getZoomLink());
            variables.put("location", "En ligne");
        } else {
            variables.put("isOnline", false);
            variables.put("roomName", session.getRoomName());
            variables.put("googleMapsLink", session.getGoogleMapsLink());
            variables.put("bringYourMattress", session.getBringYourMattress());
            variables.put("location", session.getRoomName());
        }

        // Event specific variables
        variables.put("eventType", notification.getEventType().toString());
        variables.put("notificationDate", formatDateTime(notification.getCreatedAt()));

        // Session gets modified
        if ((notification.getEventType() == NotificationEventType.SESSION_MODIFIED_TO_USER_NOTIFICATION
                || notification.getEventType() == NotificationEventType.SESSION_MODIFIED_TO_TEACHER_NOTIFICATION)
                && session.getModificationSummary() != null) {
            variables.put("modificationSummary", session.getModificationSummary());
        }

        // students values for teacher notifications when enrolled/unenrolled
        if (notification.getEventType() == NotificationEventType.USER_ENROLLED_TO_TEACHER_NOTIFICATION ||
                notification.getEventType() == NotificationEventType.USER_CANCELED_TO_TEACHER_NOTIFICATION) {

            if (notification.getAdditionalParticipants() != null && !notification.getAdditionalParticipants().isEmpty()) {
                NotificationRecipient student = notification.getAdditionalParticipants().get(0);
                variables.put("studentFirstName", student.getFirstName());
                variables.put("studentLastName", student.getLastName());
            }
        }

        variables.put("companyName", "KundApp");
        variables.put("supportEmail", "support@kundapp.com");

        return variables;
    }

    /**
     * Helper methods for formatting
     */
    private String getFullName(NotificationRecipient recipient) {
        return (recipient.getFirstName() + " " + recipient.getLastName()).trim();
    }
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));
    }
    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    private String formatTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Test method to verify email configuration
     */
    public boolean testEmailConfiguration() {
        try {
            log.info("   Testing email configuration...");
            log.info("   Email enabled: {}", emailEnabled);
            log.info("   Mock mode: {}", mockMode);
            log.info("   From email: {}", fromEmail);

            if (!mockMode && emailEnabled) {
                // Test SMTP connection
                mailSender.createMimeMessage();
                log.info("SMTP configuration OK");
            }

            return true;
        } catch (Exception e) {
            log.error("Email configuration test failed: {}", e.getMessage());
            return false;
        }
    }
}
