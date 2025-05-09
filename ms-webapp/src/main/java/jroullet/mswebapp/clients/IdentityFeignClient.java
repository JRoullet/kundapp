package jroullet.mswebapp.clients;

import jroullet.mswebapp.dto.ClientCreateDTO;
import jroullet.mswebapp.dto.ClientPatchDTO;
import jroullet.mswebapp.dto.ClientResponseDTO;
import jroullet.mswebapp.dto.EmailDto;
import jroullet.mswebapp.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-identity", path = "/api")
public interface IdentityFeignClient {

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
    @PostMapping("/user")
    User findUserByEmail(@RequestBody EmailDto emailDto);

    @PostMapping("/user/create")
    User createUser(@RequestBody User user);

    @PostMapping("/user/create-admin")
    User createAdmin(@RequestBody User user);
}
