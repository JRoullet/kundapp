package jroullet.mswebapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jroullet.mswebapp.auth.SessionService;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.math.raw.Mod;
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
public class HomeController {

    private final SessionService sessionService;
    private final IdentityFeignClient identityFeignClient;
    private final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/client")
    public ModelAndView showClientHome(Model model, HttpServletRequest request) {
        UserDTO userDTO = sessionService.getCurrentUser();
        List<UserDTO> allUsers = identityFeignClient.getAllUsers();
        model.addAttribute("user", userDTO);
        model.addAttribute("users", allUsers);

        return new ModelAndView("home-client");
    }

    @GetMapping("/admin")
    public ModelAndView showAdminPage(Model model) {
        UserDTO userDTO = sessionService.getCurrentUser();
        List<UserDTO> allUsers = identityFeignClient.getAllUsers();
        model.addAttribute("user", userDTO);
        model.addAttribute("users", allUsers);

        return new ModelAndView("home-admin");
    }

    @GetMapping("/teacher")
    public ModelAndView showTeacherHome(Model model) {
        UserDTO userDTO = sessionService.getCurrentUser();
        List<UserDTO> allUsers = identityFeignClient.getAllUsers();
        model.addAttribute("user", userDTO);
        model.addAttribute("users", allUsers);

        return new ModelAndView("home-teacher");
    }

}
