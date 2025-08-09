package jroullet.msidentity.service;

import jroullet.msidentity.dto.teacher.TeacherDTO;
import jroullet.msidentity.dto.teacher.TeacherRegistrationDTO;
import jroullet.msidentity.dto.teacher.TeacherUpdateDTO;
import jroullet.msidentity.dto.user.UserStatusResponseDTO;
import org.springframework.stereotype.Service;

public interface TeacherService {

   TeacherDTO registerTeacher(TeacherRegistrationDTO dto);
   TeacherDTO findTeacherById(Long id);
   TeacherDTO updateTeacher(Long id, TeacherUpdateDTO dto);
   UserStatusResponseDTO disableTeacher(Long id);
   UserStatusResponseDTO enableTeacher(Long id);
   void deleteTeacher(Long id);
}
