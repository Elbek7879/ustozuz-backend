package uz.ustozuz.backend.certificate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByEnrollmentStudentId(Long studentId);

    Optional<Certificate> findByEnrollmentId(Long enrollmentId);
}