package jroullet.msidentity.service.impl;

import jroullet.msidentity.model.User;
import jroullet.msidentity.repository.UserRepository;
import jroullet.msidentity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Utilisateur non trouvé avec l’email : " + email
        ));
    }
}
