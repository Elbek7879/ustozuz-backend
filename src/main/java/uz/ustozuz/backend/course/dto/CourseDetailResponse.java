package uz.ustozuz.backend.course.dto;

public record CourseDetailResponse(
        Long id,
        String title,
        String slug,
        String description,
        String instructorName,
        String category,
        double rating,
        int ratingCount,
        int studentsCount,
        long price,
        String imageUrl
) {
}