package jroullet.mswebapp.clients;

import jakarta.validation.Valid;
import jroullet.mswebapp.dto.session.*;
import jroullet.mswebapp.dto.session.create.SessionCreationResponseDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationWithTeacherDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-course-mgmt", path = "/api/sessions")
public interface CourseManagementFeignClient {

    @GetMapping("/{sessionId}")
    SessionWithParticipantsDTO getSessionById (@PathVariable("sessionId") Long sessionId);

    /**
     * Teacher session methods
     **/
    @PostMapping
    SessionCreationResponseDTO createSession(@Valid @RequestBody SessionCreationWithTeacherDTO dto);

    // Upcoming sessions
    @GetMapping("/teacher/{teacherId}/upcoming")
    List<SessionWithParticipantsDTO> getUpcomingSessionsByTeacher(@PathVariable("teacherId") Long teacherId);

    // History of sessions
    @GetMapping("/teacher/{teacherId}/past")
    List<SessionWithParticipantsDTO> getPastSessionsByTeacher(@PathVariable("teacherId") Long teacherId);

    @PutMapping("/{sessionId}/teacher/{teacherId}")
    SessionWithParticipantsDTO updateSessionByTeacher(@PathVariable Long sessionId,
                                                      @PathVariable Long teacherId,
                                                      @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO);

    @PostMapping("/teacher/cancel")
    Void cancelSessionByTeacher(@RequestBody SessionCancelDTO dto);

    /**
     * Admin session methods
     **/
    @PutMapping("/admin/{sessionId}/update")
    SessionWithParticipantsDTO updateSessionByAdmin(@PathVariable Long sessionId,
                                                    @RequestBody @Valid SessionUpdateDTO sessionUpdateDTO);

    @PostMapping("/admin/cancel")
    Void cancelSessionByAdmin(@RequestParam Long sessionId);

    @GetMapping("/all")
    List<SessionWithParticipantsDTO> getAllSessionsForAdmin();

    /**
     * Client session methods
     **/
    @GetMapping("/client/available")
    List<SessionNoParticipantsDTO> getAvailableSessionsForClient();

    @GetMapping("/client/upcoming/{participantId}")
    List<SessionNoParticipantsDTO> getUpcomingSessionsForClient(@PathVariable("participantId") Long participantId);

    @GetMapping("/client/past/{participantId}")
    List<SessionNoParticipantsDTO> getPastSessionsForClient(@PathVariable("participantId") Long participantId);
}
