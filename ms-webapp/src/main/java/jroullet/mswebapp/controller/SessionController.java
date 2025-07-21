package jroullet.mswebapp.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.dto.session.SessionCreationDTO;
import jroullet.mswebapp.dto.session.SessionDTO;
import jroullet.mswebapp.model.Subject;
import jroullet.mswebapp.service.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class SessionController {
    private final SessionManagementService sessionManagementService;
    private final SessionService sessionService;
    private final Logger logger = LoggerFactory.getLogger(SessionController.class);


    @GetMapping("/session/new")
    public ModelAndView showCreateSessionForm() {
         return new ModelAndView("teacher/create-session")
                 .addObject("sessionCreationDTO", new SessionCreationDTO())
                 .addObject("subjects", Subject.values());

    }

    @PostMapping("/session/create")
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
}
