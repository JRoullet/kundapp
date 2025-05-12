package jroullet.mswebapp.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @Email
    private String email;
    @NotEmpty
    private String password;

    public AuthRequestDTO() {
    }

    public AuthRequestDTO(String email, String password) {
    }
}
