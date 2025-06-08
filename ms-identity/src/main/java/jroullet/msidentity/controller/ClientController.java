//package jroullet.msidentity.controller;
//
//import jakarta.validation.Valid;
//import jroullet.msidentity.dto.ClientPatchDTO;
//import jroullet.msidentity.dto.ClientCreateDTO;
//import jroullet.msidentity.dto.ClientResponseDTO;
//import jroullet.msidentity.service.ClientService;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/clients")
//@RequiredArgsConstructor
//public class ClientController {
//
//    private final static Logger logger = LoggerFactory.getLogger(ClientController.class);
//    private final ClientService clientService;
//
//    @PostMapping
//    public ResponseEntity<?> createClient(@Valid @RequestBody ClientCreateDTO clientDTO) {
//        logger.info("Creating new client: {}", clientDTO.getEmail());
//        try {
//            ClientResponseDTO createdClient = clientService.createClient(clientDTO);
//            return ResponseEntity.ok(createdClient);
//        } catch (Exception e) {
//            logger.error("Error creating client: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getClient(@PathVariable Long id) {
//        logger.info("Getting client with id: {}", id);
//        try {
//            ClientResponseDTO client = clientService.getClient(id);
//            return ResponseEntity.ok(client);
//        } catch (Exception e) {
//            logger.error("Error getting client: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @GetMapping
//    public ResponseEntity<?> getAllClients() {
//        logger.info("Getting all clients");
//        try {
//            List<ClientResponseDTO> clients = clientService.getAllClients();
//            return ResponseEntity.ok(clients);
//        } catch (Exception e) {
//            logger.error("Error getting all clients: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<?> patchClient(
//            @PathVariable Long id,
//            @Valid @RequestBody ClientPatchDTO clientDTO) {
//        logger.info("Patching client with id: {}", id);
//        try {
//            ClientPatchDTO updatedClient = clientService.patchClient(id, clientDTO);
//            return ResponseEntity.ok(updatedClient);
//        } catch (Exception e) {
//            logger.error("Error patching client: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PostMapping("/{id}/disable")
//    public ResponseEntity<?> disableClient(@PathVariable Long id) {
//        logger.info("Disabling client with id: {}", id);
//        try {
//            clientService.disableClient(id);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            logger.error("Error disabling client: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
//        logger.info("Deleting client with id: {}", id);
//        try {
//            clientService.deleteClient(id);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            logger.error("Error deleting client: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//}