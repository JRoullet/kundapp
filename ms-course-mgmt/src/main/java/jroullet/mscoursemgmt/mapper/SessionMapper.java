package jroullet.mscoursemgmt.mapper;

import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.dto.SessionDTO;
import jroullet.mscoursemgmt.model.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherId", ignore = true)
    @Mapping(target = "participantIds", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Session toEntity(SessionCreationDTO dto);

    @Mapping(target = "registeredParticipants", expression = "java(session.getParticipantIds().size())")
    @Mapping(target = "endDateTime", expression = "java(session.getStartDateTime().plusMinutes(session.getDurationMinutes()))")
    SessionDTO toDTO(Session session);
}
