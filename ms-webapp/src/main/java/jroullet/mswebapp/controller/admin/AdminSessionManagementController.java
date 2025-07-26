package jroullet.mswebapp.controller.admin;

import jakarta.validation.Valid;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.dto.session.SessionDTO;
import jroullet.mswebapp.dto.session.SessionUpdateDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import jroullet.mswebapp.exception.BusinessException;
import jroullet.mswebapp.service.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminSessionManagementController {

    private final CourseManagementFeignClient courseFeignClient;
    private final SessionManagementService sessionManagementService;
    private final Logger logger = LoggerFactory.getLogger(AdminTeacherController.class);

    //Admin get session details
    @GetMapping("/sessions/{sessionId}/details")
    @ResponseBody
    public ResponseEntity<SessionDTO> getSessionDetails(@PathVariable Long sessionId) {
        try {
            SessionDTO session = courseFeignClient.getSessionById(sessionId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            logger.error("Error fetching session details for id: {}", sessionId, e);
            return ResponseEntity.notFound().build();
        }
    }

    //Admin update session endpoint
    @PostMapping("/sessions/{sessionId}/update")
    public ModelAndView updateSession(@PathVariable Long sessionId,
                                      @ModelAttribute @Valid SessionUpdateDTO sessionUpdateDTO,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erreur de validation des données");
            return new ModelAndView("redirect:/admin?tab=sessions");
        }

        try {
            courseFeignClient.updateSessionByAdmin(sessionId, sessionUpdateDTO);
            redirectAttributes.addFlashAttribute("success", "Session mise à jour avec succès");
            return new ModelAndView("redirect:/admin?tab=sessions");
        } catch (Exception e) {
            logger.error("Error updating session: {}", sessionId, e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de la session");
            return new ModelAndView("redirect:/admin?tab=sessions");
        }
    }

    @PostMapping("/sessions/cancel")
    public ModelAndView cancelSession(@RequestParam Long sessionId, RedirectAttributes redirectAttributes) {
        try {
            sessionManagementService.cancelSessionForAdmin(sessionId);
            redirectAttributes.addFlashAttribute("success", "Séance annulée avec succès");
        } catch (SecurityException | BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return new ModelAndView("redirect:/admin?tab=sessions");
    }

    @GetMapping("/sessions/{sessionId}/participants")
    @ResponseBody
    public List<UserParticipantDTO> getSessionParticipants(@PathVariable Long sessionId) {
        logger.info("Fetching session participants for session: {}", sessionId);
        return sessionManagementService.getSessionParticipants(sessionId);
    }
}
