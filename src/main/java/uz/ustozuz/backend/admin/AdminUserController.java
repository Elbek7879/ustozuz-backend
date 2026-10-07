package uz.ustozuz.backend.admin;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.admin.dto.AdminUserResponse;
import uz.ustozuz.backend.admin.dto.UserRoleRequest;
import uz.ustozuz.backend.admin.dto.UserStatusRequest;
import uz.ustozuz.backend.user.CurrentUserService;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminService adminService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<AdminUserResponse> users() {
        return adminService.findUsers();
    }

    @PutMapping("/{id}/status")
    public AdminUserResponse changeStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody UserStatusRequest request
    ) {
        return adminService.changeUserStatus(currentUserService.require(jwt), id, request.status());
    }

    @PutMapping("/{id}/role")
    public AdminUserResponse changeRole(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody UserRoleRequest request
    ) {
        return adminService.changeUserRole(currentUserService.require(jwt), id, request.role());
    }
}
