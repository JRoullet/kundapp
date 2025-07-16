package jroullet.msidentity.controller;

import jakarta.validation.Valid;
import jroullet.msidentity.dto.*;
import jroullet.msidentity.exception.EmailAlreadyExistsException;
import jroullet.msidentity.exception.UserNotFoundException;
import jroullet.msidentity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
    @RequestMapping("/api/admin/users")
    @RequiredArgsConstructor
    @PreAuthorize("hasRole('ADMIN')")
    public class AdminUserController {

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
        log.info("Create User : " + dto.getEmail());
        try {
            UserDTO response = userService.createUser(dto);
            log.info("User created");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailAlreadyExistsException e) {
            log.error("Error creating user : " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id,
                                              @Valid @RequestBody UserUpdateDTO dto) {
        log.info("Updating User ID: " + id + " with email: " + dto.getEmail());
        try {
            UserDTO updatedUser = userService.updateUser(id, dto);
            log.info("User updated successfully");
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            log.error("User not found: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EmailAlreadyExistsException e) {
            log.error("Email conflict during update: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid data for user update: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user ID: {}", id);
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
        log.info("Disabling user ID: {}", id);
        try {
            UserStatusResponseDTO response = userService.disableUser(id);
            log.info("User {} successfully disabled", id);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error("Cannot disable user {}: {}",id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Enable user
    @PostMapping("/{id}/enable")
    public ResponseEntity<UserStatusResponseDTO> enableUser(@PathVariable Long id) {
        log.info("Enabling user ID: {}", id);
        try {
            UserStatusResponseDTO response = userService.enableUser(id);
            log.info("User {} successfully enabled", id);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //add credits
    @PostMapping("/{id}/credits/add")
    public ResponseEntity<Void> addUserCredits(@PathVariable("id") Long id,
                                               @Valid @RequestParam Integer credits) {
        log.info("Adding User ID credits: " + id);
        try {
            userService.addUserCredits(id, credits);
            log.info("User credits added successfully to : " + credits);
        } catch (UserNotFoundException e) {
            log.error("User not found: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid data for adding credits : " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return null;
    }


}