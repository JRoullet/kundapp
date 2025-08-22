package jroullet.mswebapp.controller.admin;

import feign.FeignException;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.teacher.TeacherDTO;
import jroullet.mswebapp.dto.teacher.TeacherRegistrationDTO;
import jroullet.mswebapp.dto.teacher.TeacherUpdateDTO;
import jroullet.mswebapp.dto.user.*;
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
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
public class AdminTeacherController {

    private final IdentityFeignClient identityFeignClient;
    private final Logger logger = LoggerFactory.getLogger(AdminTeacherController.class);


    /**
     * Create a new teacher
     */
    @PostMapping
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
    @GetMapping("/{id}")
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

    @PostMapping("/{id}/update")
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

    @PostMapping("/{id}/disable")
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

    @PostMapping("/{id}/enable")
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

    @PostMapping("/{id}/delete")
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

}
