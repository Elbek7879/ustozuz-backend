package uz.ustozuz.backend.user;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.common.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User require(Jwt jwt) {
        if (jwt == null) {
            throw new InsufficientAuthenticationException("Tizimga kirilmagan");
        }

        User user = userRepository.findByEmail(jwt.getSubject())
                .orElseThrow(() -> new NotFoundException("Foydalanuvchi topilmadi"));

        // Bloklangan foydalanuvchining eski tokeni ham ishlamasin
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AccessDeniedException("Hisobingiz bloklangan");
        }

        return user;
    }
}
