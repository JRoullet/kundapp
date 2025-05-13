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

    // User endpoints
    @PostMapping("/user")
    ResponseEntity<User> findUserByEmail(@RequestBody EmailDto emailDto);


    // Client endpoints
    @PostMapping("/clients")
    ClientResponseDTO createClient(@RequestBody ClientCreateDTO clientDTO);

    @GetMapping("/clients/{id}")
    ClientResponseDTO getClient(@PathVariable Long id);

    @GetMapping("/clients")
    List<ClientResponseDTO> getAllClients();

    @PatchMapping("/clients/{id}")
    ClientPatchDTO patchClient(@PathVariable Long id, @RequestBody ClientPatchDTO clientDTO);

    @PostMapping("/clients/{id}/disable")
    void disableClient(@PathVariable Long id);

    // Admin endpoints
    @DeleteMapping("/admin/clients/{id}")
    void deleteClient(@PathVariable Long id);



    // User endpoints
    @PostMapping("/user/create")
    User createUser(@RequestBody User user);

    @PostMapping("/user/create-admin")
    User createAdmin(@RequestBody User user);

}
