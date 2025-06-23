package jroullet.mswebapp.dto;

import jroullet.mswebapp.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherUpdateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String biography;
    private Address address;
    // No passwords updated here, will be done in a separated endpoint
    // Validation: only non-null fields will be updated
}
