package uz.ustozuz.backend.stats;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.course.CourseRepository;
import uz.ustozuz.backend.stats.dto.PublicStatsResponse;
import uz.ustozuz.backend.user.Role;
import uz.ustozuz.backend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public PublicStatsResponse getPublicStats() {
        long totalCourses = courseRepository.count();
        long totalInstructors = userRepository.countByRole(Role.INSTRUCTOR);
        long totalStudents = userRepository.countByRole(Role.STUDENT);

        double avgRating = courseRepository.findAll().stream()
                .mapToDouble(c -> c.getRatingAvg())
                .average()
                .orElse(0);

        return new PublicStatsResponse(totalCourses, totalStudents, totalInstructors, avgRating);
    }
}