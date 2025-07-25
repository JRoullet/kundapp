package jroullet.mswebapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserParticipantDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
