package jroullet.mscoursemgmt.controller;

import jakarta.validation.Valid;
import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.dto.SessionDTO;
import jroullet.mscoursemgmt.exception.SessionStartingTimeException;
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
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionDTO> createSession(@RequestParam Long teacherId,
                                                    @Valid @RequestBody SessionCreationDTO dto) {
        log.info("Creating session for teacher: {} with subject: {}", teacherId, dto.getSubject());

        try{
            SessionDTO createdSession = sessionService.createSession(teacherId,dto);
            log.info("Session created successfully with ID: {}", createdSession.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
        } catch (SessionStartingTimeException e){
            log.error("Time conflict for teacher: {}, {}", teacherId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid session data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/teacher/{teacherId}/upcoming")
    public ResponseEntity<List<SessionDTO>> getUpcomingSessionsByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching upcoming sessions for teacher: {}", teacherId);
        List<SessionDTO> sessions = sessionService.getUpcomingSessionsByTeacher(teacherId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/teacher/{teacherId}/past")
    public ResponseEntity<List<SessionDTO>> getPastSessionsByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching past sessions for teacher: {}", teacherId);
        List<SessionDTO> sessions = sessionService.getPastSessionsByTeacher(teacherId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/teacher/{teacherId}/all")
    public ResponseEntity<List<SessionDTO>> getAllSessionsByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching all sessions for teacher: {}", teacherId);
        List<SessionDTO> sessions = sessionService.getAllSessionsByTeacher(teacherId);
        return ResponseEntity.ok(sessions);
    }


}
