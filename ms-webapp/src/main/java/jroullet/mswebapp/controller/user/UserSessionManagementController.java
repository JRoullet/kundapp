package jroullet.mswebapp.controller.user;

import feign.FeignException;
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

        } catch (FeignException e) {
            String errorMessage = switch (e.status()) {
                case 409 -> "Inscription impossible : vous êtes déjà inscrit ou la session est complète";
                case 404 -> "Session introuvable";
                case 403 -> "Vous n'avez pas l'autorisation d'accéder à cette session";
                case 400 -> "Données d'inscription invalides";
                default -> "Erreur lors de l'inscription";
            };
            redirectAttributes.addFlashAttribute("error", errorMessage);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur technique lors de l'inscription");
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

        } catch (FeignException e) {
            String errorMessage = switch (e.status()) {
                case 422 -> "Annulation impossible : délai de 48h dépassé";
                case 409 -> "Vous n'êtes pas inscrit à cette session";
                case 404 -> "Session introuvable";
                default -> "Erreur lors de l'annulation";
            };
            redirectAttributes.addFlashAttribute("error", errorMessage);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur technique lors de l'annulation");
        }
        return new ModelAndView("redirect:/client?tab=upcoming");
    }

}
