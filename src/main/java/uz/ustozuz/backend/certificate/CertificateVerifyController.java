package uz.ustozuz.backend.certificate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.certificate.dto.PublicCertificateResponse;

// Sertifikat haqiqiyligini tekshirish (kirish talab qilinmaydi)
@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateVerifyController {

    private final CertificateService certificateService;

    @GetMapping("/verify/{code}")
    public PublicCertificateResponse verify(@PathVariable String code) {
        return certificateService.verify(code);
    }
}
