package jroullet.msidentity.service.impl;

import jroullet.msidentity.dto.TeacherRegistrationDTO;
import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.exception.EmailAlreadyExistsException;
import jroullet.msidentity.mapper.UserMapper;
import jroullet.msidentity.model.Role;
import jroullet.msidentity.model.SubscriptionStatus;
import jroullet.msidentity.model.User;
import jroullet.msidentity.repository.UserRepository;
import jroullet.msidentity.service.TeacherService;
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
    private final UserMapper userMapper; // Pour la conversion DTO <-> Entity

    @Override
    public UserDTO registerTeacher(TeacherRegistrationDTO dto) {
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
        teacher.setSubscriptionStatus(SubscriptionStatus.NONE);
        teacher.setBiography(dto.getBiography());


        User savedTeacher = userRepository.save(teacher);
        return userMapper.toUserDto(savedTeacher);
    }
}
