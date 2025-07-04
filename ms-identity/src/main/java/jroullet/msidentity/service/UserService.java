package jroullet.msidentity.service;

import jroullet.msidentity.dto.TeacherDTO;
import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.model.Role;

import java.util.List;

public interface UserService {
    UserDTO findUserDTOByEmail(String email);
    List<TeacherDTO> findAllTeachers();
    List<UserDTO> findAllUsers();

}
