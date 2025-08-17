package jroullet.mswebapp.controller.teacher;

import feign.FeignException;
import jakarta.validation.Valid;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.CourseManagementFeignClient;
import jroullet.mswebapp.dto.session.create.SessionCreationDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationResponseDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationWithTeacherDTO;
import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.session.SessionUpdateDTO;
import jroullet.mswebapp.dto.user.UserParticipantDTO;
import jroullet.mswebapp.exception.BusinessException;
import jroullet.mswebapp.exception.UnauthorizedSessionAccessException;
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
import java.util.List;

@Controller
@RequestMapping("/teacher/sessions")
@RequiredArgsConstructor
public class TeacherSessionManagementController {
    private final SessionManagementService sessionManagementService;
    private final SessionService sessionService;
    private final CourseManagementFeignClient courseFeignClient;
    private final Logger logger = LoggerFactory.getLogger(TeacherSessionManagementController.class);

    @GetMapping("/{sessionId}/details")
    @ResponseBody
    public ResponseEntity<SessionWithParticipantsDTO> getSessionDetails(@PathVariable Long sessionId) {
        try {
            Long teacherId = sessionService.getCurrentUser().getId();
            SessionWithParticipantsDTO session = courseFeignClient.getSessionById(sessionId);

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

    @GetMapping("/{sessionId}/participants")
    @ResponseBody
    public List<UserParticipantDTO> getSessionParticipants(@PathVariable Long sessionId) {
        try {
            return sessionManagementService.getSessionParticipantsForTeacher(sessionId);
        } catch (SecurityException e) {
            logger.error("Security violation: Teacher tried to access unauthorized session {}", sessionId);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching participants for session {}: {}", sessionId, e.getMessage());
            throw e;
        }
    }

    /**
     * TEACHER VIEWS
     */
    @GetMapping("/new")
    public ModelAndView showCreateSessionForm() {
         return new ModelAndView("teacher/create-session")
                 .addObject("sessionCreationDTO", new SessionCreationWithTeacherDTO())
                 .addObject("subjects", Subject.values());

    }

    @PostMapping("/create")
    public ModelAndView createSession(@ModelAttribute @Valid SessionCreationDTO sessionCreationDTO,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("home-teacher")
                    .addObject("sessionCreationDTO", sessionCreationDTO)
                    .addObject("subjects", Subject.values());
        }

        try{
            // Gets actual user Id
            SessionCreationResponseDTO createdSession = sessionManagementService.createSessionForCurrentTeacher(sessionCreationDTO);
            redirectAttributes.addFlashAttribute("success",
                    "Session créée avec succès : " + createdSession.getSessionId() + " le " +
                            createdSession.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
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

    @PostMapping("/cancel")
    public ModelAndView cancelSession(@RequestParam Long sessionId, RedirectAttributes redirectAttributes) {
        try {
            sessionManagementService.cancelSessionForCurrentTeacher(sessionId);
            redirectAttributes.addFlashAttribute("success", "Séance annulée avec succès");
        } catch (SecurityException | BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return new ModelAndView("redirect:/teacher");
    }

    @PostMapping("/{sessionId}/update")
    public ModelAndView updateSession(@PathVariable Long sessionId,
                                      @ModelAttribute @Valid SessionUpdateDTO sessionUpdateDTO,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erreur de validation des données");
            return new ModelAndView("redirect:/teacher");
        }

        try {
            sessionManagementService.updateSessionForCurrentTeacher(sessionId, sessionUpdateDTO);
            redirectAttributes.addFlashAttribute("success", "Session mise à jour avec succès");
            return new ModelAndView("redirect:/teacher");
        } catch (UnauthorizedSessionAccessException e) {
            logger.error("Unauthorized access to session {}: {}", sessionId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Accès non autorisé à cette session");
            return new ModelAndView("redirect:/teacher");
        } catch (FeignException e) {
            logger.error("Error updating session {}: {}", sessionId, e.getMessage());
            String errorMessage = switch (e.status()) {
                case 400, 422 -> {
                    if (e.getMessage().contains("Cannot modify credits")) {
                        yield "Impossible de modifier les crédits : des participants sont déjà inscrits";
                    }
                    yield "Données de session invalides";
                }
                case 404 -> "Session non trouvée";
                default -> "Erreur lors de la mise à jour de la session";
            };

            redirectAttributes.addFlashAttribute("error", errorMessage);
            return new ModelAndView("redirect:/teacher");

        } catch (Exception e) {
            logger.error("Unexpected error updating session {}: {}", sessionId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur technique inattendue");
            return new ModelAndView("redirect:/teacher");
        }





    }


}
