package jroullet.mscoursemgmt.mapper;

import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.dto.SessionDTO;
import jroullet.mscoursemgmt.dto.SessionUpdateDTO;
import jroullet.mscoursemgmt.model.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherId", ignore = true)
    @Mapping(target = "participantIds", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(jroullet.mscoursemgmt.model.SessionStatus.SCHEDULED)")
    Session toEntity(SessionCreationDTO dto);

    @Mapping(target = "registeredParticipants", expression = "java(session.getParticipantIds().size())")
    @Mapping(target = "endDateTime", expression = "java(session.getStartDateTime().plusMinutes(session.getDurationMinutes()))")
    @Mapping(target = "statusDisplay", expression = "java(session.getStatus().getDisplayName())")
    SessionDTO toDTO(Session session);

    // MappingTarget maps directly to a Session entity with values from SessionUpdateDTO
    // No return needed
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherId", ignore = true)
    @Mapping(target = "participantIds", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", ignore = true)
    void updateSessionFromDto(SessionUpdateDTO dto, @MappingTarget Session session);
}
