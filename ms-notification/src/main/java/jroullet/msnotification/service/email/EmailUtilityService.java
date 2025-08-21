package jroullet.msnotification.service.email;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jroullet.msnotification.document.Notification;
import jroullet.msnotification.exception.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailUtilityService {

    private final EmailService emailService;

    /**
     * Send email with resilience4j retry and circuit breaker
     */
    @Retry(name = "email-service")
    @CircuitBreaker(name = "email-service", fallbackMethod = "emailSendingFallback")
    public boolean sendEmailWithRetry(Notification notification) {
        log.debug("Attempting to send email - Recipient: {}", notification.getRecipient().getEmail());

        boolean success = emailService.sendNotificationEmail(notification);

        if (!success) {
            throw new EmailSendingException("Failed to send email to: " + notification.getRecipient().getEmail());
        }

        return true;
    }

    /**
     * Fallback method when email sending fails after all retries
     */
    private boolean emailSendingFallback(Notification notification, Exception ex) {
        log.warn("⚠️ Email sending fallback triggered for: {} - Error: {}",
                notification.getRecipient().getEmail(), ex.getMessage());
        return false;
    }

    public boolean testEmailConfiguration() {
        log.info("Testing email configuration");
        return emailService.testEmailConfiguration();
    }
}
