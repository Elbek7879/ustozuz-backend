package uz.ustozuz.backend.course.dto;

import java.time.Instant;

public record InstructorCourseResponse(
        Long id,
        String title,
        String slug,
        String category,
        String description,
        long price,
        String imageUrl,
        String status,
        double rating,
        int studentsCount,
        int lessonsCount,
        Instant createdAt
) {
}
