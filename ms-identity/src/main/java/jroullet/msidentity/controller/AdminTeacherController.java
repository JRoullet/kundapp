package jroullet.msidentity.controller;

import jakarta.validation.Valid;
import jroullet.msidentity.dto.TeacherDTO;
import jroullet.msidentity.dto.TeacherRegistrationDTO;
import jroullet.msidentity.dto.TeacherUpdateDTO;
import jroullet.msidentity.exception.EmailAlreadyExistsException;
import jroullet.msidentity.exception.UserNotFoundException;
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

    //Register Teacher
    @PostMapping("/teachers")
    public ResponseEntity<TeacherDTO> registerTeacher(@Valid @RequestBody TeacherRegistrationDTO dto) {
        logger.info("Create Teacher : " + dto.getEmail());
        try {
            TeacherDTO response = teacherService.registerTeacher(dto);
            logger.info("Teacher created");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailAlreadyExistsException e) {
            logger.error("Error creating teacher : " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    //Update Teacher
    @PostMapping("/teachers/{id}/update")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable Long id,
                                                    @Valid @RequestBody TeacherUpdateDTO dto) {
        logger.info("Updating Teacher ID: " + id + " with email: " + dto.getEmail());
        try {
            TeacherDTO updatedTeacher = teacherService.updateTeacher(id, dto);
            logger.info("Teacher updated successfully");
            return ResponseEntity.ok(updatedTeacher);
        } catch (UserNotFoundException e) {
            logger.error("Teacher not found: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EmailAlreadyExistsException e) {
            logger.error("Email conflict during update: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid data for teacher update: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


}