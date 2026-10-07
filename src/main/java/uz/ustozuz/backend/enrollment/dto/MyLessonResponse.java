package uz.ustozuz.backend.enrollment.dto;

public record MyLessonResponse(
        Long id,
        String title,
        int orderIndex,
        String videoUrl,
        boolean completed
) {
}
