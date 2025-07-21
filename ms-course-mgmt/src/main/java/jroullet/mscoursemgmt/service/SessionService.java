package jroullet.mscoursemgmt.service;

import jroullet.mscoursemgmt.dto.SessionCreationDTO;
import jroullet.mscoursemgmt.dto.SessionDTO;
import jroullet.mscoursemgmt.dto.SessionUpdateDTO;

import java.util.List;

public interface SessionService {
    SessionDTO createSession(Long teacherId, SessionCreationDTO dto);

     //Get sessions
    List<SessionDTO> getUpcomingSessionsByTeacher(Long teacherId);
    List<SessionDTO> getPastSessionsByTeacher(Long teacherId);
    List<SessionDTO> getAllSessionsByTeacher(Long teacherId);

    SessionDTO updateSession(Long id, SessionUpdateDTO dto);
    void deleteSession(Long id);
}
