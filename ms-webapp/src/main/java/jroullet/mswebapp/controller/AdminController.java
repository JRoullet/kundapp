package jroullet.mswebapp.controller;

import feign.FeignException;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.TeacherDTO;
import jroullet.mswebapp.dto.TeacherRegistrationDTO;
import jroullet.mswebapp.dto.TeacherUpdateDTO;
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

    /**
     * Create a new teacher
     */
    @PostMapping("/teachers")
    public ModelAndView createTeacher (@ModelAttribute TeacherRegistrationDTO teacherRegistrationDTO, RedirectAttributes redirectAttributes) {
        try {
            TeacherDTO createdUser = identityFeignClient.registerTeacher(teacherRegistrationDTO);
            redirectAttributes.addFlashAttribute("success",
                    "Teacher créé avec succès : " + createdUser.getFirstName() + " " + createdUser.getLastName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la création du teacher : " + e.getMessage());
        }
        return new ModelAndView("redirect:/admin");
    }


    /**
     * GET - Récupérer un teacher pour pré-remplir le modal
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
     * POST - Mettre à jour un teacher (simplifié)
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
        } catch (Exception e) {
            logger.error("Error updating teacher {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification du teacher");
        }
        return new ModelAndView("redirect:/admin");
    }






//    /**
//     * Update existing user (partial update)
//     * PATCH /admin/users/{id}
//     */
//    @PatchMapping("/users/{id}")
//    public ModelAndView updateUser(@PathVariable Long id, @ModelAttribute UserDTO userDTO,
//                                   RedirectAttributes redirectAttributes) {
//        try {
//            userDTO.setId(id);
//            UserDTO updatedUser = identityFeignClient.patchUser(id, userDTO);
//            redirectAttributes.addFlashAttribute("success", "Utilisateur modifié avec succès");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error",
//                    "Erreur lors de la modification : " + e.getMessage());
//        }
//        return new ModelAndView("redirect:/admin");
//    }
//
//    /**
//     * Disable user (partial update - set status = false)
//     * PATCH /admin/users/{id}/disable
//     */
//    @PatchMapping("/users/{id}/disable")
//    public ModelAndView disableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        try {
//            identityFeignClient.disableUser(id);
//            redirectAttributes.addFlashAttribute("success", "Utilisateur désactivé avec succès");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error",
//                    "Erreur lors de la désactivation : " + e.getMessage());
//        }
//        return new ModelAndView("redirect:/admin");
//    }
//
//    /**
//     * Delete user permanently
//     * DELETE /admin/users/{id}
//     */
//    @DeleteMapping("/users/{id}")
//    public ModelAndView deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        try {
//            identityFeignClient.deleteUser(id);
//            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error",
//                    "Erreur lors de la suppression : " + e.getMessage());
//        }
//        return new ModelAndView("redirect:/admin");
//    }
//
//    /**
//     * Update user credits (partial update for CLIENT role only)
//     * PATCH /admin/users/{id}/credits
//     */
//    @PatchMapping("/users/{id}/credits")
//    public ModelAndView updateUserCredits(@PathVariable Long id, @RequestParam Integer credits,
//                                          RedirectAttributes redirectAttributes) {
//        try {
//            identityFeignClient.updateUserCredits(id, credits);
//            redirectAttributes.addFlashAttribute("success",
//                    "Crédits mis à jour avec succès : " + credits + " crédits");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error",
//                    "Erreur lors de la mise à jour des crédits : " + e.getMessage());
//        }
//        return new ModelAndView("redirect:/admin");
//    }
//
//    /**
//     * Change admin password (partial update)
//     * PATCH /admin/password
//     */
//    @PatchMapping("/password")
//    public ModelAndView changeAdminPassword(@RequestParam String currentPassword,
//                                            @RequestParam String newPassword,
//                                            @RequestParam String confirmPassword,
//                                            RedirectAttributes redirectAttributes,
//                                            HttpServletRequest request) {
//        try {
//            // Validate passwords match
//            if (!newPassword.equals(confirmPassword)) {
//                redirectAttributes.addFlashAttribute("error",
//                        "Les mots de passe ne correspondent pas");
//                return new ModelAndView("redirect:/admin");
//            }
//
//            // Get current user from session
//            UserDTO currentUser = sessionService.getCurrentUser();
//            if (currentUser == null) {
//                redirectAttributes.addFlashAttribute("error", "Session expirée");
//                return new ModelAndView("redirect:/login");
//            }
//
//            // Change password using existing FeignClient method
//            identityFeignClient.changeUserPassword(currentUser.getId(), currentPassword, newPassword);
//
//            redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error",
//                    "Erreur lors du changement de mot de passe : " + e.getMessage());
//        }
//        return new ModelAndView("redirect:/admin");
//    }
}
