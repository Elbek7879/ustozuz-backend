package uz.ustozuz.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 6, message = "Parol kamida 6 ta belgidan iborat bo'lishi kerak") String newPassword
) {
}