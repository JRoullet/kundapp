package jroullet.mswebapp.dto;

import lombok.Data;

@Data
public class ClientResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
} 