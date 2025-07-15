package jroullet.mswebapp.controller;

import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

// Home Pages
@Controller
@RequiredArgsConstructor
public class HomeDisplayingController {

    private final SessionService sessionService;
    private final IdentityFeignClient identityFeignClient;
    private final static Logger logger = LoggerFactory.getLogger(HomeDisplayingController.class);

    @GetMapping("/client")
    public ModelAndView showClientHome(Model model) {
        logger.info("Fetching client home");
        UserDTO userDTO = sessionService.getCurrentUser();
        List<UserDTO> allUsers = identityFeignClient.getAllUsers();
        model.addAttribute("user", userDTO);
        model.addAttribute("users", allUsers);

        return new ModelAndView("home-client");
    }

    @GetMapping("/admin")
    public ModelAndView showAdminPage(Model model) {
        logger.info("Fetching admin home");
        UserDTO userDTO = sessionService.getCurrentUser();
        List<UserDTO> allUsers = identityFeignClient.getAllUsers();
        model.addAttribute("user", userDTO);
        model.addAttribute("users", allUsers);

        return new ModelAndView("home-admin");
        // A modifier pour aller sur admin-users et créer la page admin-users pour compléter la vue admin.
        // Créer les endpoints update, disable et delete pour un teacher et pour un user.
        // Ensuite créer la vue Teacher et permettre de créer une séance.
    }

    @GetMapping("/teacher")
    public ModelAndView showTeacherHome(Model model) {
        logger.info("Fetching teacher home");
        UserDTO userDTO = sessionService.getCurrentUser();
        List<UserDTO> allUsers = identityFeignClient.getAllUsers();
        model.addAttribute("user", userDTO);
        model.addAttribute("users", allUsers);

        return new ModelAndView("home-teacher");
    }

}
