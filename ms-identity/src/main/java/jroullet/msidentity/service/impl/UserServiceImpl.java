package jroullet.msidentity.service.impl;

import jroullet.msidentity.dto.teacher.TeacherDTO;
import jroullet.msidentity.dto.user.*;
import jroullet.msidentity.exception.EmailAlreadyExistsException;
import jroullet.msidentity.exception.RoleNotAllowedException;
import jroullet.msidentity.exception.UserNotFoundException;
import jroullet.msidentity.mapper.UserMapper;
import jroullet.msidentity.model.Role;
import jroullet.msidentity.model.User;
import jroullet.msidentity.repository.UserRepository;
import jroullet.msidentity.service.UserService;
import jroullet.msidentity.service.utils.UpdateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UpdateUtils updateUtils;


    @Override
    public List<TeacherDTO> findAllTeachers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.TEACHER)
                .map(userMapper::toTeacherDto)
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

    @Override
    public List<UserParticipantDTO> findAllParticipants(List<Long> userIds) {
        return userRepository.findParticipantsByIds(userIds)
                .stream()
                .map(userMapper::toUserParticipantDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (user.getRole() != Role.CLIENT) {
            throw new RoleNotAllowedException("User with id " + id + " is not a client");
        }
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDTO createUser(UserCreationDTO dto){
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.CLIENT);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setStatus(true);
        user.setCredits(dto.getCredits());

        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);

    }

    @Override
    public UserDTO updateUser(Long id, UserUpdateDTO dto){
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if(existingUser.getRole() != Role.CLIENT) {
            throw new RoleNotAllowedException("User with id " + id + " is not a client");
        }
        //when updating, assert potential new email value is not already taken by another user
        if (dto.getEmail() != null && !dto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new EmailAlreadyExistsException("Email already exists: " + dto.getEmail());
            }
        }
        // Verify null fields keep previous value
        updateUtils.updateUserFields(existingUser, dto);
        User savedUser = userRepository.save(existingUser);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public UserStatusResponseDTO disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: {}" + id));
        if(user.getRole() != Role.CLIENT) {
            throw new RoleNotAllowedException("User with role: " + user.getRole() + " cannot disable this user");
        }
        user.setStatus(false);
        userRepository.save(user);
        return userMapper.toUserStatusResponseDTO(user);
    }

    @Override
    public UserStatusResponseDTO enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if(user.getRole() != Role.CLIENT) {
            throw new RoleNotAllowedException("User with role: " + user.getRole() + " cannot enable this user");
        }
        user.setStatus(true);
        userRepository.save(user);
        return userMapper.toUserStatusResponseDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if(user.getRole() != Role.CLIENT) {
            throw new RoleNotAllowedException("User with role: " + user.getRole() + " cannot delete this user");
        }
        userRepository.delete(user);
    }

    @Override
    public void addUserCredits(Long id, Integer credits) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        if(user.getRole() != Role.CLIENT) {
            throw new RoleNotAllowedException("User with role: " + user.getRole() + " cannot add credits to this user");
        }
        user.setCredits(user.getCredits() + credits);
        userRepository.save(user);
    }

}
