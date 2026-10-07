package uz.ustozuz.backend.user.dto;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        String role
) {
}