package jroullet.msidentity.dto.user;

import jroullet.msidentity.model.Address;
import jroullet.msidentity.model.Role;
import jroullet.msidentity.model.SubscriptionStatus;
import lombok.Data;

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
    private LocalDate dateOfBirth;
    private Address address;
    private String biography;
    private SubscriptionStatus subscriptionStatus;
    private Integer credits;

}
