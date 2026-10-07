package uz.ustozuz.backend.lesson;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import uz.ustozuz.backend.course.Course;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String title;

    // Darslar tartibi: 0, 1, 2 ... ustoz panelida yuqoriga/pastga surish shunga asoslanadi
    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    // Video hali yo'q bosqichda, keyinroq to'ldiriladi
    @Column(name = "video_url")
    private String videoUrl;
}