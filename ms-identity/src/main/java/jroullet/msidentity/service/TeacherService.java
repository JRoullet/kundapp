package jroullet.msidentity.service;

import jakarta.validation.Valid;
import jroullet.msidentity.dto.*;
import org.springframework.stereotype.Service;
@Service
public interface TeacherService {

   TeacherDTO registerTeacher(TeacherRegistrationDTO dto);
   TeacherDTO findTeacherById(Long id);
   TeacherDTO updateTeacher(Long id, TeacherUpdateDTO dto);
   UserStatusResponseDTO disableTeacher(Long id);
   UserStatusResponseDTO enableTeacher(Long id);
   void deleteTeacher(Long id);
}
