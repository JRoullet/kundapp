package jroullet.msidentity.repository;

import jakarta.validation.constraints.Email;
import jroullet.msidentity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(@Email String email);
}
