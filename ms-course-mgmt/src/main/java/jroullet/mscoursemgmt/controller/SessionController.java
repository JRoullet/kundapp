package jroullet.mscoursemgmt.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jroullet.mscoursemgmt.dto.SessionCancelDTO;
import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.dto.SessionDTO;
import jroullet.mscoursemgmt.dto.SessionUpdateDTO;
import jroullet.mscoursemgmt.exception.BusinessException;
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

    // Get session by ID
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDTO> getSessionById(@PathVariable Long sessionId) {
        log.info("Fetching session by ID: {}", sessionId);
        try {
            SessionDTO session = sessionService.getSessionById(sessionId);
            return ResponseEntity.status(HttpStatus.OK).body(session);
        } catch (IllegalArgumentException e) {
            log.error("Invalid session ID: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

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
    @PostMapping("/teacher/cancel")
    public ResponseEntity<Void> cancelSession(@RequestBody SessionCancelDTO dto) {
        try {
            sessionService.cancelSession(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (SecurityException | BusinessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{sessionId}/teacher/{teacherId}")
    public ResponseEntity<SessionDTO> updateSessionByTeacher(@PathVariable Long sessionId,
                                                             @PathVariable Long teacherId,
                                                             @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO) {
        try {
            SessionDTO updatedSession = sessionService.updateSessionByTeacher(sessionId, teacherId, sessionUpdateDTO);
            return ResponseEntity.ok(updatedSession);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Next sessions for current teacher
    @GetMapping("/teacher/{teacherId}/upcoming")
    public ResponseEntity<List<SessionDTO>> getUpcomingSessionsByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching upcoming sessions for teacher: {}", teacherId);
        List<SessionDTO> sessions = sessionService.getUpcomingSessionsByTeacher(teacherId);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    // Past sessions for current teacher
    @GetMapping("/teacher/{teacherId}/past")
    public ResponseEntity<List<SessionDTO>> getHistorySessionsByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching past sessions for teacher: {}", teacherId);
        List<SessionDTO> sessions = sessionService.getHistorySessionsByTeacher(teacherId);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    // All sessions for all teachers (admin)
    @GetMapping("/all")
    public ResponseEntity<List<SessionDTO>> getAllSessionsForAdmin() {
        log.info("Fetching all sessions");
        List<SessionDTO> sessions = sessionService.getAllSessionsForAdmin();
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }


    @PutMapping("/admin/{sessionId}/update")
    public ResponseEntity<SessionDTO> updateSessionByAdmin(@PathVariable Long sessionId,
                                                           @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO) {
        try {
            SessionDTO updatedSession = sessionService.updateSessionByAdmin(sessionId, sessionUpdateDTO);
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
