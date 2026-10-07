package uz.ustozuz.backend.lesson.dto;

import jakarta.validation.constraints.NotBlank;

public record LessonRequest(
        @NotBlank(message = "Dars nomi kiritilishi shart") String title
) {
}