package jroullet.mswebapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.session.SessionNoParticipantsDTO;
import jroullet.mswebapp.dto.session.SessionWithParticipantsDTO;
import jroullet.mswebapp.dto.user.UserDTO;
import jroullet.mswebapp.service.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
        List<SessionNoParticipantsDTO> sessions = sessionManagementService.getAvailableSessionsForClient();
        List<SessionNoParticipantsDTO> upcomingSessions = sessionManagementService.getUpcomingSessionsForClient();
        List<SessionNoParticipantsDTO> historySessions = sessionManagementService.getPastSessionsForClient();

        logger.info("User credits loaded: {}", userDTO.getCredits());

        model.addAttribute("user", userDTO);
        model.addAttribute("sessions", sessions);
        model.addAttribute("upcomingSessions", upcomingSessions);
        model.addAttribute("historySessions", historySessions);

        return new ModelAndView("home-client");
    }

    @GetMapping("/admin")
    public ModelAndView showAdminPage(Model model) {
        logger.info("Fetching admin home");
        UserDTO userDTO = sessionService.getCurrentUser();
        List<UserDTO> allUsers = identityFeignClient.getAllUsers();
        List<SessionWithParticipantsDTO> sessions = sessionManagementService.getAllSessionsForAdmin();

        model.addAttribute("user", userDTO);
        model.addAttribute("users", allUsers);
        model.addAttribute("sessions", sessions);

        return new ModelAndView("home-admin");
    }

    @GetMapping("/teacher")
    public ModelAndView showTeacherHome(Model model) {
        logger.info("Fetching teacher home");
        UserDTO userDTO = sessionService.getCurrentUser();
        // Loading upcoming sessions
        List<SessionWithParticipantsDTO> upcomingSessions = sessionManagementService
                .getUpcomingSessionsForCurrentTeacher(userDTO.getId());
        List<SessionWithParticipantsDTO> pastSessions = sessionManagementService
                .getPastSessionsForCurrentTeacher(userDTO.getId());

        model.addAttribute("user", userDTO);
        model.addAttribute("sessions", upcomingSessions);
        model.addAttribute("historySessions", pastSessions);



        return new ModelAndView("home-teacher");
    }

    @GetMapping("/favicon.ico")
    public void favicon(HttpServletResponse response) {
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

}
