package jroullet.msidentity.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jroullet.msidentity.dto.UserDTO;
import jroullet.msidentity.model.Role;
import jroullet.msidentity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE email= ?", nativeQuery = true)
    Optional<User> findByEmail(@Email String email);

    @Query(value = "SELECT * FROM users WHERE id IN (:userIds)", nativeQuery = true)
    List<User> findParticipantsByIds(@Param("userIds") List<Long> userIds);

}
