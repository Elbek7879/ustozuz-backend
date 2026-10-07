package uz.ustozuz.backend.admin.dto;

import java.time.Instant;

public record AdminCourseResponse(
        Long id,
        String title,
        String slug,
        String instructorName,
        String category,
        long price,
        String status,
        double rating,
        int studentsCount,
        Instant createdAt
) {
}
