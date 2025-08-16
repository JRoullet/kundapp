package jroullet.mscoursemgmt.controller.session;

import jakarta.validation.Valid;
import jroullet.mscoursemgmt.dto.session.SessionWithParticipantsDTO;
import jroullet.mscoursemgmt.dto.session.SessionUpdateDTO;
import jroullet.mscoursemgmt.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/sessions")
public class SessionAdminController {

    private final SessionService sessionService;

    @GetMapping("/all")
    public ResponseEntity<List<SessionWithParticipantsDTO>> getAllSessionsForAdmin() {
        log.info("Fetching all sessions");
        List<SessionWithParticipantsDTO> sessions = sessionService.getAllSessionsForAdmin();
        return ResponseEntity.ok(sessions);
    }

    @PutMapping("/admin/{sessionId}/update")
    public ResponseEntity<SessionWithParticipantsDTO> updateSessionByAdmin(
            @PathVariable Long sessionId,
            @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO) {

        SessionWithParticipantsDTO updatedSession = sessionService.updateSessionByAdmin(sessionId, sessionUpdateDTO);
        return ResponseEntity.ok(updatedSession);
    }

    @PostMapping("/admin/cancel")
    public ResponseEntity<Void> cancelSessionByAdmin(@RequestParam Long sessionId) {
        sessionService.cancelSessionByAdmin(sessionId);
        return ResponseEntity.noContent().build();
    }

}
