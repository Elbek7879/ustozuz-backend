package uz.ustozuz.backend.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    List<User> findAllByOrderByCreatedAtDesc();

    List<User> findTop5ByOrderByCreatedAtDesc();
}
