package jroullet.mscoursemgmt.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jroullet.mscoursemgmt.dto.SessionWithParticipantsDTO;
import jroullet.mscoursemgmt.dto.SessionUpdateDTO;
import jroullet.mscoursemgmt.exception.BusinessException;
import jroullet.mscoursemgmt.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/sessions")
public class SessionAdminController {

    private final SessionService sessionService;

    /**
     * Admin part
     */
    // All sessions for all teachers (admin)
    @GetMapping("/all")
    public ResponseEntity<List<SessionWithParticipantsDTO>> getAllSessionsForAdmin() {
        log.info("Fetching all sessions");
        List<SessionWithParticipantsDTO> sessions = sessionService.getAllSessionsForAdmin();
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }


    @PutMapping("/admin/{sessionId}/update")
    public ResponseEntity<SessionWithParticipantsDTO> updateSessionByAdmin(@PathVariable Long sessionId,
                                                                           @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO) {
        try {
            SessionWithParticipantsDTO updatedSession = sessionService.updateSessionByAdmin(sessionId, sessionUpdateDTO);
            return ResponseEntity.ok(updatedSession);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/admin/cancel")
    public ResponseEntity<Void> cancelSessionByAdmin(@RequestParam Long sessionId) {
        try {
            sessionService.cancelSessionByAdmin(sessionId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
