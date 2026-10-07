package uz.ustozuz.backend.lesson.dto;

public record LessonResponse(
        Long id,
        String title,
        int orderIndex,
        String videoUrl
) {
}