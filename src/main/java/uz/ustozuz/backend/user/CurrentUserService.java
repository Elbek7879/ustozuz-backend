package uz.ustozuz.backend.user;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.common.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User require(Jwt jwt) {
        return userRepository.findByEmail(jwt.getSubject())
                .orElseThrow(() -> new NotFoundException("Foydalanuvchi topilmadi"));
    }
}