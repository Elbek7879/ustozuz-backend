package uz.ustozuz.backend.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.config.AppProperties;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final AppProperties appProperties;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = appProperties.getFrontendUrl() + "/parolni-tiklash/" + resetToken;
        log.info("=== PAROLNI TIKLASH XATI ===");
        log.info("Kimga: {}", toEmail);
        log.info("Havola: {}", resetLink);
        log.info("=============================");
    }
}
