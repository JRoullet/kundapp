package jroullet.mswebapp.controller.user;

import jroullet.mswebapp.dto.session.SessionNoParticipantsDTO;
import jroullet.mswebapp.service.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/client/sessions")
@RequiredArgsConstructor

public class UserSessionManagementController {

    private final SessionManagementService sessionManagementService;

    @GetMapping("/available")
    @ResponseBody
    public ResponseEntity<List<SessionNoParticipantsDTO>> getAvailableSessions() {
        List<SessionNoParticipantsDTO> sessions = sessionManagementService.getAvailableSessionsForClient();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/upcoming")
    @ResponseBody
    public ResponseEntity<List<SessionNoParticipantsDTO>> getUpcomingSessions() {
        List<SessionNoParticipantsDTO> sessions = sessionManagementService.getUpcomingSessionsForClient();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<List<SessionNoParticipantsDTO>> getHistorySessions() {
        List<SessionNoParticipantsDTO> sessions = sessionManagementService.getPastSessionsForClient();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Register current user to a session
     */
    @PostMapping("/{sessionId}/register")
    public ModelAndView registerToSession(@PathVariable Long sessionId,
                                          RedirectAttributes redirectAttributes) {
        try {
            int newCredits = sessionManagementService.registerToSession(sessionId);
            redirectAttributes.addFlashAttribute("success", "Inscription confirmée ! Votre place est réservée.");
            redirectAttributes.addFlashAttribute("creditsOperation", "registration");
            redirectAttributes.addFlashAttribute("newCredits", newCredits);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Inscription impossible : " + e.getMessage());
        }

        return new ModelAndView("redirect:/client?tab=upcoming");
    }

    /**
     * Unregister current user from a session - Thymeleaf form POST
     */
    @PostMapping("/{sessionId}/unregister")
    public ModelAndView unregisterFromSession(@PathVariable Long sessionId,
                                              RedirectAttributes redirectAttributes) {
        try {
            int newCredits = sessionManagementService.unregisterFromSession(sessionId);
            redirectAttributes.addFlashAttribute("success",
                    "Désinscription confirmée ! Vos crédits ont été remboursés.");
            redirectAttributes.addFlashAttribute("creditsOperation", "unregistration");
            redirectAttributes.addFlashAttribute("newCredits", newCredits);


        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Annulation impossible : " + e.getMessage());
        }

        return new ModelAndView("redirect:/client?tab=upcoming");
    }

}
