package jroullet.mscoursemgmt.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jroullet.mscoursemgmt.dto.*;
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
public class SessionTeacherController {

    private final SessionService sessionService;
    /**
     * Teacher part
     */
    @PostMapping
    public ResponseEntity<SessionCreationResponseDTO> createSession(@Valid @RequestBody SessionCreationWithTeacherDTO dto) {
        log.info("Creating session for teacher: {} with subject: {}", dto.getTeacherId(), dto.getSubject());

        try{
            SessionCreationResponseDTO createdSession = sessionService.createSession(dto);
            log.info("Session created successfully with ID: {}", createdSession.getSessionId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
        } catch (SessionStartingTimeException e){
            log.error("Time conflict for teacher: {}, {}", dto.getTeacherId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid session data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/teacher/cancel")
    public ResponseEntity<Void> cancelSessionByTeacher(@RequestBody SessionCancelDTO dto) {
        try {
            sessionService.cancelSessionByTeacher(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (SecurityException | BusinessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{sessionId}/teacher/{teacherId}")
    public ResponseEntity<SessionWithParticipantsDTO> updateSessionByTeacher(@PathVariable Long sessionId,
                                                                             @PathVariable Long teacherId,
                                                                             @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO) {
        try {
            SessionWithParticipantsDTO updatedSession = sessionService.updateSessionByTeacher(sessionId, teacherId, sessionUpdateDTO);
            return ResponseEntity.ok(updatedSession);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Next sessions for current teacher
    @GetMapping("/teacher/{teacherId}/upcoming")
    public ResponseEntity<List<SessionWithParticipantsDTO>> getUpcomingSessionsByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching upcoming sessions for teacher: {}", teacherId);
        List<SessionWithParticipantsDTO> sessions = sessionService.getUpcomingSessionsByTeacher(teacherId);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    // Past sessions for current teacher
    @GetMapping("/teacher/{teacherId}/past")
    public ResponseEntity<List<SessionWithParticipantsDTO>> getHistorySessionsByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching past sessions for teacher: {}", teacherId);
        List<SessionWithParticipantsDTO> sessions = sessionService.getHistorySessionsByTeacher(teacherId);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }
}
