package jroullet.msidentity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Data
public class User {

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


    // Complete information
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private boolean status = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Billing address - Relation OneToOne
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "biography", length = 1000)
    private String biography;

    @Column(name = "subscription_status")
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.NONE;

    Integer credits;
}
