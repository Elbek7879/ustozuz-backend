package uz.ustozuz.backend.lesson;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.common.exception.BadRequestException;
import uz.ustozuz.backend.common.exception.NotFoundException;
import uz.ustozuz.backend.course.Course;
import uz.ustozuz.backend.course.CourseRepository;
import uz.ustozuz.backend.enrollment.LessonProgressRepository;
import uz.ustozuz.backend.lesson.dto.LessonRequest;
import uz.ustozuz.backend.lesson.dto.LessonResponse;
import uz.ustozuz.backend.user.User;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonProgressRepository lessonProgressRepository;

    @Transactional(readOnly = true)
    public List<LessonResponse> findByCourse(User instructor, Long courseId) {
        Course course = getOwnedCourse(instructor, courseId);
        return lessonRepository.findByCourseIdOrderByOrderIndexAsc(course.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LessonResponse create(User instructor, Long courseId, LessonRequest request) {
        Course course = getOwnedCourse(instructor, courseId);

        int nextIndex = lessonRepository.findByCourseIdOrderByOrderIndexAsc(course.getId()).size();

        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(request.title().trim());
        lesson.setOrderIndex(nextIndex);

        lessonRepository.save(lesson);
        return toResponse(lesson);
    }

    @Transactional
    public LessonResponse update(User instructor, Long courseId, Long lessonId, LessonRequest request) {
        Lesson lesson = getOwnedLesson(instructor, courseId, lessonId);
        lesson.setTitle(request.title().trim());
        lessonRepository.save(lesson);
        return toResponse(lesson);
    }

    @Transactional
    public void delete(User instructor, Long courseId, Long lessonId) {
        Lesson lesson = getOwnedLesson(instructor, courseId, lessonId);
        lessonProgressRepository.deleteByLessonId(lesson.getId());
        lessonRepository.delete(lesson);
    }

    @Transactional
    public void reorder(User instructor, Long courseId, List<Long> lessonIds) {
        Course course = getOwnedCourse(instructor, courseId);
        if (lessonIds == null) {
            throw new BadRequestException("Darslar ro'yxati berilmagan");
        }

        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(course.getId());
        Map<Long, Lesson> byId = lessons.stream()
                .collect(java.util.stream.Collectors.toMap(Lesson::getId, l -> l));

        if (byId.size() != lessonIds.size() || !byId.keySet().containsAll(lessonIds)) {
            throw new BadRequestException("Darslar ro'yxati kursga mos kelmayapti");
        }

        for (int i = 0; i < lessonIds.size(); i++) {
            Lesson lesson = byId.get(lessonIds.get(i));
            lesson.setOrderIndex(i);
            lessonRepository.save(lesson);
        }
    }

    private Course getOwnedCourse(User instructor, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new AccessDeniedException("Bu kurs sizga tegishli emas");
        }

        return course;
    }

    private Lesson getOwnedLesson(User instructor, Long courseId, Long lessonId) {
        getOwnedCourse(instructor, courseId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Dars topilmadi"));

        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new BadRequestException("Dars bu kursga tegishli emas");
        }

        return lesson;
    }

    private LessonResponse toResponse(Lesson l) {
        return new LessonResponse(l.getId(), l.getTitle(), l.getOrderIndex(), l.getVideoUrl());
    }
}