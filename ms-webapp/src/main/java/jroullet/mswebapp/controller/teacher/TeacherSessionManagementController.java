package jroullet.mswebapp.controller.teacher;

import feign.FeignException;
import jakarta.validation.Valid;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.dto.session.SessionCreationDTO;
import jroullet.mswebapp.dto.session.SessionDTO;
import jroullet.mswebapp.dto.session.SessionUpdateDTO;
import jroullet.mswebapp.exception.BusinessException;
import jroullet.mswebapp.model.Subject;
import jroullet.mswebapp.service.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherSessionManagementController {
    private final SessionManagementService sessionManagementService;
    private final SessionService sessionService;
    private final CourseManagementFeignClient courseFeignClient;
    private final Logger logger = LoggerFactory.getLogger(TeacherSessionManagementController.class);

    @GetMapping("/sessions/{sessionId}/details")
    @ResponseBody
    public ResponseEntity<SessionDTO> getSessionDetails(@PathVariable Long sessionId) {
        try {
            Long teacherId = sessionService.getCurrentUser().getId();
            SessionDTO session = courseFeignClient.getSessionById(sessionId);

            //Teacher can only access their own sessions
            if (!session.getTeacherId().equals(teacherId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(session);
        } catch (Exception e) {
            logger.error("Error fetching session details for id: {}", sessionId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/sessions/new")
    public ModelAndView showCreateSessionForm() {
         return new ModelAndView("teacher/create-session")
                 .addObject("sessionCreationDTO", new SessionCreationDTO())
                 .addObject("subjects", Subject.values());

    }

    @PostMapping("/sessions/create")
    public ModelAndView createSession(@ModelAttribute @Valid SessionCreationDTO sessionCreationDTO,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("teacher/create-session")
                    .addObject("sessionCreationDTO", sessionCreationDTO)
                    .addObject("subjects", Subject.values());
        }

        try{
            // Gets actual user Id
            Long teacherId = sessionService.getCurrentUser().getId();
            SessionDTO createdSession = sessionManagementService.createSessionForCurrentTeacher(teacherId, sessionCreationDTO);
            redirectAttributes.addFlashAttribute("success",
                    "Session créée avec succès : " + createdSession.getSubject() + " le " +
                            createdSession.getStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
        } catch (FeignException.Conflict e) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous avez déjà une session programmée à ces horaires");
        } catch (FeignException.BadRequest e) {
            redirectAttributes.addFlashAttribute("error",
                    "Données invalides. Vérifiez les informations saisies");
        } catch (Exception e) {
            logger.error("Error creating session: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la création de la session");
        }

        return new ModelAndView("redirect:/teacher");
    }

    @PostMapping("/sessions/cancel")
    public ModelAndView cancelSession(@RequestParam Long sessionId, RedirectAttributes redirectAttributes) {
        try {
            sessionManagementService.cancelSessionForCurrentTeacher(sessionId);
            redirectAttributes.addFlashAttribute("success", "Séance annulée avec succès");
        } catch (SecurityException | BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return new ModelAndView("redirect:/teacher");
    }

    @PostMapping("/sessions/{sessionId}/update")
    public ModelAndView updateSession(@PathVariable Long sessionId,
                                      @ModelAttribute @Valid SessionUpdateDTO sessionUpdateDTO,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erreur de validation des données");
            return new ModelAndView("redirect:/admin");
        }

        try {
            Long teacherId = sessionService.getCurrentUser().getId();
            courseFeignClient.updateSessionByTeacher(sessionId, teacherId, sessionUpdateDTO);
            redirectAttributes.addFlashAttribute("success", "Session mise à jour avec succès");
            return new ModelAndView("redirect:/teacher");
        } catch (Exception e) {
            logger.error("Error updating session: {}", sessionId, e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de la session");
            return new ModelAndView("redirect:/teacher");
        }
    }
}
