package uz.ustozuz.backend.stats.dto;

public record PublicStatsResponse(
        long totalCourses,
        long totalStudents,
        long totalInstructors,
        double avgRating
) {
}