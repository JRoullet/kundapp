package jroullet.msidentity.controller;

import jakarta.validation.Valid;
import jroullet.msidentity.dto.TeacherRegistrationDTO;
import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.exception.EmailAlreadyExistsException;
import jroullet.msidentity.exception.UserNotFoundException;
import jroullet.msidentity.model.Role;
import jroullet.msidentity.service.TeacherService;
import jroullet.msidentity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final TeacherService teacherService;
    private final Logger logger = Logger.getLogger(UserController.class.getName());

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

    @PostMapping("/teachers")
    public ResponseEntity<UserDTO> registerTeacher(@Valid @RequestBody TeacherRegistrationDTO dto) {
        try {
            UserDTO response = teacherService.registerTeacher(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<UserDTO>> getAllTeachers() {
        logger.info("Fetching all teachers");
        List<UserDTO> teachers = userService.findAllByRole(Role.TEACHER);
        return ResponseEntity.ok(teachers);
    }


}
