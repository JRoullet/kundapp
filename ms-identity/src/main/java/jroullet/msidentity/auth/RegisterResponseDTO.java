package jroullet.msidentity.auth;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
