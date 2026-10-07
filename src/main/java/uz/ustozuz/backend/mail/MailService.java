package uz.ustozuz.backend.mail;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.HtmlUtils;

import jakarta.annotation.PostConstruct;

import uz.ustozuz.backend.common.exception.ServiceUnavailableException;
import uz.ustozuz.backend.config.AppProperties;

// Xatlar Brevo HTTPS API orqali yuboriladi: Railway'ning arzon tariflarida SMTP portlari yopiq.
// Sozlanmagan bo'lsa (provider=log) xat matni konsolga yoziladi.
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final AppProperties appProperties;
    private final RestClient restClient;

    public MailService(AppProperties appProperties) {
        this.appProperties = appProperties;

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @PostConstruct
    void checkConfig() {
        AppProperties.Mail mail = appProperties.getMail();
        if (isBrevo() && (isBlank(mail.getBrevoApiKey()) || isBlank(mail.getFromEmail()))) {
            log.warn("app.mail.provider=brevo, lekin BREVO_API_KEY yoki MAIL_FROM_EMAIL berilmagan — xat yuborilmaydi");
        } else {
            log.info("Email yuborish usuli: {}", isBrevo() ? "Brevo (" + mail.getFromEmail() + ")" : "konsolga yozish");
        }
    }

    public void sendPasswordResetEmail(String toEmail, String toName, String resetToken) {
        String resetLink = appProperties.getFrontendUrl() + "/parolni-tiklash/" + resetToken;

        if (!isBrevo()) {
            log.info("=== PAROLNI TIKLASH XATI ===");
            log.info("Kimga: {}", toEmail);
            log.info("Havola: {}", resetLink);
            log.info("=============================");
            return;
        }

        send(toEmail, toName, "UstozUz: parolni tiklash",
                passwordResetHtml(toName, resetLink), passwordResetText(toName, resetLink));
    }

    private void send(String toEmail, String toName, String subject, String html, String text) {
        AppProperties.Mail mail = appProperties.getMail();
        if (isBlank(mail.getBrevoApiKey()) || isBlank(mail.getFromEmail())) {
            throw new ServiceUnavailableException("Xat yuborish xizmati sozlanmagan. Administratorga murojaat qiling");
        }

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", mail.getFromName(), "email", mail.getFromEmail()),
                "to", List.of(Map.of("email", toEmail, "name", toName)),
                "subject", subject,
                "htmlContent", html,
                "textContent", text
        );

        try {
            restClient.post()
                    .uri(mail.getBrevoUrl())
                    .header("api-key", mail.getBrevoApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Parolni tiklash xati yuborildi: {}", toEmail);
        } catch (RestClientException e) {
            log.error("Xat yuborilmadi ({}): {}", toEmail, e.getMessage());
            throw new ServiceUnavailableException("Xatni yuborib bo'lmadi. Birozdan keyin qayta urinib ko'ring");
        }
    }

    private String passwordResetHtml(String name, String link) {
        String safeName = HtmlUtils.htmlEscape(name);
        return """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:0 auto;padding:24px;color:#111827">
                  <p style="font-size:24px;font-weight:bold;color:#4338ca;margin:0 0 24px">Ustoz<span style="color:#111827">Uz</span></p>
                  <p style="font-size:16px">Assalomu alaykum, %s!</p>
                  <p style="font-size:15px;line-height:1.6">Hisobingiz uchun parolni tiklash so'rovi keldi.
                     Yangi parol o'rnatish uchun quyidagi tugmani bosing:</p>
                  <p style="margin:28px 0">
                    <a href="%s" style="background:#4338ca;color:#ffffff;text-decoration:none;padding:12px 24px;border-radius:6px;font-weight:bold;display:inline-block">Yangi parol o'rnatish</a>
                  </p>
                  <p style="font-size:13px;color:#6b7280;line-height:1.6">Havola 1 soat davomida amal qiladi va faqat bir marta ishlatiladi.<br>
                     Agar bu so'rovni siz yubormagan bo'lsangiz, xatga e'tibor bermang — parolingiz o'zgarmaydi.</p>
                  <p style="font-size:12px;color:#9ca3af;margin-top:24px;word-break:break-all">Tugma ishlamasa, havolani brauzerga nusxalang:<br>%s</p>
                </div>
                """.formatted(safeName, link, link);
    }

    private String passwordResetText(String name, String link) {
        return """
                Assalomu alaykum, %s!

                Hisobingiz uchun parolni tiklash so'rovi keldi. Yangi parol o'rnatish uchun havolani oching:
                %s

                Havola 1 soat amal qiladi. Agar so'rovni siz yubormagan bo'lsangiz, xatga e'tibor bermang.

                UstozUz
                """.formatted(name, link);
    }

    private boolean isBrevo() {
        return "brevo".equalsIgnoreCase(appProperties.getMail().getProvider());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
