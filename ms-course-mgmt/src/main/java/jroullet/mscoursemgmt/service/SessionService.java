package jroullet.mscoursemgmt.service;

import jroullet.mscoursemgmt.dto.session.*;
import jroullet.mscoursemgmt.model.session.Session;

import java.util.List;

public interface SessionService {

    //Common methods
    SessionWithParticipantsDTO getSessionById(Long sessionId);
    Session validateSession(Long sessionId);

     //Teacher methods
    SessionCreationResponseDTO createSession(SessionCreationWithTeacherDTO dto);
    List<SessionWithParticipantsDTO> getUpcomingSessionsByTeacher(Long teacherId);
    List<SessionWithParticipantsDTO> getHistorySessionsByTeacher(Long teacherId);
    void cancelSessionByTeacher(SessionCancelDTO dto);
    SessionWithParticipantsDTO updateSessionByTeacher(Long sessionId, Long teacherId, SessionUpdateDTO sessionUpdateDTO);

    //Admin methods
    SessionWithParticipantsDTO updateSessionByAdmin(Long id, SessionUpdateDTO dto);
    void cancelSessionByAdmin(Long sessionId);
    List<SessionWithParticipantsDTO> getAllSessionsForAdmin();

    //Client methods
    List<SessionNoParticipantsDTO> getAvailableSessionsForClient();
    List<SessionNoParticipantsDTO> getUpcomingSessionsForClient(Long participantId);
    List<SessionNoParticipantsDTO> getPastSessionsForClient(Long participantId);
}
