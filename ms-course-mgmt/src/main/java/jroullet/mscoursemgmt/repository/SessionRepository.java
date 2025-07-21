package jroullet.mscoursemgmt.repository;

import jroullet.mscoursemgmt.model.Session;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
//    @SQL()

    List<Session> findByTeacherIdAndStartDateTimeBetween(Long teacherId, LocalDateTime sessionStart, LocalDateTime sessionEnd);

    // Upcoming sessions
    @Query("SELECT s FROM Session s WHERE s.teacherId = :teacherId AND s.startDateTime > CURRENT_TIMESTAMP")
    List<Session> findByTeacherIdAndStartDateTimeAfterOrderByStartDateTimeAsc(
            Long teacherId, LocalDateTime now);

    // Past sessions
    List<Session> findByTeacherIdAndStartDateTimeBeforeOrderByStartDateTimeDesc(
            Long teacherId, LocalDateTime now);

    // Teacher sessions whole catalogue
    List<Session> findByTeacherIdOrderByStartDateTimeDesc(Long teacherId);
}
