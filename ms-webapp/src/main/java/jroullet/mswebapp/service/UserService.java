package jroullet.mswebapp.service;


import feign.FeignException;
import jroullet.mswebapp.clients.IdentityFeignClient;
import jroullet.mswebapp.dto.SignUpForm;
import jroullet.mswebapp.dto.EmailDto;
import jroullet.mswebapp.model.Role;
import jroullet.mswebapp.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final IdentityFeignClient identityFeignClient;
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    public Optional<User> findByEmail(EmailDto email){
        logger.info("findByEmail: " + email);
        try{
            User user = identityFeignClient.findUserByEmail(email);
            logger.info("User found: " + (user != null ? user.getEmail() : "null"));
            return Optional.ofNullable(user);
        }
        catch (FeignException.NotFound e){
            logger.info("User not found: " + email);
            return Optional.empty();
        }
        catch (Exception e){
            logger.error("Error while finding user: " + email, e);
            throw e;
        }

    }

    public User registration(SignUpForm form){

        try{
            Optional<User> existingUser = findByEmail(new EmailDto(form.getEmail()));
            if(existingUser.isPresent()){
                logger.info("User already exists: " + existingUser);
                throw new RuntimeException("Email already exists");
            }
            User newUser = new User();
            newUser.setEmail(form.getEmail());
            newUser.setPassword(passwordEncoder.encode(form.getPassword()));
            newUser.setRole(Role.valueOf("CLIENT"));


            return identityFeignClient.createUser(newUser);
        }
        catch(Exception e){
            logger.info("Error creating user: " + e);
                throw e;

        }
    }
}

