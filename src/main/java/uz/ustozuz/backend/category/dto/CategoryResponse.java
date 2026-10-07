package uz.ustozuz.backend.category.dto;

public record CategoryResponse(
        Long id,
        String title,
        String description,
        String icon,
        long coursesCount
) {
}
