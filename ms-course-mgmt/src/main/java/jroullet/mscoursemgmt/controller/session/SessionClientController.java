package jroullet.mscoursemgmt.controller.session;

import jakarta.validation.Valid;
import jroullet.mscoursemgmt.dto.participant.AddParticipantRequest;
import jroullet.mscoursemgmt.dto.participant.ParticipantOperationResponse;
import jroullet.mscoursemgmt.dto.session.SessionNoParticipantsDTO;
import jroullet.mscoursemgmt.service.SessionParticipantService;
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
public class SessionClientController {

    private final SessionService sessionService;
    private final SessionParticipantService sessionParticipantService;

    /**
     *  Client part
     */
    // Displaying available sessions for client
    @GetMapping("/client/available")
    public ResponseEntity<List<SessionNoParticipantsDTO>> getAvailableSessionsForClient() {
        log.info("Fetching available sessions for client");
        List<SessionNoParticipantsDTO> sessions = sessionService.getAvailableSessionsForClient();
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    // Displaying upcoming sessions for client
    @GetMapping("/client/upcoming/{participantId}")
    public ResponseEntity<List<SessionNoParticipantsDTO>> getUpcomingSessionsForClient(@PathVariable("participantId") Long participantId) {
        List<SessionNoParticipantsDTO> sessions = sessionService.getUpcomingSessionsForClient(participantId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/client/past/{participantId}")
    public ResponseEntity<List<SessionNoParticipantsDTO>> getPastSessionsForClient(@PathVariable("participantId") Long participantId){
        List<SessionNoParticipantsDTO> sessions = sessionService.getPastSessionsForClient(participantId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Adds participant to a session
     */
    @PostMapping("/client/{sessionId}/participants")
    public ResponseEntity<ParticipantOperationResponse> addParticipant(
            @PathVariable Long sessionId,
            @Valid @RequestBody AddParticipantRequest request) {

        ParticipantOperationResponse response = sessionParticipantService
                .addParticipantToSession(sessionId, request.userId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Removes participant from a session (for cancellation)
     */
    @PostMapping("/client/{sessionId}/participants/remove/{userId}")
    public ResponseEntity<ParticipantOperationResponse> removeParticipant(
            @PathVariable Long sessionId,
            @PathVariable Long userId) {

        ParticipantOperationResponse response = sessionParticipantService
                .removeParticipantFromSession(sessionId, userId);

        return ResponseEntity.ok(response);
    }
}
