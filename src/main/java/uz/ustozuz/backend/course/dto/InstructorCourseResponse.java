package uz.ustozuz.backend.course.dto;

import java.time.Instant;

public record InstructorCourseResponse(
        Long id,
        String title,
        String slug,
        String category,
        long price,
        String status,
        double rating,
        int studentsCount,
        Instant createdAt
) {
}