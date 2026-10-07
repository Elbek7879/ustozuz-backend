package uz.ustozuz.backend.order.dto;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String status,
        String method,
        long totalAmount,
        Instant createdAt,
        List<Item> items
) {
    public record Item(
            Long courseId,
            String slug,
            String title,
            long price
    ) {
    }
}
