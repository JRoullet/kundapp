package jroullet.mswebapp.clients;

import jroullet.mswebapp.auth.AuthRequestDTO;
import jroullet.mswebapp.auth.AuthResponseDTO;
import jroullet.mswebapp.auth.RegisterRequestDTO;
import jroullet.mswebapp.auth.RegisterResponseDTO;
import jroullet.mswebapp.dto.teacher.TeacherDTO;
import jroullet.mswebapp.dto.teacher.TeacherRegistrationDTO;
import jroullet.mswebapp.dto.teacher.TeacherUpdateDTO;
import jroullet.mswebapp.dto.user.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-identity", path = "/api")
public interface IdentityFeignClient {

    /**
     *     TEACHER SECTION
      */

    @GetMapping("/teachers/{id}")
    TeacherDTO getTeacherById(@PathVariable Long id);

    @PostMapping("/admin/teachers")
    TeacherDTO registerTeacher(@RequestBody TeacherRegistrationDTO dto);

    @PostMapping("/admin/teachers/{id}/update")
    TeacherDTO updateTeacher(@PathVariable Long id, @RequestBody TeacherUpdateDTO teacherUpdateDTO);

    @PostMapping("/admin/teachers/{id}/disable")
    UserStatusResponseDTO disableTeacher(@PathVariable("id") Long id);

    @PostMapping("/admin/teachers/{id}/enable")
    UserStatusResponseDTO enableTeacher(@PathVariable("id") Long id);

    @DeleteMapping("/admin/teachers/{id}/delete")
    void deleteTeacher(@PathVariable("id") Long id);

    /**
     *     USER SECTION
      */

    @GetMapping("/admin/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    @PostMapping("/admin/users")
    UserDTO registerUser(@RequestBody UserCreationDTO userCreationDTO);

    @PostMapping("/admin/users/{id}/update")
    UserDTO updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateDTO userUpdateDTO);

    @PostMapping("/admin/users/{id}/disable")
    UserStatusResponseDTO disableUser(@PathVariable("id") Long id);

    @PostMapping("/admin/users/{id}/enable")
    UserStatusResponseDTO enableUser(@PathVariable("id") Long id);

    @DeleteMapping("/admin/users/{id}/delete")
    void deleteUser(@PathVariable("id") Long id);

    @PostMapping("/admin/users/{id}/credits/add")
    void addUserCredits(@PathVariable("id") Long id, @RequestParam Integer credits);

    /**
     *     SESSION USERS SECTION
     */

    @PostMapping("/admin/users/list")
    List<UserParticipantDTO> getUsersByIds(@RequestBody List<Long> userIds);


    /**
     *     AUTHENTICATION SECTION
      */

    // Authentication request
    @PostMapping("/authenticate")
    ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO authRequest);

    // Registration request
    @PostMapping("/register")
    ResponseEntity<RegisterResponseDTO> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO);

    // Return UserDto (sessionUser)
    @GetMapping("/user")
    UserDTO findUserDtoByEmail(@RequestParam String email);

    // Returns list of all Users
    @GetMapping("/users")
    List<UserDTO> getAllUsers();

}
