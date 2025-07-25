package jroullet.mswebapp.dto.teacher;

import jroullet.mswebapp.model.Address;
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
public class TeacherDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String biography;
    private Role role;
    private boolean status;
    private LocalDateTime createdAt;
    private Address address;
}
