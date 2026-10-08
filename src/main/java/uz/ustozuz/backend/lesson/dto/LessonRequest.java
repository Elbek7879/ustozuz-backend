package uz.ustozuz.backend.lesson.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LessonRequest(
        @NotBlank(message = "Dars nomi kiritilishi shart") String title,
        // Ixtiyoriy YouTube havolasi; bo'sh bo'lsa dars videosiz
        @Size(max = 300) String videoUrl
) {
}
