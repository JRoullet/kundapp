package jroullet.mswebapp.dto.user;

import jroullet.mswebapp.model.Address;
import jroullet.mswebapp.enums.Role;
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
    private LocalDate dateOfBirth;
    private Address address;
    private Role role;
    private boolean status;
    private LocalDateTime createdAt;
    private Integer credits;
}
