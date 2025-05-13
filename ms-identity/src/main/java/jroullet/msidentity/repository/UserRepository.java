package jroullet.msidentity.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jroullet.msidentity.model.Role;
import jroullet.msidentity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(@Email String email);

    @Query(value = "SELECT * FROM Users WHERE email= ?", nativeQuery = true)
    Optional<User> findByEmail(@Email String email);

    List<User> findByRole(Role role);

    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);

}
