package jroullet.mswebapp.clients;

import jroullet.mswebapp.auth.AuthRequestDTO;
import jroullet.mswebapp.auth.AuthResponseDTO;
import jroullet.mswebapp.auth.RegisterRequestDTO;
import jroullet.mswebapp.auth.RegisterResponseDTO;
import jroullet.mswebapp.dto.*;
import jroullet.mswebapp.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-identity", path = "/api")
public interface IdentityFeignClient {

    // Authentication request
    @PostMapping("/authenticate")
    ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO authRequest);

    // Registration request
    @PostMapping("/register")
    ResponseEntity<RegisterResponseDTO> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO);

    // Return UserDto (sessionUser)
    @GetMapping("/user")
    UserDTO findUserDtoByEmail(@RequestParam String email);

    // Returns All Users
    @GetMapping("/users")
    List<UserDTO> getAllUsers();


    /**
     * Get user by ID
     */
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);

    /**
     * Create new user
     */
    @PostMapping("/users")
    UserDTO createUser(@RequestBody UserDTO userDTO);

    /**
     * Partial update of existing user (PATCH - safer for partial entities)
     */
    @PatchMapping("/users/{id}")
    UserDTO patchUser(@PathVariable Long id, @RequestBody UserDTO userDTO);

    /**
     * Disable user (set status = false) - PATCH
     */
    @PatchMapping("/users/{id}/disable")
    void disableUser(@PathVariable Long id);

    /**
     * Delete user permanently
     */
    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id);

    /**
     * Update user credits (CLIENT only) - PATCH
     */
    @PatchMapping("/users/{id}/credits")
    void updateUserCredits(@PathVariable Long id, @RequestParam Integer credits);

    /**
     * Change user password - PATCH
     */
    @PatchMapping("/users/{id}/password")
    void changeUserPassword(@PathVariable Long id,
                            @RequestParam String currentPassword,
                            @RequestParam String newPassword);






}
