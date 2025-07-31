package jroullet.mswebapp.controller.admin;

import feign.FeignException;
import jroullet.mswebapp.clients.IdentityFeignClient;
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
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final IdentityFeignClient identityFeignClient;
    private final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    // User Section
    @GetMapping("/{id}")
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

    @PostMapping
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

    @PostMapping("/{id}/update")
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
    @PostMapping("/{id}/disable")
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

    @PostMapping("/{id}/enable")
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

    @PostMapping("/{id}/delete")
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

    @PostMapping("/{id}/credits/add")
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
