package jroullet.msidentity.mapper;

import jroullet.msidentity.dto.TeacherDTO;
import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    TeacherDTO toTeacherDto(User user);

    UserDTO toUserDto(User user);
}

//    @Mapping(target ="password", ignore = true)
//    User toEntity(UserDTO userDTO);
