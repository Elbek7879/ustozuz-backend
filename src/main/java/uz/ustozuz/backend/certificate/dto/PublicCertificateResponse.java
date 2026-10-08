package uz.ustozuz.backend.certificate.dto;

import java.time.Instant;

// Ochiq tekshiruv sahifasi uchun: faqat sertifikatda yozilgan ma'lumotlar (email va boshqalar yo'q)
public record PublicCertificateResponse(
        String number,
        String studentName,
        String courseTitle,
        String courseSlug,
        String instructorName,
        Instant issuedAt
) {
}
