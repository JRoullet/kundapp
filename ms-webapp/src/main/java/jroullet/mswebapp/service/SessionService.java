package jroullet.mswebapp.service;


import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.EmailDto;
import jroullet.mswebapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final IdentityFeignClient identityFeignClient;

    public User sessionUser() {
        org.springframework.security.core.userdetails.User springUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return identityFeignClient.findUserByEmail(new EmailDto(springUser.getUsername()));
    }


}
