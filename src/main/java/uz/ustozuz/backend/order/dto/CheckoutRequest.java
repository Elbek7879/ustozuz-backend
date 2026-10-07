package uz.ustozuz.backend.order.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import uz.ustozuz.backend.order.PaymentMethod;

public record CheckoutRequest(
        @NotEmpty(message = "Savat bo'sh") List<Long> courseIds,
        @NotBlank(message = "Ism kiritilishi shart") String buyerName,
        @NotBlank(message = "Telefon kiritilishi shart")
        @Pattern(regexp = "[0-9]{9}", message = "Telefon 9 ta raqamdan iborat bo'lishi kerak") String buyerPhone,
        @NotNull(message = "To'lov usuli tanlanishi shart") PaymentMethod method
) {
}
