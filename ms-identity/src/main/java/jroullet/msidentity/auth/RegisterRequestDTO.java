package jroullet.msidentity.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @Email(message = "Please enter an email")
    private String email;
    @NotBlank(message = "Password is mandatory")
    private String password;

}
