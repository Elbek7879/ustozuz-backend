package uz.ustozuz.backend.auth;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    // Muddati shu vaqtdan keyin tugaydigan token bormi (ya'ni yaqinda yaratilganmi)
    boolean existsByUserIdAndExpiresAtAfter(Long userId, Instant threshold);
}
