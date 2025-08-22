package jroullet.msnotification;

import jroullet.msnotification.config.EmailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = "jroullet.msnotification.repository")
@Slf4j
public class MsNotificationApplication implements CommandLineRunner {

    private final EmailConfig.EmailConfigValidator emailConfigValidator;

    public static void main(String[] args) {
        // ASCII Art Banner for ms-notification
        System.out.println("""
            ╔═══════════════════════════════════════════════╗
            ║            🔔 MS-NOTIFICATION 🔔               ║
            ║                                               ║
            ║  📧 Email Notifications Service               ║
            ║  📊 MongoDB Logging                          ║
            ║  🔄 Resilience4j Retry & Circuit Breaker    ║
            ║  🌐 Eureka Service Discovery                 ║
            ╚═══════════════════════════════════════════════╝
            """);

        SpringApplication.run(MsNotificationApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("MS-NOTIFICATION starting...");

        boolean configValid = emailConfigValidator.validateConfiguration();
        if (!configValid) {
            log.error("Invalid email configuration");
        }

        emailConfigValidator.logConfigurationSummary();
        log.info("MS-NOTIFICATION started successfully");
    }
}
