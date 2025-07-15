package jroullet.msidentity.controller;

import jakarta.validation.Valid;
import jroullet.msidentity.dto.*;
import jroullet.msidentity.exception.EmailAlreadyExistsException;
import jroullet.msidentity.exception.UserNotFoundException;
import jroullet.msidentity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
    @RequestMapping("/api/admin/users")
    @RequiredArgsConstructor
    @PreAuthorize("hasRole('ADMIN')")
    public class AdminUserController {

    private final static Logger logger = LoggerFactory.getLogger(jroullet.msidentity.controller.AdminUserController.class);
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        try {
            UserDTO user = userService.findUserById(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreationDTO dto){
        logger.info("Create User : " + dto.getEmail());
        try {
            UserDTO response = userService.createUser(dto);
            logger.info("User created");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailAlreadyExistsException e) {
            logger.error("Error creating user : " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id,
                                              @Valid @RequestBody UserUpdateDTO dto) {
        logger.info("Updating User ID: " + id + " with email: " + dto.getEmail());
        try {
            UserDTO updatedUser = userService.updateUser(id, dto);
            logger.info("User updated successfully");
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            logger.error("User not found: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EmailAlreadyExistsException e) {
            logger.error("Email conflict during update: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid data for user update: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Disable user
    @PostMapping("/{id}/disable")
    public ResponseEntity<UserStatusResponseDTO> disableUser(@PathVariable Long id) {
        logger.info("Disabling user ID: {}", id);
        try {
            UserStatusResponseDTO response = userService.disableUser(id);
            logger.info("User {} successfully disabled", id);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            logger.error("Cannot disable user {}: {}",id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Enable user
    @PostMapping("/{id}/enable")
    public ResponseEntity<UserStatusResponseDTO> enableUser(@PathVariable Long id) {
        logger.info("Enabling user ID: {}", id);
        try {
            UserStatusResponseDTO response = userService.enableUser(id);
            logger.info("User {} successfully enabled", id);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }




}