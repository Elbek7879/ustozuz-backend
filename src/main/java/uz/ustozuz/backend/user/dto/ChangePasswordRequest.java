package uz.ustozuz.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Joriy parol kiritilishi shart") String currentPassword,
        @NotBlank @Size(min = 6, message = "Parol kamida 6 ta belgidan iborat bo'lishi kerak") String newPassword
) {
}
