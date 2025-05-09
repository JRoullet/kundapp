package jroullet.mswebapp.controller;

import jakarta.validation.Valid;
import jroullet.mswebapp.dto.SignInForm;
import jroullet.mswebapp.dto.SignUpForm;
import jroullet.mswebapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    // SPRING SECURITY OWN MANAGEMENT

    @GetMapping("/signin")
    public ModelAndView showSignInView(@RequestParam(value = "authError", required = false) String authError) {
        ModelAndView modelAndView = new ModelAndView("signin", "signInForm", new SignInForm());
        if (authError != null) {
            modelAndView.addObject("authError", authError);
        }
        return modelAndView;
    }

    @GetMapping("/signup")
    public ModelAndView showSignUpView() {
        return new ModelAndView("signup", "signUpForm", new SignUpForm());
    }

    @PostMapping("/signup")
    public ModelAndView processSignUp(@Valid @ModelAttribute("signUpForm") SignUpForm form,
                                BindingResult result, RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            return new ModelAndView("signup", "signUpForm", form);
        }
        try {
            userService.registration(form);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please sign in.");
            return new ModelAndView("redirect:/signin");
        } catch(RuntimeException e) {
            logger.error("Registration failed: ", e);
            redirectAttributes.addFlashAttribute("authError", "Registration failed: " + e.getMessage());
            return new ModelAndView("redirect:/signin");
        }
    }

    @GetMapping("/logout")
    public ModelAndView showLogout() {
        return new ModelAndView("logout");
    }
}
