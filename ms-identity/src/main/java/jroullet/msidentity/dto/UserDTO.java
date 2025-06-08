package jroullet.msidentity.dto;

import jroullet.msidentity.model.Role;
import jroullet.msidentity.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private boolean status;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePicture;
    private LocalDate dateOfBirth;
    private String street;
    private String city;
    private String zipCode;
    private String country;
    private String biography;
    private SubscriptionStatus subscriptionStatus;
    private Integer credits;
}
