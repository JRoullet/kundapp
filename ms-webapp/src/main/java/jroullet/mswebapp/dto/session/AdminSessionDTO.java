package jroullet.mswebapp.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSessionDTO {
    private SessionDTO session;
    private String teacherFirstName;
    private String teacherLastName;
}
