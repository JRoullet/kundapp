package jroullet.mscoursemgmt.service;

import jakarta.validation.Valid;
import jroullet.mscoursemgmt.dto.SessionCancelDTO;
import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.dto.SessionDTO;
import jroullet.mscoursemgmt.dto.SessionUpdateDTO;

import java.util.List;

public interface SessionService {

    //Common methods
    SessionDTO getSessionById(Long sessionId);

     //Teacher methods
     SessionDTO createSession(Long teacherId, SessionCreationDTO dto);
    List<SessionDTO> getUpcomingSessionsByTeacher(Long teacherId);
    List<SessionDTO> getHistorySessionsByTeacher(Long teacherId);
    void cancelSession(SessionCancelDTO dto);
    SessionDTO updateSessionByTeacher(Long sessionId, Long teacherId, SessionUpdateDTO sessionUpdateDTO);

    //Admin methods
    SessionDTO updateSessionByAdmin(Long id, SessionUpdateDTO dto);
    void cancelSessionByAdmin(Long sessionId);
    List<SessionDTO> getAllSessionsForAdmin();



}
