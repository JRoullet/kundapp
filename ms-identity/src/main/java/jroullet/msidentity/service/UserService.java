package jroullet.msidentity.service;

import jroullet.msidentity.dto.*;

import java.util.List;

public interface UserService {

    UserDTO findUserDTOByEmail(String email);

    List<TeacherDTO> findAllTeachers();
    List<UserDTO> findAllUsers();

    UserDTO findUserById(Long id);
    UserDTO createUser(UserCreationDTO dto);
    UserDTO updateUser(Long id, UserUpdateDTO dto);
    UserStatusResponseDTO disableUser(Long id);
    UserStatusResponseDTO enableUser(Long id);
    void deleteUser(Long id);

}
