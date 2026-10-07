package uz.ustozuz.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email kiritilishi shart") String email,
        @NotBlank(message = "Parol kiritilishi shart") String password
) {
}