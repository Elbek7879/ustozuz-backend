package uz.ustozuz.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Ism kiritilishi shart") String name,
        @NotBlank(message = "Email kiritilishi shart") @Email(message = "Email noto'g'ri formatda") String email,
        @NotBlank(message = "Parol kiritilishi shart") @Size(min = 6, message = "Parol kamida 6 ta belgidan iborat bo'lishi kerak") String password
) {
}