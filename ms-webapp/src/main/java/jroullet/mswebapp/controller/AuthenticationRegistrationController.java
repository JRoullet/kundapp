package jroullet.mswebapp.controller;

import feign.FeignException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jroullet.mswebapp.auth.AuthRequestDTO;
import jroullet.mswebapp.auth.RegisterRequestDTO;
import jroullet.mswebapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling user authentication and registration.
 */
@RestController
public class AuthenticationRegistrationController {

    public AuthenticationRegistrationController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;
    private final static Logger logger = LoggerFactory.getLogger(AuthenticationRegistrationController.class);

    // Display sign in form
    @GetMapping("/signin")
    public ModelAndView showSignInView(@RequestParam(value = "authError", required = false) String authError,
                                       HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("signin", "signInForm", new AuthRequestDTO());

        // Retrieve error from session if exists
        String sessionError = (String) session.getAttribute("authError");
        if (sessionError != null) {
            modelAndView.addObject("authError", sessionError);
            session.removeAttribute("authError");
        } else if (authError != null) {
            modelAndView.addObject("authError", authError);
        }
        return modelAndView;
        //PostMapping /authenticate is done through FeignClient and calling authenticate method from AuthenticationProvider
    }

    // Display sign up form
    @GetMapping("/signup")
    public ModelAndView showSignUpView() {
        return new ModelAndView("signup", "signupform", new RegisterRequestDTO());
    }

    // Submit sign up form
    @PostMapping("/signup")
    public ModelAndView processSignUp(@Valid @ModelAttribute("signupform") RegisterRequestDTO form,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {
        logger.info("processSignUp() called");

        if (result.hasErrors()) {
            logger.warn("Validation errors in signup form: {}", result.getAllErrors());
            return new ModelAndView("signup", "signupform", form);
        }

        try {
            userService.registration(form);
            redirectAttributes.addFlashAttribute("successMessage", "Enregistrement réussi!");
            return new ModelAndView("redirect:/signin");
        } catch (FeignException e) {
            logger.error("FeignException during signup: HTTP {}, Message: {}", e.status(), e.getMessage());

            String errorMessage = switch (e.status()) {
                case 409 -> "Impossible d'enregistrer : email déjà utilisé";
                case 403 -> "Rôle non autorisé pour l'inscription";
                case 404 -> "Service d'inscription indisponible";
                case 400 -> "Données d'inscription invalides";
                default -> "Erreur lors de l'inscription";
            };
            redirectAttributes.addFlashAttribute("authError", errorMessage);

        } catch (RuntimeException e) {
            logger.error("Unexpected error during signup: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("authError", e.getMessage());
        }
        return new ModelAndView("redirect:/signin");
    }

}
