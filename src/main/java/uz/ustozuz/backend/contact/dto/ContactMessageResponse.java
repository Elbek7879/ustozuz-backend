package uz.ustozuz.backend.contact.dto;

import java.time.Instant;

public record ContactMessageResponse(
        Long id,
        String name,
        String email,
        String message,
        Instant createdAt
) {
}
