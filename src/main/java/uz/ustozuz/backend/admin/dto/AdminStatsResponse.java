package uz.ustozuz.backend.admin.dto;

import java.util.List;

public record AdminStatsResponse(
        long totalUsers,
        long totalStudents,
        long totalInstructors,
        long totalCourses,
        long activeCourses,
        long draftCourses,
        long paidOrders,
        long totalRevenue,
        double avgRating,
        List<ActivityItem> recentActivity
) {
}
