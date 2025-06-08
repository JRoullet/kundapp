//package jroullet.msidentity.mapper;
//
//import jroullet.msidentity.dto.ClientPatchDTO;
//import jroullet.msidentity.dto.ClientCreateDTO;
//import jroullet.msidentity.dto.ClientResponseDTO;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingTarget;
//
//@Mapper(componentModel = "spring")
//public interface ClientMapper {
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "role", ignore = true)
//    @Mapping(target = "status", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "subscriptionStatus", constant = "NONE")
//    ClientProfile toEntity(ClientCreateDTO dto);
//
//    @Mapping(target = "id", source = "id")
//    @Mapping(target = "email", source = "email")
//    @Mapping(target = "firstName", source = "firstName")
//    @Mapping(target = "lastName", source = "lastName")
//    ClientResponseDTO toResponseDTO(ClientProfile client);
//
//
//    @Mapping(target = "firstName", source = "firstName")
//    @Mapping(target = "lastName", source = "lastName")
//    @Mapping(target = "phoneNumber", source = "phoneNumber")
//    @Mapping(target = "profilePicture", source = "profilePicture")
//    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
//    @Mapping(target = "street", source = "street")
//    @Mapping(target = "city", source = "city")
//    @Mapping(target = "zipCode", source = "zipCode")
//    @Mapping(target = "country", source = "country")
//    ClientPatchDTO toPatchDTO(ClientProfile client);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "email", ignore = true)
//    @Mapping(target = "password", ignore = true)
//    @Mapping(target = "role", ignore = true)
//    @Mapping(target = "status", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "subscriptionStatus", ignore = true)
//    void updateEntityFromDTO(ClientPatchDTO dto, @MappingTarget ClientProfile client);
//}