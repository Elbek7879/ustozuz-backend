package uz.ustozuz.backend.admin.dto;

import jakarta.validation.constraints.NotNull;

import uz.ustozuz.backend.user.UserStatus;

public record UserStatusRequest(
        @NotNull UserStatus status
) {
}
