package uz.ustozuz.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Ism kiritilishi shart") @Size(max = 100) String name,
        // Bo'sh qoldirish mumkin, aks holda 9 ta raqam (masalan 901234567)
        @Pattern(regexp = "^$|[0-9]{9}", message = "Telefon 9 ta raqamdan iborat bo'lishi kerak") String phone
) {
}
