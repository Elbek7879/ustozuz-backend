package uz.ustozuz.backend.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequest(
        @NotBlank(message = "Ism kiritilishi shart") @Size(max = 100) String name,
        @NotBlank(message = "Email kiritilishi shart") @Email(message = "Email noto'g'ri formatda") String email,
        @NotBlank(message = "Xabar kiritilishi shart") @Size(max = 5000) String message
) {
}
