package jroullet.mswebapp.clients;

import jroullet.mswebapp.dto.session.SessionCreationDTO;
import jroullet.mswebapp.dto.session.SessionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-course-mgmt", path = "/api")
public interface CourseManagementFeignClient {

    /**
     * Create a new session
     */
    @PostMapping("/sessions")
    SessionDTO createSession(@RequestParam("teacherId") Long teacherId,
                             @RequestBody SessionCreationDTO dto);

    /**
     * Upcoming sessions
     */
    @GetMapping("/sessions/teacher/{teacherId}/upcoming")
    List<SessionDTO> getUpcomingSessionsByTeacher(@PathVariable("teacherId") Long teacherId);

    /**
     * History of sessions
     */
    @GetMapping("/sessions/teacher/{teacherId}/past")
    List<SessionDTO> getPastSessionsByTeacher(@PathVariable("teacherId") Long teacherId);

    /**
     * All existing sessions (admin table)
     */
    @GetMapping("/sessions/teacher/{teacherId}/all")
    List<SessionDTO> getAllSessionsByTeacher(@PathVariable("teacherId") Long teacherId);
}
