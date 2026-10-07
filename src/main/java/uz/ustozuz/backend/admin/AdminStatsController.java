package uz.ustozuz.backend.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.admin.dto.AdminStatsResponse;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminService adminService;

    @GetMapping
    public AdminStatsResponse stats() {
        return adminService.getStats();
    }
}
