package uz.ustozuz.backend.user.dto;

import uz.ustozuz.backend.user.User;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        String role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole().name()
        );
    }
}
