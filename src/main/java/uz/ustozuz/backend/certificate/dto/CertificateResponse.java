package uz.ustozuz.backend.certificate.dto;

import java.time.Instant;

public record CertificateResponse(
        Long id,
        String number,
        String studentName,
        String courseTitle,
        String courseSlug,
        String instructorName,
        Instant issuedAt
) {
}
