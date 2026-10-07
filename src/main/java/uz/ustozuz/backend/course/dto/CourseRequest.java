package uz.ustozuz.backend.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CourseRequest(
        @NotBlank(message = "Kurs nomi kiritilishi shart") String title,
        @NotBlank(message = "Kategoriya tanlanishi shart") String category,
        @NotBlank(message = "Tavsif kiritilishi shart") String description,
        @Min(value = 0, message = "Narx manfiy bo'lishi mumkin emas") long price,
        String imageUrl
) {
}