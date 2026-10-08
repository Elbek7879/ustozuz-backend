package uz.ustozuz.backend.course;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.category.Category;
import uz.ustozuz.backend.category.CategoryRepository;
import uz.ustozuz.backend.common.exception.BadRequestException;
import uz.ustozuz.backend.common.exception.ConflictException;
import uz.ustozuz.backend.common.exception.NotFoundException;
import uz.ustozuz.backend.common.util.SlugUtil;
import uz.ustozuz.backend.course.dto.CourseCardResponse;
import uz.ustozuz.backend.course.dto.CourseDetailResponse;
import uz.ustozuz.backend.course.dto.CourseRequest;
import uz.ustozuz.backend.course.dto.InstructorCourseResponse;
import uz.ustozuz.backend.enrollment.EnrollmentRepository;
import uz.ustozuz.backend.lesson.Lesson;
import uz.ustozuz.backend.lesson.LessonRepository;
import uz.ustozuz.backend.order.OrderRepository;
import uz.ustozuz.backend.user.User;

@Service
@RequiredArgsConstructor
public class CourseService {

    // Ustoz rasm bermasa qo'yiladigan muqova
    public static final String DEFAULT_IMAGE_URL =
            "https://images.unsplash.com/photo-1501504905252-473c47e087f8?w=400&q=80";

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OrderRepository orderRepository;

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

    // Faqat faol kurslar ochiq ko'rinadi; qoralama va yashirin kurslar uchun 404
    @Transactional(readOnly = true)
    public CourseDetailResponse findBySlug(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .filter(c -> c.getStatus() == CourseStatus.ACTIVE)
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

    @Transactional(readOnly = true)
    public InstructorCourseResponse findOwned(User instructor, Long courseId) {
        return toInstructorResponse(getOwnedCourse(instructor, courseId));
    }

    @Transactional
    public InstructorCourseResponse create(User instructor, CourseRequest request) {
        Category category = findCategory(request.category());

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
        Category category = findCategory(request.category());

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
        changeStatus(course, statusValue);
        return toInstructorResponse(course);
    }

    @Transactional
    public void delete(User instructor, Long courseId) {
        deleteCourse(getOwnedCourse(instructor, courseId));
    }

    // --- Admin qismi (egalik tekshirilmaydi) ---

    @Transactional
    public void changeStatusAsAdmin(Long courseId, String statusValue) {
        changeStatus(getCourse(courseId), statusValue);
    }

    @Transactional
    public void deleteAsAdmin(Long courseId) {
        deleteCourse(getCourse(courseId));
    }

    // --- Yordamchilar ---

    private Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi"));
    }

    private Course getOwnedCourse(User instructor, Long courseId) {
        Course course = getCourse(courseId);

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new AccessDeniedException("Bu kurs sizga tegishli emas");
        }

        return course;
    }

    private void changeStatus(Course course, String statusValue) {
        try {
            course.setStatus(CourseStatus.valueOf(statusValue));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Noto'g'ri holat: " + statusValue);
        }
        courseRepository.save(course);
    }

    // Sotib olingan kursni o'chirib bo'lmaydi: talabalar kursini yo'qotmasligi kerak
    private void deleteCourse(Course course) {
        if (enrollmentRepository.existsByCourseId(course.getId())
                || orderRepository.existsByItems_Course_Id(course.getId())) {
            throw new ConflictException(
                    "Bu kursni sotib olgan talabalar bor. O'chirish o'rniga uni yashiring.");
        }
        lessonRepository.deleteByCourseId(course.getId());
        courseRepository.delete(course);
    }

    private Category findCategory(String title) {
        return categoryRepository.findByTitle(title)
                .orElseThrow(() -> new BadRequestException("Kategoriya topilmadi: " + title));
    }

    private void applyRequest(Course course, CourseRequest request, Category category) {
        course.setTitle(request.title().trim());
        course.setDescription(request.description());
        course.setPriceAmount(request.price());
        course.setCategory(category);

        String imageUrl = request.imageUrl() == null ? "" : request.imageUrl().trim();
        if (!imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("https://")) {
                throw new BadRequestException("Rasm havolasi https:// bilan boshlanishi kerak");
            }
            course.setImageUrl(imageUrl);
        } else if (course.getImageUrl() == null) {
            course.setImageUrl(DEFAULT_IMAGE_URL);
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

    private static String imageOrDefault(Course c) {
        return c.getImageUrl() != null ? c.getImageUrl() : DEFAULT_IMAGE_URL;
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
                imageOrDefault(c)
        );
    }

    private CourseDetailResponse toDetail(Course c) {
        List<Lesson> courseLessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(c.getId());
        List<String> lessons = courseLessons.stream().map(Lesson::getTitle).toList();
        String previewVideoUrl = courseLessons.isEmpty() ? null : courseLessons.get(0).getVideoUrl();

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
                imageOrDefault(c),
                lessons,
                previewVideoUrl
        );
    }

    private InstructorCourseResponse toInstructorResponse(Course c) {
        return new InstructorCourseResponse(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getCategory().getTitle(),
                c.getDescription(),
                c.getPriceAmount(),
                imageOrDefault(c),
                c.getStatus().name(),
                c.getRatingAvg(),
                c.getStudentsCount(),
                lessonRepository.countByCourseId(c.getId()),
                c.getCreatedAt()
        );
    }
}
