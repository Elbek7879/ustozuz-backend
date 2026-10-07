package uz.ustozuz.backend.enrollment;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.certificate.Certificate;
import uz.ustozuz.backend.certificate.CertificateRepository;
import uz.ustozuz.backend.common.exception.BadRequestException;
import uz.ustozuz.backend.common.exception.NotFoundException;
import uz.ustozuz.backend.course.Course;
import uz.ustozuz.backend.course.CourseRepository;
import uz.ustozuz.backend.course.CourseService;
import uz.ustozuz.backend.enrollment.dto.MyCourseDetailResponse;
import uz.ustozuz.backend.enrollment.dto.MyCourseResponse;
import uz.ustozuz.backend.enrollment.dto.MyLessonResponse;
import uz.ustozuz.backend.lesson.Lesson;
import uz.ustozuz.backend.lesson.LessonRepository;
import uz.ustozuz.backend.user.User;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final CertificateRepository certificateRepository;

    // Buyurtma to'langanda chaqiriladi
    @Transactional
    public void enroll(User student, Course course) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            return;
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);

        course.setStudentsCount(course.getStudentsCount() + 1);
        courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Transactional(readOnly = true)
    public List<MyCourseResponse> findMyCourses(User student) {
        return enrollmentRepository.findByStudentIdOrderByEnrolledAtDesc(student.getId()).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public MyCourseDetailResponse getMyCourse(User student, String slug) {
        return toDetail(getEnrollment(student, slug));
    }

    @Transactional
    public MyCourseDetailResponse setLessonCompleted(User student, String slug, Long lessonId, boolean completed) {
        Enrollment enrollment = getEnrollment(student, slug);

        Lesson lesson = lessonRepository.findById(lessonId)
                .filter(l -> l.getCourse().getId().equals(enrollment.getCourse().getId()))
                .orElseThrow(() -> new BadRequestException("Dars bu kursga tegishli emas"));

        LessonProgress progress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollment.getId(), lesson.getId())
                .orElseGet(() -> {
                    LessonProgress p = new LessonProgress();
                    p.setEnrollment(enrollment);
                    p.setLesson(lesson);
                    return p;
                });
        progress.setCompleted(completed);
        lessonProgressRepository.save(progress);

        updateProgress(enrollment);
        return toDetail(enrollment);
    }

    // --- Yordamchilar ---

    private Enrollment getEnrollment(User student, String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi"));
        return enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseThrow(() -> new NotFoundException("Siz bu kursga yozilmagansiz"));
    }

    private Set<Long> completedLessonIds(Enrollment enrollment) {
        return lessonProgressRepository.findByEnrollmentId(enrollment.getId()).stream()
                .filter(LessonProgress::isCompleted)
                .map(p -> p.getLesson().getId())
                .collect(Collectors.toSet());
    }

    private static int percent(int done, int total) {
        return total == 0 ? 0 : done * 100 / total;
    }

    // Progress saqlanadi; 100% bo'lganda kurs tugallangan deb belgilanadi va sertifikat beriladi
    private void updateProgress(Enrollment enrollment) {
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(enrollment.getCourse().getId());
        Set<Long> done = completedLessonIds(enrollment);
        int completed = (int) lessons.stream().filter(l -> done.contains(l.getId())).count();
        int progress = percent(completed, lessons.size());

        enrollment.setProgress(progress);
        if (progress == 100) {
            if (enrollment.getCompletedAt() == null) {
                enrollment.setCompletedAt(Instant.now());
            }
            if (certificateRepository.findByEnrollmentId(enrollment.getId()).isEmpty()) {
                Certificate certificate = new Certificate();
                certificate.setEnrollment(enrollment);
                certificateRepository.save(certificate);
            }
        } else {
            enrollment.setCompletedAt(null);
        }
        enrollmentRepository.save(enrollment);
    }

    private MyCourseResponse toSummary(Enrollment e) {
        Course c = e.getCourse();
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(c.getId());
        Set<Long> done = completedLessonIds(e);
        int completed = (int) lessons.stream().filter(l -> done.contains(l.getId())).count();

        return new MyCourseResponse(
                c.getId(),
                c.getSlug(),
                c.getTitle(),
                c.getInstructor().getName(),
                c.getCategory().getTitle(),
                imageOf(c),
                percent(completed, lessons.size()),
                lessons.size(),
                completed,
                e.getEnrolledAt(),
                e.getCompletedAt()
        );
    }

    private MyCourseDetailResponse toDetail(Enrollment e) {
        Course c = e.getCourse();
        Set<Long> done = completedLessonIds(e);
        List<MyLessonResponse> lessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(c.getId()).stream()
                .map(l -> new MyLessonResponse(
                        l.getId(), l.getTitle(), l.getOrderIndex(), l.getVideoUrl(), done.contains(l.getId())))
                .toList();
        int completed = (int) lessons.stream().filter(MyLessonResponse::completed).count();
        Long certificateId = certificateRepository.findByEnrollmentId(e.getId())
                .map(Certificate::getId)
                .orElse(null);

        return new MyCourseDetailResponse(
                c.getId(),
                c.getSlug(),
                c.getTitle(),
                c.getDescription(),
                c.getInstructor().getName(),
                c.getCategory().getTitle(),
                imageOf(c),
                percent(completed, lessons.size()),
                e.getCompletedAt(),
                certificateId,
                lessons
        );
    }

    private static String imageOf(Course c) {
        return c.getImageUrl() != null ? c.getImageUrl() : CourseService.DEFAULT_IMAGE_URL;
    }
}
