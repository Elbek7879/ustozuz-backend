package uz.ustozuz.backend.certificate;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.certificate.dto.CertificateResponse;
import uz.ustozuz.backend.user.CurrentUserService;

@RestController
@RequestMapping("/api/me/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<CertificateResponse> myCertificates(@AuthenticationPrincipal Jwt jwt) {
        return certificateService.findMyCertificates(currentUserService.require(jwt));
    }
}
