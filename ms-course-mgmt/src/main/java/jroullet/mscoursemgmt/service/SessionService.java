package jroullet.mscoursemgmt.service;

import jroullet.mscoursemgmt.dto.SessionCancelDTO;
import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.dto.SessionDTO;
import jroullet.mscoursemgmt.dto.SessionUpdateDTO;

import java.util.List;

public interface SessionService {
    SessionDTO createSession(Long teacherId, SessionCreationDTO dto);
    void cancelSession(SessionCancelDTO dto);

    SessionDTO getSessionById(Long sessionId);

     //Get sessions
    List<SessionDTO> getUpcomingSessionsByTeacher(Long teacherId);
    List<SessionDTO> getHistorySessionsByTeacher(Long teacherId);


    SessionDTO updateSession(Long id, SessionUpdateDTO dto);
    void deleteSession(Long id);

    List<SessionDTO> getAllSessions();


}
