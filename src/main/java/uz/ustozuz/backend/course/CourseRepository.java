package uz.ustozuz.backend.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Optional<Course> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Course> findByInstructorId(Long instructorId);

    List<Course> findAllByOrderByCreatedAtDesc();

    List<Course> findTop5ByOrderByCreatedAtDesc();

    long countByStatus(CourseStatus status);

    long countByCategoryIdAndStatus(Long categoryId, CourseStatus status);

    @Query("select coalesce(avg(c.ratingAvg), 0) from Course c where c.status = :status and c.ratingCount > 0")
    double averageRating(@Param("status") CourseStatus status);

    @Query("select coalesce(sum(c.studentsCount), 0) from Course c where c.status = :status")
    long sumStudentsCount(@Param("status") CourseStatus status);
}
