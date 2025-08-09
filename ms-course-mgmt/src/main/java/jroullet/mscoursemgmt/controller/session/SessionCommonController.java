package jroullet.mscoursemgmt.controller.session;

import jroullet.mscoursemgmt.dto.session.SessionWithParticipantsDTO;
import jroullet.mscoursemgmt.exception.SessionNotFoundException;
import jroullet.mscoursemgmt.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/sessions")
public class SessionCommonController {

    private final SessionService sessionService;

    // Get session by ID
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionWithParticipantsDTO> getSessionById(@PathVariable Long sessionId) {
        log.info("Fetching session by ID: {}", sessionId);
        SessionWithParticipantsDTO session = sessionService.getSessionById(sessionId);
        return ResponseEntity.ok(session);
    }

}
