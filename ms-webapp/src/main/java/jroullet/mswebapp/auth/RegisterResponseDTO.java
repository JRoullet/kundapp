package jroullet.mswebapp.auth;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegisterResponseDTO {
    private Long id;
    private String email;
    private String role;
}
