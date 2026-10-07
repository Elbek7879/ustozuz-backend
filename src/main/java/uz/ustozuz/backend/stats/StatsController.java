package uz.ustozuz.backend.stats;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.stats.dto.PublicStatsResponse;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/public")
    public PublicStatsResponse getPublicStats() {
        return statsService.getPublicStats();
    }
}