package jroullet.mswebapp.clients;

import jakarta.validation.Valid;
import jroullet.mswebapp.dto.session.SessionCancelDTO;
import jroullet.mswebapp.dto.session.SessionCreationDTO;
import jroullet.mswebapp.dto.session.SessionDTO;
import jroullet.mswebapp.dto.session.SessionUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-course-mgmt", path = "/api/sessions")
public interface CourseManagementFeignClient {

    @GetMapping("/{sessionId}")
    SessionDTO getSessionById (@PathVariable("sessionId") Long sessionId);

    /**
     * Teacher session methods
     **/
    @PostMapping
    SessionDTO createSession(@RequestParam("teacherId") Long teacherId,
                             @RequestBody SessionCreationDTO dto);

    // Upcoming sessions
    @GetMapping("/teacher/{teacherId}/upcoming")
    List<SessionDTO> getUpcomingSessionsByTeacher(@PathVariable("teacherId") Long teacherId);

    // History of sessions
    @GetMapping("/teacher/{teacherId}/past")
    List<SessionDTO> getPastSessionsByTeacher(@PathVariable("teacherId") Long teacherId);

    @PutMapping("/{sessionId}/teacher/{teacherId}")
    SessionDTO updateSessionByTeacher(@PathVariable Long sessionId,
                                    @PathVariable Long teacherId,
                                    @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO);

    @PostMapping("/teacher/cancel")
    Void cancelSession(@RequestBody SessionCancelDTO dto);


    /**
     * Admin session methods
     **/
    @PutMapping("/admin/{sessionId}/update")
    SessionDTO updateSessionByAdmin(@PathVariable Long sessionId,
                                    @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO);

    @PostMapping("/admin/cancel")
    Void cancelSessionByAdmin(@RequestParam Long sessionId);

    @GetMapping("/all")
    List<SessionDTO> getAllSessionsForAdmin();
}
