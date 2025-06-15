package jroullet.msidentity.controller;

import jakarta.validation.Valid;
import jroullet.msidentity.dto.TeacherRegistrationDTO;
import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.exception.EmailAlreadyExistsException;
import jroullet.msidentity.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTeacherController {

    private final static Logger logger = LoggerFactory.getLogger(AdminTeacherController.class);
    private final TeacherService teacherService;

    @PostMapping("/teachers")
    public ResponseEntity<UserDTO> registerTeacher(@Valid @RequestBody TeacherRegistrationDTO dto) {
        logger.info("Create Teacher : " + dto.getEmail());
        try {
            UserDTO response = teacherService.registerTeacher(dto);
            logger.info("Teacher created");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailAlreadyExistsException e) {
            logger.error("Error creating teacher : " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}