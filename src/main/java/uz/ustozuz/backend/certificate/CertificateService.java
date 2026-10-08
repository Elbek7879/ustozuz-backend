package uz.ustozuz.backend.certificate;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.certificate.dto.CertificateResponse;
import uz.ustozuz.backend.certificate.dto.PublicCertificateResponse;
import uz.ustozuz.backend.common.exception.NotFoundException;
import uz.ustozuz.backend.config.JwtProperties;
import uz.ustozuz.backend.course.Course;
import uz.ustozuz.backend.user.User;

@Service
@RequiredArgsConstructor
public class CertificateService {

    // Tekshirish kodi: UZ-000001-<12 ta hex>. Raqam ketma-ket, shuning uchun oxiridagi imzo
    // kodni taxmin qilib bo'lmaydigan qiladi (begona odam raqamlarni terib ismlarni ko'ra olmaydi).
    private static final Pattern VERIFY_CODE = Pattern.compile("^UZ-(\\d{6,12})-([0-9a-fA-F]{12})$");

    private final CertificateRepository certificateRepository;
    private final JwtProperties jwtProperties;

    @Transactional(readOnly = true)
    public List<CertificateResponse> findMyCertificates(User student) {
        return certificateRepository.findByEnrollmentStudentId(student.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    // Ochiq tekshiruv: QR kod yoki havola orqali hamma ko'ra oladi
    @Transactional(readOnly = true)
    public PublicCertificateResponse verify(String code) {
        Matcher m = VERIFY_CODE.matcher(code == null ? "" : code.trim());
        if (!m.matches()) {
            throw new NotFoundException("Sertifikat topilmadi");
        }
        long id = Long.parseLong(m.group(1));
        byte[] expected = signature(id).getBytes(StandardCharsets.UTF_8);
        byte[] given = m.group(2).toLowerCase().getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, given)) {
            throw new NotFoundException("Sertifikat topilmadi");
        }

        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sertifikat topilmadi"));
        Course course = certificate.getEnrollment().getCourse();
        return new PublicCertificateResponse(
                number(certificate.getId()),
                certificate.getEnrollment().getStudent().getName(),
                course.getTitle(),
                course.getSlug(),
                course.getInstructor().getName(),
                certificate.getIssuedAt()
        );
    }

    private CertificateResponse toResponse(Certificate certificate) {
        Course course = certificate.getEnrollment().getCourse();
        return new CertificateResponse(
                certificate.getId(),
                number(certificate.getId()),
                certificate.getEnrollment().getStudent().getName(),
                course.getTitle(),
                course.getSlug(),
                course.getInstructor().getName(),
                certificate.getIssuedAt(),
                number(certificate.getId()) + "-" + signature(certificate.getId())
        );
    }

    private static String number(long id) {
        return String.format("UZ-%06d", id);
    }

    // HMAC-SHA256 ning dastlabki 6 bayti; kalit sifatida maxfiy JWT kaliti boshqa maqsad prefiksi bilan ishlatiladi
    private String signature(long id) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(("certificate:" + id).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash, 0, 6);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Sertifikat kodini hisoblab bo'lmadi", e);
        }
    }
}
