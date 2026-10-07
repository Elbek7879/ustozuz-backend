package uz.ustozuz.backend.certificate;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.certificate.dto.CertificateResponse;
import uz.ustozuz.backend.course.Course;
import uz.ustozuz.backend.user.User;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;

    @Transactional(readOnly = true)
    public List<CertificateResponse> findMyCertificates(User student) {
        return certificateRepository.findByEnrollmentStudentId(student.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private CertificateResponse toResponse(Certificate certificate) {
        Course course = certificate.getEnrollment().getCourse();
        return new CertificateResponse(
                certificate.getId(),
                String.format("UZ-%06d", certificate.getId()),
                certificate.getEnrollment().getStudent().getName(),
                course.getTitle(),
                course.getSlug(),
                course.getInstructor().getName(),
                certificate.getIssuedAt()
        );
    }
}
