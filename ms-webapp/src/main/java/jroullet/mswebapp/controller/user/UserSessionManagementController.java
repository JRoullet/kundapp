package jroullet.mswebapp.controller.user;

import jroullet.mswebapp.dto.session.SessionNoParticipantsDTO;
import jroullet.mswebapp.service.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/client/sessions")
@RequiredArgsConstructor

public class UserSessionManagementController {

    private final SessionManagementService sessionManagementService;

    @GetMapping("/available")
    @ResponseBody
    public ResponseEntity<List<SessionNoParticipantsDTO>> getAvailableSessions() {
        List<SessionNoParticipantsDTO> sessions = sessionManagementService.getAvailableSessionsForClient();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/upcoming")
    @ResponseBody
    public ResponseEntity<List<SessionNoParticipantsDTO>> getUpcomingSessions() {
        List<SessionNoParticipantsDTO> sessions = sessionManagementService.getUpcomingSessionsForClient();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<List<SessionNoParticipantsDTO>> getHistorySessions() {
        List<SessionNoParticipantsDTO> sessions = sessionManagementService.getPastSessionsForClient();
        return ResponseEntity.ok(sessions);
    }

}
