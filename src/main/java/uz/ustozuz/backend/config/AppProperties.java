package uz.ustozuz.backend.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    // Parolni tiklash havolasi shu manzil asosida yasaladi
    private String frontendUrl = "http://localhost:3000";

    // Brauzerdan so'rov yuborishga ruxsat berilgan manzillar, masalan https://*.vercel.app
    private List<String> corsOrigins = new ArrayList<>(List.of("http://localhost:3000"));

    private Seed seed = new Seed();

    private Mail mail = new Mail();

    @Getter
    @Setter
    public static class Seed {
        private String adminPassword;
        private String instructorPassword;
    }

    @Getter
    @Setter
    public static class Mail {
        // "log" — xat konsolga yoziladi (lokal); "brevo" — Brevo HTTPS API orqali haqiqiy xat
        private String provider = "log";
        private String brevoApiKey;
        private String brevoUrl = "https://api.brevo.com/v3/smtp/email";
        // Brevo'da tasdiqlangan jo'natuvchi manzil
        private String fromEmail;
        private String fromName = "UstozUz";
    }
}
