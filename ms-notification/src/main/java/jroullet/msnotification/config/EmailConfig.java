package jroullet.msnotification.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Email configuration for both development (mock) and production (real SMTP)
 * Switches between mock and real email sending based on configuration
 */
@Configuration
@Slf4j
public class EmailConfig {

    //Mock mode for development
    @Value("${notification.email.mock:true}")
    private boolean mockMode;




    // Production mode for real email sending
    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String smtpHost;

    @Value("${spring.mail.port:587}")
    private int smtpPort;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    @Value("${spring.mail.password:}")
    private String smtpPassword;


    /**
     * JavaMailSender for MOCK email sending (development)
     * Used when notification.email.mock=true
     * Creates a dummy sender that doesn't actually send emails
     * returns a mailSender that logs emails instead of sending them
     */
    @Bean
    @Profile("mock")
    public JavaMailSender mockJavaMailSender() {
        log.info("Configuring MOCK email sender - Emails will be logged only");

        // Create a basic JavaMailSender for mock mode
        // EmailService will handle the mock logic and won't actually use this sender
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(25);

        log.info("Mock email sender configured successfully");
        return mailSender;
    }





    /**
     * JavaMailSender for REAL email sending (production)
     * Used when notification.email.mock=false
     * Returns a mailSender configured with SMTP settings
     */
    @Bean
    @Profile("!mock")
    public JavaMailSender realJavaMailSender() {
        log.info("Configuring REAL email sender - Host: {}, Port: {}", smtpHost, smtpPort);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // SMTP Server configuration
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(smtpUsername);
        mailSender.setPassword(smtpPassword);

        // SMTP Properties
        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");

        // Security properties
        properties.put("mail.smtp.ssl.trust", smtpHost);
        properties.put("mail.debug", "false");

        log.info("Real email sender configured successfully");
        return mailSender;
    }

    /**
     * Email configuration validation
     * Checks if email configuration is valid for the current mode
     */
    @Bean
    public EmailConfigValidator emailConfigValidator() {
        return new EmailConfigValidator();
    }

    /**
     * Validator class to check email configuration
     */
    public class EmailConfigValidator {

        public boolean validateConfiguration() {
            log.info("Validating email configuration...");
            log.info("Email enabled: {}", emailEnabled);
            log.info("Mock mode: {}", mockMode);

            if (!emailEnabled) {
                log.info("Email disabled - Configuration valid");
                return true;
            }

            if (mockMode) {
                log.info("Mock mode enabled - Configuration valid");
                return true;
            }

            // Validate real SMTP configuration
            if (smtpHost == null || smtpHost.trim().isEmpty()) {
                log.error("SMTP host is required for real email sending");
                return false;
            }

            if (smtpUsername == null || smtpUsername.trim().isEmpty()) {
                log.error("SMTP username is required for real email sending");
                return false;
            }

            if (smtpPassword == null || smtpPassword.trim().isEmpty()) {
                log.error("SMTP password is required for real email sending");
                return false;
            }

            log.info("Real email configuration validated successfully");
            log.info("Host: {}", smtpHost);
            log.info("Port: {}", smtpPort);
            log.info("Username: {}", smtpUsername);
            log.info("Password: [HIDDEN]");

            return true;
        }

        public void logConfigurationSummary() {
            log.info("EMAIL CONFIGURATION SUMMARY:");
            log.info("Mode: {}", mockMode ? "MOCK (development)" : "REAL (production)");
            log.info("Enabled: {}", emailEnabled);

            if (!mockMode && emailEnabled) {
                log.info("SMTP Host: {}", smtpHost);
                log.info("SMTP Port: {}", smtpPort);
                log.info("SMTP User: {}", smtpUsername);
            }

            log.info("   ═══════════════════════════════════════");
        }
    }


}

/**
 * Configuration properties explanation:
 *
 * DEVELOPMENT (application-docker.yml on GitHub):
 * notification:
 *   email:
 *     enabled: false
 *     mock: true
 *     from: noreply@kundapp.com
 *
 * PRODUCTION (environment variables):
 * NOTIFICATION_EMAIL_ENABLED=true
 * NOTIFICATION_EMAIL_MOCK=false
 * SPRING_MAIL_HOST=smtp.gmail.com
 * SPRING_MAIL_PORT=587
 * SPRING_MAIL_USERNAME=your-email@gmail.com
 * SPRING_MAIL_PASSWORD=your-app-password
 *
 * The configuration automatically switches between mock and real sending
 * based on these properties, making it safe for development and production.
 */
