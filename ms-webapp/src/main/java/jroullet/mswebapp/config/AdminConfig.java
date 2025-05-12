package jroullet.mswebapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class AdminConfig {

    /**
     * Password encoder bean using BCrypt algorithm.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner initAdmin(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin already exists
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users WHERE email = ?",
                    Integer.class,
                    "admin@gmail.com");

            if (count == 0) {
                // Insert admin
                jdbcTemplate.update(
                        "INSERT INTO users (email, password, role, created_at, status, user_type) VALUES (?, ?, ?, ?, ?, ?)",
                        "admin@gmail.com",
                        passwordEncoder.encode("admin123"),
                        "ADMIN",
                        LocalDateTime.now(),
                        true,
                        "USER");  // Discriminator value for standard User
                System.out.println("Admin successfully added (admin@gmail.com / admin123)");

                // Insert client (with all fields in a single table)
                jdbcTemplate.update(
                        "INSERT INTO users (email, password, role, created_at, status, first_name, last_name, " +
                                "phone_number, date_of_birth, street, city, zip_code, country, subscription_status, user_type) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "user@gmail.com",
                        passwordEncoder.encode("user123"),
                        "CLIENT",
                        LocalDateTime.now(),
                        true,
                        "John",
                        "Doe",
                        "514-5555-555",
                        java.sql.Date.valueOf(LocalDate.of(2020, 12, 15)),
                        "22 Jump Street",
                        "Las Vegas",
                        "83500",
                        "USA",
                        "ACTIVE",
                        "CLIENT");  // Discriminator value for Client
                System.out.println("Client successfully added (user@gmail.com / user123)");

                // Insert teacher (with all fields in a single table)
                jdbcTemplate.update(
                        "INSERT INTO users (email, password, role, created_at, status, first_name, last_name, " +
                                "phone_number, biography, user_type) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "teacher@gmail.com",
                        passwordEncoder.encode("teacher123"),
                        "TEACHER",
                        LocalDateTime.now(),
                        true,
                        "Jenna",
                        "Watkins",
                        "512-422-887",
                        "lalalalala lorem ipsum",
                        "TEACHER");  // Discriminator value for Teacher
                System.out.println("Teacher successfully added (teacher@gmail.com / teacher123)");
            }
        };
    }
}


