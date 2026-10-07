package uz.ustozuz.backend.enrollment.dto;

import java.time.Instant;
import java.util.List;

public record MyCourseDetailResponse(
        Long courseId,
        String slug,
        String title,
        String description,
        String instructorName,
        String category,
        String imageUrl,
        int progress,
        Instant completedAt,
        Long certificateId,
        List<MyLessonResponse> lessons
) {
}
