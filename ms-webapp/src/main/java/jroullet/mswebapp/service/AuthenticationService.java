package jroullet.mswebapp.service;

import feign.FeignException;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.EmailDto;
import jroullet.mswebapp.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service // Authentication procedure BY SPRING SECURITY OWN MANAGEMENT
public class AuthenticationService implements UserDetailsService {

//    private UserService userService;
    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final IdentityFeignClient identityFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Retrieve user from microservice
            User user = identityFeignClient.findUserByEmail(new EmailDto(username));
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            // Converts User to UserDetails
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(String.valueOf(user.getRole()))
                    .build();
        } catch (FeignException.NotFound e) {
            logger.info("User not found: " + username);
            throw new UsernameNotFoundException("User not found: " + username, e);
        } catch (Exception e) {
            logger.error("Error loading user: " + username, e);
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }

}
