package uz.ustozuz.backend.admin.dto;

import jakarta.validation.constraints.NotNull;

import uz.ustozuz.backend.user.Role;

public record UserRoleRequest(
        @NotNull Role role
) {
}
