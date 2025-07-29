package jroullet.mscoursemgmt.controller;

import jroullet.mscoursemgmt.dto.SessionNoParticipantsDTO;
import jroullet.mscoursemgmt.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/sessions")
public class SessionClientController {

    private final SessionService sessionService;

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
}
