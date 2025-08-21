package jroullet.msidentity.controller;

import jroullet.msidentity.dto.teacher.TeacherDTO;
import jroullet.msidentity.dto.user.UserDTO;
import jroullet.msidentity.dto.user.UserParticipantDTO;
import jroullet.msidentity.exception.UserNotFoundException;
import jroullet.msidentity.service.TeacherService;
import jroullet.msidentity.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UtilityUserFinderController {

    private final UserServiceImpl userService;
    private final Logger logger = LoggerFactory.getLogger(UtilityUserFinderController.class);
    private final TeacherService teacherService;

    // Return UserDto (sessionUser)
    @GetMapping("/user")
    ResponseEntity<UserDTO> findUserDtoByEmail(@RequestParam("email") String email){
        logger.info("Looking for user by email : " + email);
        try {
            UserDTO user = userService.findUserDTOByEmail(email);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Return all users
    @GetMapping("/users")
    ResponseEntity<List<UserDTO>> getAllUsers(){
        logger.info("Looking for users");
        try {
            List<UserDTO> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Return all teachers
    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        logger.info("Fetching all teachers");
        List<TeacherDTO> teachers = userService.findAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    // Return teacher by id
    @GetMapping("/teachers/{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long id) {
        logger.info("Fetching teacher by ID: " + id);
        try {
            TeacherDTO teacher = teacherService.findTeacherById(id);
            return ResponseEntity.ok(teacher);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    //Public endpoints for user operations
    @PostMapping("/users/basic-info")
    ResponseEntity<List<UserParticipantDTO>> getUsersBasicInfo(@RequestBody List<Long> userIds){
        logger.info("Fetching basic info for users with IDs: " + userIds);
        try {
            List<UserParticipantDTO> users = userService.findAllBasicInfoParticipants(userIds);
            return ResponseEntity.ok(users);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/users/{id}/basic-info")
    ResponseEntity<UserParticipantDTO> getUserBasicInfo(@PathVariable Long id){
        logger.info("Fetching user basic info by ID: " + id);
        try {
            UserParticipantDTO user = userService.findBasicUserById(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
