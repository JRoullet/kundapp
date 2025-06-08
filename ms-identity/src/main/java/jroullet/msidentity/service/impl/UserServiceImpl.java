package jroullet.msidentity.service.impl;

import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.exception.UserNotFoundException;
import jroullet.msidentity.mapper.UserMapper;
import jroullet.msidentity.model.Role;
import jroullet.msidentity.model.User;
import jroullet.msidentity.repository.UserRepository;
import jroullet.msidentity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDTO> findAllByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findUserDTOByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: {}" + email));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream().map(userMapper::toUserDto)
                .toList();
    }

}
