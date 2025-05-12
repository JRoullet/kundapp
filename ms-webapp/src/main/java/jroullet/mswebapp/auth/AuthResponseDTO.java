package jroullet.mswebapp.auth;

import jroullet.mswebapp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor  // for deserializing
public class AuthResponseDTO {
    private boolean authenticated;
    private Long userId;
    private String email;
    private Role role;
}
