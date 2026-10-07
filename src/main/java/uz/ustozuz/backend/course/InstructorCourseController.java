package uz.ustozuz.backend.course;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.course.dto.CourseRequest;
import uz.ustozuz.backend.course.dto.CourseStatusRequest;
import uz.ustozuz.backend.course.dto.InstructorCourseResponse;
import uz.ustozuz.backend.user.CurrentUserService;
import uz.ustozuz.backend.user.User;

@RestController
@RequestMapping("/api/instructor/courses")
@RequiredArgsConstructor
public class InstructorCourseController {

    private final CourseService courseService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<InstructorCourseResponse> myCourses(@AuthenticationPrincipal Jwt jwt) {
        User instructor = currentUserService.require(jwt);
        return courseService.findByInstructor(instructor.getId());
    }

    @PostMapping
    public InstructorCourseResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CourseRequest request
    ) {
        User instructor = currentUserService.require(jwt);
        return courseService.create(instructor, request);
    }

    @PutMapping("/{id}")
    public InstructorCourseResponse update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request
    ) {
        User instructor = currentUserService.require(jwt);
        return courseService.update(instructor, id, request);
    }

    @PutMapping("/{id}/status")
    public InstructorCourseResponse updateStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody CourseStatusRequest request
    ) {
        User instructor = currentUserService.require(jwt);
        return courseService.updateStatus(instructor, id, request.status());
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        User instructor = currentUserService.require(jwt);
        courseService.delete(instructor, id);
    }
}