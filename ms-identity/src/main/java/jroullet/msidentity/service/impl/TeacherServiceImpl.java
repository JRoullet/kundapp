package jroullet.msidentity.service.impl;

import jroullet.msidentity.dto.*;
import jroullet.msidentity.exception.EmailAlreadyExistsException;
import jroullet.msidentity.exception.RoleNotAllowedException;
import jroullet.msidentity.exception.UserNotFoundException;
import jroullet.msidentity.mapper.UserMapper;
import jroullet.msidentity.model.Role;
import jroullet.msidentity.model.User;
import jroullet.msidentity.repository.UserRepository;
import jroullet.msidentity.service.TeacherService;
import jroullet.msidentity.service.utils.UpdateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UpdateUtils updateUtils;

    @Override
    public TeacherDTO registerTeacher(TeacherRegistrationDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User teacher = new User();
        teacher.setEmail(dto.getEmail());
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        teacher.setRole(Role.TEACHER);
        teacher.setFirstName(dto.getFirstName());
        teacher.setLastName(dto.getLastName());
        teacher.setPhoneNumber(dto.getPhoneNumber());
        teacher.setDateOfBirth(dto.getDateOfBirth());
        teacher.setAddress(dto.getAddress());
        teacher.setStatus(true);
        teacher.setBiography(dto.getBiography());

        User savedTeacher = userRepository.save(teacher);
        return userMapper.toTeacherDto(savedTeacher);
    }

    @Override
    public TeacherDTO findTeacherById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (user.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("User with id " + id + " is not a teacher");
        }
        return userMapper.toTeacherDto(user);
    }

    @Override
    public TeacherDTO updateTeacher(Long id, TeacherUpdateDTO dto) {
        User existingTeacher = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if(existingTeacher.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("User with id " + id + " is not a teacher");
        }
        //when updating, assert potential new email value is not already taken by another user
        if (dto.getEmail() != null && !dto.getEmail().equals(existingTeacher.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new EmailAlreadyExistsException("Email already exists: " + dto.getEmail());
            }
        }
        // Verify null fields keep previous value
        updateUtils.updateTeacherFields(existingTeacher, dto);
        User savedTeacher = userRepository.save(existingTeacher);
        return userMapper.toTeacherDto(savedTeacher);
    }

    @Override
    public UserStatusResponseDTO disableTeacher(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Teacher not found with id: {}" + id));
        if(user.getRole() != Role.TEACHER) {
            throw new RoleNotAllowedException("User with role: {} cannot disable this user" + user.getRole());
        }
        user.setStatus(false);
        userRepository.save(user);
        return userMapper.toUserStatusResponseDTO(user);
    }

    @Override
    public UserStatusResponseDTO enableTeacher(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Teacher not found with id: " + id));

        if(user.getRole() != Role.TEACHER) {
            throw new RoleNotAllowedException("User with role: " + user.getRole() + " cannot enable this user");
        }
        user.setStatus(true);
        userRepository.save(user);
        return userMapper.toUserStatusResponseDTO(user);
    }

    @Override
    public void deleteTeacher(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Teacher not found with id: " + id));

        if(user.getRole() != Role.TEACHER) {
            throw new RoleNotAllowedException("User with role: " + user.getRole() + " cannot delete this user");
        }
        userRepository.delete(user);
    }


}

