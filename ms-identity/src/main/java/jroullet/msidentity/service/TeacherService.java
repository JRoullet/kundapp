package jroullet.msidentity.service;

import jroullet.msidentity.dto.TeacherRegistrationDTO;
import jroullet.msidentity.dto.UserDTO;
import org.springframework.stereotype.Service;
@Service
public interface TeacherService {

   UserDTO registerTeacher(TeacherRegistrationDTO dto);
}
