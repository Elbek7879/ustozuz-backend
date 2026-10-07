package uz.ustozuz.backend.auth;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.auth.dto.AuthResponse;
import uz.ustozuz.backend.auth.dto.LoginRequest;
import uz.ustozuz.backend.auth.dto.RegisterRequest;
import uz.ustozuz.backend.common.exception.BadRequestException;
import uz.ustozuz.backend.common.exception.ConflictException;
import uz.ustozuz.backend.mail.MailService;
import uz.ustozuz.backend.user.Role;
import uz.ustozuz.backend.user.User;
import uz.ustozuz.backend.user.UserRepository;
import uz.ustozuz.backend.user.UserStatus;
import uz.ustozuz.backend.user.dto.UserResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    private static final Duration RESET_TOKEN_TTL = Duration.ofHours(1);
    private static final Duration RESEND_INTERVAL = Duration.ofMinutes(1);

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Bu email allaqachon ro'yxatdan o'tgan");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.STUDENT);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(() -> new BadRequestException("Email yoki parol noto'g'ri"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Email yoki parol noto'g'ri");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new BadRequestException("Hisobingiz bloklangan");
        }

        return buildAuthResponse(user);
    }

    // Email ro'yxatda bo'lmasa ham bir xil javob qaytadi — begona odam qaysi email borligini bilmasin
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .filter(user -> user.getStatus() != UserStatus.BLOCKED)
                .ifPresent(user -> {
                    Instant now = Instant.now();

                    // Bir daqiqada bir martadan ko'p xat yubormaymiz (pochtani to'ldirib tashlamaslik uchun)
                    if (resetTokenRepository.existsByUserIdAndExpiresAtAfter(
                            user.getId(), now.plus(RESET_TOKEN_TTL).minus(RESEND_INTERVAL))) {
                        return;
                    }

                    String rawToken = generateRandomToken();

                    PasswordResetToken resetToken = new PasswordResetToken();
                    resetToken.setUser(user);
                    resetToken.setToken(rawToken);
                    resetToken.setExpiresAt(now.plus(RESET_TOKEN_TTL));
                    resetTokenRepository.save(resetToken);

                    // Xat yuborilmasa tranzaksiya bekor bo'ladi va token saqlanmaydi
                    mailService.sendPasswordResetEmail(user.getEmail(), user.getName(), rawToken);
                });
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Havola yaroqsiz yoki muddati o'tgan"));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Havola yaroqsiz yoki muddati o'tgan");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserResponse.from(user));
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}