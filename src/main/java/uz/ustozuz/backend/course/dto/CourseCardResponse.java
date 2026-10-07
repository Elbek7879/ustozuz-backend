package uz.ustozuz.backend.course.dto;

public record CourseCardResponse(
        Long id,
        String title,
        String slug,
        String instructorName,
        String category,
        double rating,
        int studentsCount,
        long price,
        String imageUrl
) {
}