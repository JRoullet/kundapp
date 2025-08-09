package jroullet.msidentity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusResponseDTO {
    private String firstName;
    private String lastName;
}
