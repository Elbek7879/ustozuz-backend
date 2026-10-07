package uz.ustozuz.backend.contact;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findTop20ByOrderByCreatedAtDesc();
}
