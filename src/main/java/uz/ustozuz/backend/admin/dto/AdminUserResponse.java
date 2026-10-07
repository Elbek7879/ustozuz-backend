package uz.ustozuz.backend.admin.dto;

import java.time.Instant;

public record AdminUserResponse(
        Long id,
        String name,
        String email,
        String phone,
        String role,
        String status,
        Instant createdAt
) {
}
