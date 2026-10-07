package uz.ustozuz.backend.course;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.category.Category;
import uz.ustozuz.backend.category.CategoryRepository;
import uz.ustozuz.backend.common.exception.BadRequestException;
import uz.ustozuz.backend.common.exception.NotFoundException;
import uz.ustozuz.backend.common.util.SlugUtil;
import uz.ustozuz.backend.course.dto.CourseCardResponse;
import uz.ustozuz.backend.course.dto.CourseDetailResponse;
import uz.ustozuz.backend.course.dto.CourseRequest;
import uz.ustozuz.backend.course.dto.InstructorCourseResponse;
import uz.ustozuz.backend.user.User;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    // --- Ochiq qismlar ---

    @Transactional(readOnly = true)
    public Page<CourseCardResponse> search(String category, String q, Pageable pageable) {
        Specification<Course> spec = Specification.allOf();

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), CourseStatus.ACTIVE));

        if (category != null && !category.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("title"), category));
        }

        if (q != null && !q.isBlank()) {
            String pattern = "%" + q.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("instructor").get("name")), pattern),
                    cb.like(cb.lower(root.get("category").get("title")), pattern)
            ));
        }

        return courseRepository.findAll(spec, pageable).map(this::toCard);
    }

    @Transactional(readOnly = true)
    public CourseDetailResponse findBySlug(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi: " + slug));
        return toDetail(course);
    }

    // --- Ustoz qismi ---

    @Transactional(readOnly = true)
    public List<InstructorCourseResponse> findByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId).stream()
                .map(this::toInstructorResponse)
                .toList();
    }

    @Transactional
    public InstructorCourseResponse create(User instructor, CourseRequest request) {
        Category category = categoryRepository.findByTitle(request.category())
                .orElseThrow(() -> new BadRequestException("Kategoriya topilmadi: " + request.category()));

        Course course = new Course();
        applyRequest(course, request, category);
        course.setInstructor(instructor);
        course.setStatus(CourseStatus.DRAFT);
        course.setSlug(uniqueSlug(request.title()));

        courseRepository.save(course);
        return toInstructorResponse(course);
    }

    @Transactional
    public InstructorCourseResponse update(User instructor, Long courseId, CourseRequest request) {
        Course course = getOwnedCourse(instructor, courseId);

        Category category = categoryRepository.findByTitle(request.category())
                .orElseThrow(() -> new BadRequestException("Kategoriya topilmadi: " + request.category()));

        boolean titleChanged = !course.getTitle().equals(request.title());
        applyRequest(course, request, category);

        if (titleChanged) {
            course.setSlug(uniqueSlug(request.title()));
        }

        courseRepository.save(course);
        return toInstructorResponse(course);
    }

    @Transactional
    public InstructorCourseResponse updateStatus(User instructor, Long courseId, String statusValue) {
        Course course = getOwnedCourse(instructor, courseId);

        CourseStatus status;
        try {
            status = CourseStatus.valueOf(statusValue);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Noto'g'ri holat: " + statusValue);
        }

        course.setStatus(status);
        courseRepository.save(course);
        return toInstructorResponse(course);
    }

    @Transactional
    public void delete(User instructor, Long courseId) {
        Course course = getOwnedCourse(instructor, courseId);
        courseRepository.delete(course);
    }

    // --- Yordamchilar ---

    private Course getOwnedCourse(User instructor, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new BadRequestException("Bu kurs sizga tegishli emas");
        }

        return course;
    }

    private void applyRequest(Course course, CourseRequest request, Category category) {
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setPriceAmount(request.price());
        course.setCategory(category);
        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            course.setImageUrl(request.imageUrl());
        }
    }

    private String uniqueSlug(String title) {
        String base = SlugUtil.slugify(title);
        String slug = base;
        int suffix = 1;
        while (courseRepository.existsBySlug(slug)) {
            suffix++;
            slug = base + "-" + suffix;
        }
        return slug;
    }

    private CourseCardResponse toCard(Course c) {
        return new CourseCardResponse(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getInstructor().getName(),
                c.getCategory().getTitle(),
                c.getRatingAvg(),
                c.getStudentsCount(),
                c.getPriceAmount(),
                c.getImageUrl()
        );
    }

    private CourseDetailResponse toDetail(Course c) {
        return new CourseDetailResponse(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getDescription(),
                c.getInstructor().getName(),
                c.getCategory().getTitle(),
                c.getRatingAvg(),
                c.getRatingCount(),
                c.getStudentsCount(),
                c.getPriceAmount(),
                c.getImageUrl()
        );
    }

    private InstructorCourseResponse toInstructorResponse(Course c) {
        return new InstructorCourseResponse(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getCategory().getTitle(),
                c.getPriceAmount(),
                c.getStatus().name(),
                c.getRatingAvg(),
                c.getStudentsCount(),
                c.getCreatedAt()
        );
    }
}