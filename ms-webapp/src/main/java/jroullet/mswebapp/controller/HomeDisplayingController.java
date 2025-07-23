package jroullet.mswebapp.controller;

import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.session.AdminSessionDTO;
import jroullet.mswebapp.dto.session.SessionDTO;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.service.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

// Home Pages
@Controller
@RequiredArgsConstructor
public class HomeDisplayingController {

    private final SessionService sessionService;
    private final IdentityFeignClient identityFeignClient;
    private final SessionManagementService sessionManagementService;
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
        List<AdminSessionDTO> sessions = sessionManagementService.getAllSessionsForAdmin();

        model.addAttribute("user", userDTO);
        model.addAttribute("users", allUsers);
        model.addAttribute("sessions", sessions);

        return new ModelAndView("home-admin");
    }

    @GetMapping("/teacher")
    public ModelAndView showTeacherHome(Model model) {
        logger.info("Fetching teacher home");

        try {
            UserDTO userDTO = sessionService.getCurrentUser();
            model.addAttribute("user", userDTO);

            // Loading upcoming sessions
            List<SessionDTO> upcomingSessions = sessionManagementService
                    .getUpcomingSessionsForCurrentTeacher(userDTO.getId());
            model.addAttribute("sessions", upcomingSessions);

        } catch (Exception e) {
            logger.error("Error loading teacher home: {}", e.getMessage());
            model.addAttribute("error", "Erreur lors du chargement des sessions");
            model.addAttribute("sessions", Collections.emptyList());
        }

        return new ModelAndView("home-teacher");
    }

}
