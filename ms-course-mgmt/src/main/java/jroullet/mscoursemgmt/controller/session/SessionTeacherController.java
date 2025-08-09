package jroullet.mscoursemgmt.controller.session;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jroullet.mscoursemgmt.dto.session.*;
import jroullet.mscoursemgmt.exception.*;
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

    @PostMapping
    public ResponseEntity<SessionCreationResponseDTO> createSession(@Valid @RequestBody SessionCreationWithTeacherDTO dto) {
        log.info("Creating session for teacher: {} with subject: {}", dto.getTeacherId(), dto.getSubject());

        // Plus besoin de try/catch - le GlobalExceptionHandler s'en charge
        SessionCreationResponseDTO createdSession = sessionService.createSession(dto);
        log.info("Session created successfully with ID: {}", createdSession.getSessionId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
    }

    @PostMapping("/teacher/cancel")
    public ResponseEntity<Void> cancelSessionByTeacher(@RequestBody SessionCancelDTO dto) {
        sessionService.cancelSessionByTeacher(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{sessionId}/teacher/{teacherId}")
    public ResponseEntity<SessionWithParticipantsDTO> updateSessionByTeacher(
            @PathVariable Long sessionId,
            @PathVariable Long teacherId,
            @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO) {

        SessionWithParticipantsDTO updatedSession = sessionService.updateSessionByTeacher(
                sessionId, teacherId, sessionUpdateDTO
        );
        return ResponseEntity.ok(updatedSession);
    }

    @GetMapping("/teacher/{teacherId}/upcoming")
    public ResponseEntity<List<SessionWithParticipantsDTO>> getUpcomingSessionsByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching upcoming sessions for teacher: {}", teacherId);
        List<SessionWithParticipantsDTO> sessions = sessionService.getUpcomingSessionsByTeacher(teacherId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/teacher/{teacherId}/past")
    public ResponseEntity<List<SessionWithParticipantsDTO>> getHistorySessionsByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching past sessions for teacher: {}", teacherId);
        List<SessionWithParticipantsDTO> sessions = sessionService.getHistorySessionsByTeacher(teacherId);
        return ResponseEntity.ok(sessions);
    }
}
