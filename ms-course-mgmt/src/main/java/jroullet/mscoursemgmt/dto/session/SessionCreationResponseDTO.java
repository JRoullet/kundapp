package jroullet.mscoursemgmt.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreationResponseDTO {
    private Long sessionId;
    private LocalDateTime createdAt;
}
