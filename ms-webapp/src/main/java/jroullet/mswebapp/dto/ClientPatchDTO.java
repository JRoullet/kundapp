package jroullet.mswebapp.dto;

import lombok.Data;

@Data
public class ClientPatchDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePicture;
    private String dateOfBirth;
    private String street;
    private String city;
    private String zipCode;
    private String country;
} 