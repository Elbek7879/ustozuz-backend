package uz.ustozuz.backend.admin.dto;

import java.time.Instant;

// type: USER | COURSE | ORDER — frontend ikonka tanlashi uchun
public record ActivityItem(
        String type,
        String text,
        Instant createdAt
) {
}
