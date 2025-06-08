package jroullet.mswebapp.dto;

import jroullet.mswebapp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePicture;
    private LocalDate dateOfBirth;
    private String street;
    private String city;
    private String zipCode;
    private String country;
    private Role role;
    private boolean status;
    private LocalDateTime createdAt;
    private Integer credits;
}
