package jroullet.msidentity.dto;

import jroullet.msidentity.model.Address;
import jroullet.msidentity.model.Role;
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
public class UserCreationDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Address address;
    private Role role;
    private boolean status;
    private LocalDateTime createdAt;
    private Integer credits;
}
