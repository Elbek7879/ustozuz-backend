package uz.ustozuz.backend.stats;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.course.CourseRepository;
import uz.ustozuz.backend.course.CourseStatus;
import uz.ustozuz.backend.stats.dto.PublicStatsResponse;
import uz.ustozuz.backend.user.Role;
import uz.ustozuz.backend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // Ochiq statistikada faqat faol (katalogdagi) kurslar hisoblanadi
    @Transactional(readOnly = true)
    public PublicStatsResponse getPublicStats() {
        long totalCourses = courseRepository.countByStatus(CourseStatus.ACTIVE);
        long totalInstructors = userRepository.countByRole(Role.INSTRUCTOR);
        long totalStudents = userRepository.countByRole(Role.STUDENT);
        double avgRating = courseRepository.averageRating(CourseStatus.ACTIVE);

        return new PublicStatsResponse(totalCourses, totalStudents, totalInstructors, avgRating);
    }
}
