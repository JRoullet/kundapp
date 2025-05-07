package jroullet.msidentity.controller;

import jroullet.msidentity.dto.EmailDto;
import jroullet.msidentity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> findUserByEmail(@RequestBody EmailDto emailDto) {
        userRepository.findUserByEmail(emailDto.getEmail());
        return new ResponseEntity<>(emailDto.getEmail(), HttpStatus.OK);
    }

}
