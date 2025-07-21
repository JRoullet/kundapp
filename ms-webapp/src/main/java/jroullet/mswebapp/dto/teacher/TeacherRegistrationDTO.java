package jroullet.mswebapp.dto.teacher;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jroullet.mswebapp.model.Address;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TeacherRegistrationDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private LocalDate dateOfBirth;

    private String phoneNumber;

    private Address address;

    @Size(max = 1000, message = "Biography must not exceed 1000 characters")
    private String biography;
}
