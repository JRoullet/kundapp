package jroullet.mswebapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jroullet.mswebapp.auth.AuthResponseDTO;
import jroullet.mswebapp.model.User;
import jroullet.mswebapp.auth.SessionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SessionService sessionService;
//    private final IdentityFeignClient identityFeignClient;
    private final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    public String showClientHome(Model model, HttpServletRequest request) {
        AuthResponseDTO dto = (AuthResponseDTO) request.getSession().getAttribute("currentUser");
        if (dto != null) {
            model.addAttribute("userEmail", dto.getEmail());
            // éventuellement : appel Feign pour récupérer les détails métier
        }
        return "home-client";
    }

    @GetMapping("/admin/home")
    public String showAdminPage(Model model) {
        User user = sessionService.getCurrentUser();
//        List<Patient> patients = identityFeignClient.findAll();
//        model.addAttribute("patients", patients);
        model.addAttribute("user", user);

        return "home-admin";
    }

    @GetMapping("/teacher/home")
    public String showTeacherHome(Model model) {
        User user = sessionService.getCurrentUser();
//        List<Patient> patients = identityFeignClient.findAll();
//        model.addAttribute("patients", patients);
        model.addAttribute("user", user);

        return "home-teacher";
    }

}
