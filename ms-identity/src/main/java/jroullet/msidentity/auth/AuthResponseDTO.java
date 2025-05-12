package jroullet.msidentity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor  // For deserialization
public class AuthResponseDTO {
    private boolean authenticated;
    private Long userId;
    private String email;
    private String role;


}
