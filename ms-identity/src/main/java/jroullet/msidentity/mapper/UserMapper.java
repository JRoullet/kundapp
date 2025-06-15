package jroullet.msidentity.mapper;

import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.zipCode", target = "zipCode")
    @Mapping(source = "address.country", target = "country")
    UserDTO toUserDto(User user);
}

//    @Mapping(target ="password", ignore = true)
//    User toEntity(UserDTO userDTO);
