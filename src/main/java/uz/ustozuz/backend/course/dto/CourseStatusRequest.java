package uz.ustozuz.backend.course.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseStatusRequest(
        @NotBlank String status
) {
}