package jroullet.msidentity.mapper;

import jroullet.msidentity.dto.teacher.TeacherDTO;
import jroullet.msidentity.dto.user.UserDTO;
import jroullet.msidentity.dto.user.UserStatusResponseDTO;
import jroullet.msidentity.dto.user.UserParticipantDTO;
import jroullet.msidentity.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    TeacherDTO toTeacherDto(User user);

    UserDTO toUserDto(User user);

    UserStatusResponseDTO toUserStatusResponseDTO(User user);

    UserParticipantDTO toUserParticipantDto(User user);
}

//    @Mapping(target ="password", ignore = true)
//    User toEntity(UserDTO userDTO);
