package uz.ustozuz.backend.course;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.common.dto.PageResponse;
import uz.ustozuz.backend.course.dto.CourseCardResponse;
import uz.ustozuz.backend.course.dto.CourseDetailResponse;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public PageResponse<CourseCardResponse> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 12) Pageable pageable
    ) {
        return PageResponse.from(courseService.search(category, q, pageable));
    }

    @GetMapping("/{slug}")
    public CourseDetailResponse getBySlug(@PathVariable String slug) {
        return courseService.findBySlug(slug);
    }
}