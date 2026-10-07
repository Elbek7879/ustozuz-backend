package uz.ustozuz.backend.auth.dto;

import uz.ustozuz.backend.user.dto.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {
}