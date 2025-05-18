package jroullet.msidentity.controller;

import jroullet.msidentity.auth.EmailDto;
import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.mapper.UserMapper;
import jroullet.msidentity.model.User;
import jroullet.msidentity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserMapper userMapper;

    @PostMapping("/api/user")
    ResponseEntity<User> findUserByEmail(@RequestBody EmailDto emailDto) {
        logger.info("Looking for user by email : " + emailDto.getEmail());
        User user = userService.findUserByEmail(emailDto.getEmail());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Return UserDto (sessionUser)
    @PostMapping("/user")
    UserDTO findUserDtoByEmail(EmailDto emailDto){
        logger.info("Looking for user by email : " + emailDto.getEmail());
        User user = userService.findUserByEmail(emailDto.getEmail());
        return userMapper.toUserDto(user);
    }

}
