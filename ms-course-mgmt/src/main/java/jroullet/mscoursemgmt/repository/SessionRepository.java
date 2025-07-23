package jroullet.mscoursemgmt.repository;

import jroullet.mscoursemgmt.model.Session;
import jroullet.mscoursemgmt.model.SessionStatus;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Sessions finders
     */
    List<Session> findSessionByTeacherId(Long teacherId);

    // Time conflicting sessions for teacher
//    @SQL()
    List<Session> findByTeacherIdAndStartDateTimeBetween(Long teacherId, LocalDateTime sessionStart, LocalDateTime sessionEnd);

//    // Upcoming sessions
//    @Query("SELECT s FROM Session s WHERE s.teacherId = :teacherId AND s.startDateTime > CURRENT_TIMESTAMP")
//    List<Session> findByTeacherIdAndStartDateTimeAfterOrderByStartDateTimeAsc(
//            Long teacherId, LocalDateTime now);

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
}
