package uz.ustozuz.backend.auth;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

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

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Bu email allaqachon ro'yxatdan o'tgan");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.STUDENT);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Email yoki parol noto'g'ri"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Email yoki parol noto'g'ri");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new BadRequestException("Hisobingiz bloklangan");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String rawToken = generateRandomToken();

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setToken(rawToken);
            resetToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
            resetTokenRepository.save(resetToken);

            mailService.sendPasswordResetEmail(user.getEmail(), rawToken);
        });
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
        UserResponse userResponse = new UserResponse(
                user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole().name()
        );
        return new AuthResponse(token, userResponse);
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}