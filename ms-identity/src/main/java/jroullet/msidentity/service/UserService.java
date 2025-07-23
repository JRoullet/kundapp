package jroullet.msidentity.service;

import jroullet.msidentity.dto.*;
import jroullet.msidentity.dto.user.UserParticipantDTO;

import java.util.List;

public interface UserService {

    UserDTO findUserDTOByEmail(String email);

    List<TeacherDTO> findAllTeachers();
    List<UserDTO> findAllUsers();
    List<UserParticipantDTO> findAllParticipants(List<Long> userIds);

    UserDTO findUserById(Long id);
    UserDTO createUser(UserCreationDTO dto);
    UserDTO updateUser(Long id, UserUpdateDTO dto);
    UserStatusResponseDTO disableUser(Long id);
    UserStatusResponseDTO enableUser(Long id);
    void deleteUser(Long id);
    void addUserCredits(Long id, Integer credits);



}
