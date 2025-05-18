package jroullet.msidentity.auth;

import jakarta.validation.constraints.Email;
import jroullet.msidentity.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor  // for deserializing
public class AuthResponseDTO {
    private boolean authenticated;
    private Long userId;
    private String email;
    private Role role;
}
