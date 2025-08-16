package jroullet.mscoursemgmt.mapper;

import jroullet.mscoursemgmt.dto.session.SessionCreationWithTeacherDTO;
import jroullet.mscoursemgmt.dto.session.SessionNoParticipantsDTO;
import jroullet.mscoursemgmt.dto.session.SessionUpdateDTO;
import jroullet.mscoursemgmt.dto.session.SessionWithParticipantsDTO;
import jroullet.mscoursemgmt.model.session.Session;
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
    @Mapping(target = "status", expression = "java(jroullet.mscoursemgmt.model.session.SessionStatus.SCHEDULED)")
    Session toEntity(SessionCreationWithTeacherDTO dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participantIds", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(jroullet.mscoursemgmt.model.session.SessionStatus.SCHEDULED)")
    Session toCreateEntity(SessionCreationWithTeacherDTO dto);

    @Mapping(target = "registeredParticipants", expression = "java(session.getParticipantIds().size())")
    @Mapping(target = "endDateTime", expression = "java(session.getStartDateTime().plusMinutes(session.getDurationMinutes()))")
    @Mapping(target = "statusDisplay", expression = "java(session.getStatus().getDisplayName())")
    SessionWithParticipantsDTO toDTO(Session session);

    // MappingTarget maps directly to a Session entity with values from SessionUpdateDTO
    // No return needed
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherId", ignore = true)
    @Mapping(target = "participantIds", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", ignore = true)
    void updateSessionFromDto(SessionUpdateDTO dto, @MappingTarget Session session);


    // Used to display session information to the client
    @Mapping(target = "registeredParticipants", expression = "java(session.getParticipantIds() != null ? session.getParticipantIds().size() : 0)")
    @Mapping(target = "isUserRegistered", ignore = true)
    @Mapping(target = "status", source = "session.status") // For history purpose
    SessionNoParticipantsDTO toSessionGetClientResponseDTO(Session session);

}
