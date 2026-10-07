package uz.ustozuz.backend.admin;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.admin.dto.AdminCourseResponse;
import uz.ustozuz.backend.course.CourseService;
import uz.ustozuz.backend.course.dto.CourseStatusRequest;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final AdminService adminService;
    private final CourseService courseService;

    @GetMapping
    public List<AdminCourseResponse> courses() {
        return adminService.findCourses();
    }

    @PutMapping("/{id}/status")
    public void changeStatus(@PathVariable Long id, @Valid @RequestBody CourseStatusRequest request) {
        courseService.changeStatusAsAdmin(id, request.status());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        courseService.deleteAsAdmin(id);
    }
}
