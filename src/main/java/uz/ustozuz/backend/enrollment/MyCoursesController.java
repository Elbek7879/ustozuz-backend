package uz.ustozuz.backend.enrollment;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.enrollment.dto.LessonCompletionRequest;
import uz.ustozuz.backend.enrollment.dto.MyCourseDetailResponse;
import uz.ustozuz.backend.enrollment.dto.MyCourseResponse;
import uz.ustozuz.backend.user.CurrentUserService;

// Talabaning sotib olgan kurslari
@RestController
@RequestMapping("/api/me/courses")
@RequiredArgsConstructor
public class MyCoursesController {

    private final EnrollmentService enrollmentService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<MyCourseResponse> myCourses(@AuthenticationPrincipal Jwt jwt) {
        return enrollmentService.findMyCourses(currentUserService.require(jwt));
    }

    @GetMapping("/{slug}")
    public MyCourseDetailResponse myCourse(@AuthenticationPrincipal Jwt jwt, @PathVariable String slug) {
        return enrollmentService.getMyCourse(currentUserService.require(jwt), slug);
    }

    @PutMapping("/{slug}/lessons/{lessonId}")
    public MyCourseDetailResponse setLessonCompleted(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String slug,
            @PathVariable Long lessonId,
            @RequestBody LessonCompletionRequest request
    ) {
        return enrollmentService.setLessonCompleted(
                currentUserService.require(jwt), slug, lessonId, request.completed());
    }
}
