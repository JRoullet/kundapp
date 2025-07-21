package jroullet.mswebapp.controller;

import feign.FeignException;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.teacher.TeacherDTO;
import jroullet.mswebapp.dto.teacher.TeacherRegistrationDTO;
import jroullet.mswebapp.dto.teacher.TeacherUpdateDTO;
import jroullet.mswebapp.dto.user.UserCreationDTO;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.dto.user.UserStatusResponseDTO;
import jroullet.mswebapp.dto.user.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IdentityFeignClient identityFeignClient;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    //Teacher section
    /**
     * Create a new teacher
     */
    @PostMapping("/teachers")
    public ModelAndView createTeacher (@ModelAttribute TeacherRegistrationDTO teacherRegistrationDTO,
                                       RedirectAttributes redirectAttributes) {
        try {
            TeacherDTO createdUser = identityFeignClient.registerTeacher(teacherRegistrationDTO);
            redirectAttributes.addFlashAttribute("success",
                    "Teacher créé avec succès : " + createdUser.getFirstName() + " " + createdUser.getLastName());
        } catch (FeignException.Conflict e) {
            logger.warn("Email already exists for teacher creation: {}", teacherRegistrationDTO.getEmail());
            redirectAttributes.addFlashAttribute("error", "Cet email est déjà utilisé par un autre utilisateur");
        } catch (FeignException.BadRequest e) {
            logger.warn("Bad request for teacher creation: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Données invalides fournies");
        } catch (FeignException e) {
            logger.error("Feign error creating teacher: Status {}, Message: {}", e.status(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur de communication avec le service d'authentification");
        } catch (Exception e) {
            logger.error("Unexpected error creating teacher: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du teacher : " + e.getMessage());
        }
        return new ModelAndView("redirect:/admin");
    }
    /**
     * GET - get teacher to prefill modal
     */
    @GetMapping("/teachers/{id}")
    @ResponseBody
    public ResponseEntity<TeacherDTO> getTeacher(@PathVariable Long id) {
        try {
            TeacherDTO teacher = identityFeignClient.getTeacherById(id);
            return ResponseEntity.ok(teacher);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching teacher {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * POST -update teacher
     */
    @PostMapping("/teachers/{id}/update")
    public ModelAndView updateTeacher(@PathVariable Long id,
                                      @ModelAttribute TeacherUpdateDTO teacherUpdateDTO,
                                      RedirectAttributes redirectAttributes) {
        try {
            TeacherDTO updatedTeacher = identityFeignClient.updateTeacher(id, teacherUpdateDTO);
            redirectAttributes.addFlashAttribute("success",
                    "Teacher modifié avec succès : " + updatedTeacher.getFirstName() + " " + updatedTeacher.getLastName());
        } catch (FeignException.NotFound e) {
            redirectAttributes.addFlashAttribute("error", "Teacher non trouvé");
        } catch (FeignException.Conflict e) {  // Email déjà existant lors de l'update
            redirectAttributes.addFlashAttribute("error", "Cet email est déjà utilisé par un autre utilisateur");
        } catch (FeignException.BadRequest e) {
            redirectAttributes.addFlashAttribute("error", "Données invalides fournies");
        } catch (Exception e) {
            logger.error("Error updating teacher {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification du teacher");
        }
        return new ModelAndView("redirect:/admin");
    }
    /**
     * POST - disable a teacher
     */
    @PostMapping("/teachers/{id}/disable")
    public ModelAndView disableTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            UserStatusResponseDTO response = identityFeignClient.disableTeacher(id);
            String userName = response.getFirstName() + " " + response.getLastName();
            redirectAttributes.addFlashAttribute("success", "Utilisateur " + userName + " désactivé avec succès");
        } catch (FeignException.NotFound e) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
        } catch (FeignException.BadRequest e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de désactiver cet utilisateur");
        } catch (Exception e) {
            logger.error("Error disabling user {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la désactivation");
        }
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/teachers/{id}/enable")
    public ModelAndView enableTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            UserStatusResponseDTO response = identityFeignClient.enableTeacher(id);
            String userName = response.getFirstName() + " " + response.getLastName();
            redirectAttributes.addFlashAttribute("success", "Utilisateur " + userName + " activé avec succès");
        } catch (FeignException.NotFound e) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
        } catch (Exception e) {
            logger.error("Error enabling user {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'activation");
        }
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/teachers/{id}/delete")
    public ModelAndView deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            identityFeignClient.deleteTeacher(id);
            redirectAttributes.addFlashAttribute("success", "Teacher supprimé avec succès");
        } catch (FeignException.NotFound e) {
            redirectAttributes.addFlashAttribute("error", "Teacher non trouvé");
        } catch (Exception e) {
            logger.error("Error deleting teacher {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return new ModelAndView("redirect:/admin");
    }

    // User Section
    @GetMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = identityFeignClient.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/users")
    public ModelAndView createUser (@ModelAttribute UserCreationDTO userCreationDTO,
                                       RedirectAttributes redirectAttributes) {
        try {
            UserDTO createdUser = identityFeignClient.registerUser(userCreationDTO);
            redirectAttributes.addFlashAttribute("success",
                    "User créé avec succès : " + createdUser.getFirstName() + " " + createdUser.getLastName());
        } catch (FeignException.Conflict e) {
            logger.warn("Email already exists for user creation: {}", userCreationDTO.getEmail());
            redirectAttributes.addFlashAttribute("error", "Cet email est déjà utilisé par un autre utilisateur");
        } catch (FeignException.BadRequest e) {
            logger.warn("Bad request for user creation: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Données invalides fournies");
        } catch (FeignException e) {
            logger.error("Feign error creating user: Status {}, Message: {}", e.status(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur de communication avec le service d'authentification");
        } catch (Exception e) {
            logger.error("Unexpected error creating user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du user : " + e.getMessage());
        }
        return new ModelAndView("redirect:/admin?tab=users");
    }

    @PostMapping("/users/{id}/update")
    public ModelAndView updateUser(@PathVariable Long id, @ModelAttribute UserUpdateDTO dto,
                                   RedirectAttributes redirectAttributes) {
        try {
            UserDTO updatedUser = identityFeignClient.updateUser(id, dto);
            redirectAttributes.addFlashAttribute("success", "User " + updatedUser.getFirstName() + " " + updatedUser.getLastName() + " mis à jour avec succès");
        } catch (FeignException.NotFound e) {
            redirectAttributes.addFlashAttribute("error", "User non trouvé");
        } catch (FeignException.Conflict e) {
            redirectAttributes.addFlashAttribute("error", "Cet email est déjà utilisé par un autre utilisateur");
        } catch (FeignException.BadRequest e) {
            redirectAttributes.addFlashAttribute("error", "Données invalides fournies");
        } catch (Exception e) {
            logger.error("Error updating user {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour");
        }
        return new ModelAndView("redirect:/admin?tab=users");
    }
    /**
     * POST - Disable a user
     */
    @PostMapping("/users/{id}/disable")
    public ModelAndView disableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            UserStatusResponseDTO response = identityFeignClient.disableUser(id);
            String userName = response.getFirstName() + " " + response.getLastName();
            redirectAttributes.addFlashAttribute("success", "Utilisateur " + userName + " désactivé avec succès");
        } catch (FeignException.NotFound e) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
        } catch (FeignException.BadRequest e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de désactiver cet utilisateur");
        } catch (Exception e) {
            logger.error("Error disabling user {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la désactivation");
        }
        return new ModelAndView("redirect:/admin?tab=users");
    }

    @PostMapping("/users/{id}/enable")
    public ModelAndView enableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            UserStatusResponseDTO response = identityFeignClient.enableUser(id);
            String userName = response.getFirstName() + " " + response.getLastName();
            redirectAttributes.addFlashAttribute("success", "Utilisateur " + userName + " activé avec succès");
        } catch (FeignException.NotFound e) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
        } catch (Exception e) {
            logger.error("Error enabling user {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'activation");
        }
        return new ModelAndView("redirect:/admin?tab=users");
    }

    @PostMapping("/users/{id}/delete")
    public ModelAndView deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            identityFeignClient.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User supprimé avec succès");
        } catch (FeignException.NotFound e) {
            redirectAttributes.addFlashAttribute("error", "User non trouvé");
        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return new ModelAndView("redirect:/admin?tab=users");
    }

    @PostMapping("/users/{id}/credits/add")
    public ModelAndView addUserCredits(@PathVariable Long id,
                                       @RequestParam Integer credits,
                                       RedirectAttributes redirectAttributes) {
        try {
            identityFeignClient.addUserCredits(id, credits);
            redirectAttributes.addFlashAttribute("success", "Crédits ajoutés avec succès");
        } catch (FeignException.NotFound e) {
            redirectAttributes.addFlashAttribute("error", "User non trouvé");
        } catch (Exception e) {
            logger.error("Error updating credits for user {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout de crédits");
        }
        return new ModelAndView("redirect:/admin?tab=users");
    }

}
