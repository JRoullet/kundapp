package jroullet.mscoursemgmt.repository;

import jroullet.mscoursemgmt.model.session.Session;
import jroullet.mscoursemgmt.model.session.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Sessions finders
     */
    List<Session> findSessionByTeacherId(Long teacherId);

    // Time conflicting sessions for teacher
    List<Session> findByTeacherIdAndStartDateTimeBetween(Long teacherId, LocalDateTime sessionStart, LocalDateTime sessionEnd);

    /** Upcoming sessions */
    // Next sessions are first
    @Query("SELECT s FROM Session s WHERE s.teacherId = :teacherId AND s.status = :status ORDER BY s.startDateTime ASC")
    List<Session> findByTeacherIdAndStatusOrderByStartDateTimeAsc(@Param("teacherId") Long teacherId,
                                                                  @Param("status") SessionStatus status);

    /** History sessions */
    // Most recent sessions are first
    @Query("SELECT s FROM Session s WHERE s.teacherId = :teacherId AND s.status != :status ORDER BY s.startDateTime DESC")
    List<Session> findByTeacherIdAndStatusNotOrderByStartDateTimeDesc(@Param("teacherId") Long teacherId,
                                                                      @Param("status") SessionStatus status);

    /** FindAll sessions by teacher*/
    //General usage
    @Query("SELECT s FROM Session s WHERE s.teacherId = :teacherId ORDER BY s.startDateTime DESC")
    List<Session> findByTeacherIdOrderByStartDateTimeDesc(@Param("teacherId") Long teacherId);

    /** Next Available sessions for clients */
    @Query("SELECT s FROM Session s WHERE s.status = :status AND s.startDateTime > :now ORDER BY s.startDateTime ASC")
    List<Session> findByStatusAndStartDateTimeAfterOrderByStartDateTimeAsc(@Param("status") SessionStatus status,
                                                                           @Param("now") LocalDateTime now);

    @Query("SELECT s FROM Session s ORDER BY s.startDateTime DESC")
    List<Session> findAllOrderByStartDateTimeDesc();

    @Query("SELECT s FROM Session s WHERE s.status = :sessionStatus ORDER BY s.startDateTime ASC")
    List<Session> findByStatusOrderByStartDateTimeAsc(@Param("sessionStatus") SessionStatus sessionStatus);

    //TODO : CHECK IF THIS IS CORRECT
    @Query(value = "SELECT * FROM session s INNER JOIN session_participants sp ON s.id = sp.session_id WHERE sp.participant_id = :participantId AND s.status = :sessionStatus ORDER BY s.start_date_time ASC", nativeQuery = true)
    List<Session> findByParticipantIdOrderByStartDateTimeAsc(@Param("participantId") Long participantId, @Param("sessionStatus")SessionStatus sessionStatus);

    @Query("SELECT s FROM Session s WHERE :participantId MEMBER OF s.participantIds AND s.status = :sessionStatus ORDER BY s.startDateTime DESC")
    List<Session> findByParticipantIdOrderByStartDateTimeDesc(@Param("participantId") Long participantId, @Param("sessionStatus")SessionStatus sessionStatus);
}
