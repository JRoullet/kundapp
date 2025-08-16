package jroullet.msidentity.controller;

import jroullet.msidentity.dto.user.UserParticipantDTO;
import jroullet.msidentity.exception.UserNotFoundException;
import jroullet.msidentity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/teacher/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherUserController {

    private final UserService userService;

    @PostMapping("/participants/list")
    public ResponseEntity<List<UserParticipantDTO>> getParticipantsByIds(@RequestBody List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        try {
            List<UserParticipantDTO> participants = userService.findAllParticipants(userIds);
            return ResponseEntity.status(HttpStatus.OK).body(participants);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
