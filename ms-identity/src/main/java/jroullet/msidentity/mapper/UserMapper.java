package jroullet.msidentity.mapper;

import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDto(User user);
    User toEntity (UserDTO userDTO);
}
