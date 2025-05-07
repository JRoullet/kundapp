package jroullet.mswebapp.controller;

import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.model.User;
import jroullet.mswebapp.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SessionService sessionService;
//    private final IdentityFeignClient identityFeignClient;
    private final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/home")
    public String showHomeView(Model model) {
        User user = sessionService.sessionUser();
//        List<Patient> patients = identityFeignClient.findAll();
//        model.addAttribute("patients", patients);
        model.addAttribute("user", user);

        return "home";
    }

    @GetMapping("/admin")
    public String showAdminPage(Model model) {
        User user = sessionService.sessionUser();
//        List<Patient> patients = identityFeignClient.findAll();
//        model.addAttribute("patients", patients);
        model.addAttribute("user", user);

        return "admin-dashboard";
    }

}
