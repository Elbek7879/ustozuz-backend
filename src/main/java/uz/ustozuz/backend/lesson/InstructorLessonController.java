package uz.ustozuz.backend.lesson;

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

import uz.ustozuz.backend.lesson.dto.LessonRequest;
import uz.ustozuz.backend.lesson.dto.LessonResponse;
import uz.ustozuz.backend.lesson.dto.ReorderLessonsRequest;
import uz.ustozuz.backend.user.CurrentUserService;
import uz.ustozuz.backend.user.User;

@RestController
@RequestMapping("/api/instructor/courses/{courseId}/lessons")
@RequiredArgsConstructor
public class InstructorLessonController {

    private final LessonService lessonService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<LessonResponse> list(@AuthenticationPrincipal Jwt jwt, @PathVariable Long courseId) {
        User instructor = currentUserService.require(jwt);
        return lessonService.findByCourse(instructor, courseId);
    }

    @PostMapping
    public LessonResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long courseId,
            @Valid @RequestBody LessonRequest request
    ) {
        User instructor = currentUserService.require(jwt);
        return lessonService.create(instructor, courseId, request);
    }

    @PutMapping("/{lessonId}")
    public LessonResponse update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonRequest request
    ) {
        User instructor = currentUserService.require(jwt);
        return lessonService.update(instructor, courseId, lessonId, request);
    }

    @DeleteMapping("/{lessonId}")
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long courseId,
            @PathVariable Long lessonId
    ) {
        User instructor = currentUserService.require(jwt);
        lessonService.delete(instructor, courseId, lessonId);
    }

    @PutMapping("/reorder")
    public void reorder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long courseId,
            @RequestBody ReorderLessonsRequest request
    ) {
        User instructor = currentUserService.require(jwt);
        lessonService.reorder(instructor, courseId, request.lessonIds());
    }
}