package jroullet.mswebapp.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "Saisir votre prénom")
    private String firstName;
    @NotBlank(message = "Saisir votre nom")
    private String lastName;
    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    private String email;
    @NotBlank(message = "Mot de passe obligatoire")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "The password must contain 8 characters, including an uppercase letter, a number, and a symbol"
    )
    private String password;

}


