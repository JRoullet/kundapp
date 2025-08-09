package jroullet.mscoursemgmt.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionCancelDTO {
    private Long sessionId;
    private Long teacherId;
}
