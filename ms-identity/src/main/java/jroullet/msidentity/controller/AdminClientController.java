//package jroullet.msidentity.controller;
//
//import jroullet.msidentity.service.ClientService;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
////import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/admin/clients")
//@RequiredArgsConstructor
////@PreAuthorize("hasRole('ADMIN')")
//public class AdminClientController {
//
//    private final static Logger logger = LoggerFactory.getLogger(AdminClientController.class);
//    private final ClientService clientService;
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
//        logger.info("Admin deleting client with id: {}", id);
//        try {
//            clientService.deleteClient(id);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            logger.error("Error deleting client: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//}