package uz.ustozuz.backend.lesson.dto;

import java.util.List;

public record ReorderLessonsRequest(
        List<Long> lessonIds
) {
}