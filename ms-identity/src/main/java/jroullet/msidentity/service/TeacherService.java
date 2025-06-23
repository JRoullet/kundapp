package jroullet.msidentity.service;

import jakarta.validation.Valid;
import jroullet.msidentity.dto.TeacherDTO;
import jroullet.msidentity.dto.TeacherRegistrationDTO;
import jroullet.msidentity.dto.TeacherUpdateDTO;
import jroullet.msidentity.dto.UserDTO;
import org.springframework.stereotype.Service;
@Service
public interface TeacherService {

   TeacherDTO registerTeacher(TeacherRegistrationDTO dto);
   TeacherDTO findTeacherById(Long id);
   TeacherDTO updateTeacher(Long id, TeacherUpdateDTO dto);
}
