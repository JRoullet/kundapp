package jroullet.mswebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("USER")
@Data
public class User{

    // Authentication Part
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email
    private String email; // login

    @NotBlank(message = "Password is mandatory")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private boolean status = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Complete information
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_picture")
    private String profilePicture; // URL or path to image

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Billing address
    private String street;
    private String city;
    private String zipCode;
    private String country;

//    // Oauth linking account attribute
//    @Column(name = "oauth_provider")
//    private String oauthProvider;
//
//    @Column(name = "oauth_id")
//    private String oauthId;
//
//    // email verification
//    @Column(name = "email_verified")
//    private Boolean emailVerified = false;

}
