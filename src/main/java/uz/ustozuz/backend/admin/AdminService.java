package uz.ustozuz.backend.admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.admin.dto.ActivityItem;
import uz.ustozuz.backend.admin.dto.AdminCourseResponse;
import uz.ustozuz.backend.admin.dto.AdminStatsResponse;
import uz.ustozuz.backend.admin.dto.AdminUserResponse;
import uz.ustozuz.backend.common.exception.BadRequestException;
import uz.ustozuz.backend.common.exception.NotFoundException;
import uz.ustozuz.backend.course.Course;
import uz.ustozuz.backend.course.CourseRepository;
import uz.ustozuz.backend.course.CourseStatus;
import uz.ustozuz.backend.order.Order;
import uz.ustozuz.backend.order.OrderRepository;
import uz.ustozuz.backend.order.OrderStatus;
import uz.ustozuz.backend.user.Role;
import uz.ustozuz.backend.user.User;
import uz.ustozuz.backend.user.UserRepository;
import uz.ustozuz.backend.user.UserStatus;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;

    // --- Statistika ---

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        return new AdminStatsResponse(
                userRepository.count(),
                userRepository.countByRole(Role.STUDENT),
                userRepository.countByRole(Role.INSTRUCTOR),
                courseRepository.count(),
                courseRepository.countByStatus(CourseStatus.ACTIVE),
                courseRepository.countByStatus(CourseStatus.DRAFT),
                orderRepository.countByStatus(OrderStatus.PAID),
                orderRepository.sumTotalByStatus(OrderStatus.PAID),
                courseRepository.averageRating(CourseStatus.ACTIVE),
                recentActivity()
        );
    }

    // So'nggi ro'yxatdan o'tishlar, yaratilgan kurslar va xaridlar — vaqt bo'yicha aralash
    private List<ActivityItem> recentActivity() {
        List<ActivityItem> items = new ArrayList<>();

        for (User u : userRepository.findTop5ByOrderByCreatedAtDesc()) {
            items.add(new ActivityItem("USER", u.getName() + " ro'yxatdan o'tdi", u.getCreatedAt()));
        }
        for (Course c : courseRepository.findTop5ByOrderByCreatedAtDesc()) {
            items.add(new ActivityItem("COURSE",
                    c.getInstructor().getName() + " \"" + c.getTitle() + "\" kursini yaratdi", c.getCreatedAt()));
        }
        for (Order o : orderRepository.findTop5ByStatusOrderByCreatedAtDesc(OrderStatus.PAID)) {
            items.add(new ActivityItem("ORDER",
                    o.getBuyerName() + " xarid qildi: " + formatSum(o.getTotalAmount()), o.getCreatedAt()));
        }

        return items.stream()
                .sorted(Comparator.comparing(ActivityItem::createdAt).reversed())
                .limit(8)
                .toList();
    }

    private static String formatSum(long amount) {
        return String.format("%,d so'm", amount).replace(',', ' ');
    }

    // --- Foydalanuvchilar ---

    @Transactional(readOnly = true)
    public List<AdminUserResponse> findUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Transactional
    public AdminUserResponse changeUserStatus(User admin, Long userId, UserStatus status) {
        User user = getOtherUser(admin, userId);
        user.setStatus(status);
        return toUserResponse(userRepository.save(user));
    }

    // Yangi rol foydalanuvchi qayta kirganda (yangi token olganda) kuchga kiradi
    @Transactional
    public AdminUserResponse changeUserRole(User admin, Long userId, Role role) {
        User user = getOtherUser(admin, userId);
        user.setRole(role);
        return toUserResponse(userRepository.save(user));
    }

    private User getOtherUser(User admin, Long userId) {
        if (admin.getId().equals(userId)) {
            throw new BadRequestException("O'z hisobingizni o'zgartira olmaysiz");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Foydalanuvchi topilmadi"));
    }

    private AdminUserResponse toUserResponse(User u) {
        return new AdminUserResponse(
                u.getId(), u.getName(), u.getEmail(), u.getPhone(),
                u.getRole().name(), u.getStatus().name(), u.getCreatedAt()
        );
    }

    // --- Kurslar ---

    @Transactional(readOnly = true)
    public List<AdminCourseResponse> findCourses() {
        return courseRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(c -> new AdminCourseResponse(
                        c.getId(),
                        c.getTitle(),
                        c.getSlug(),
                        c.getInstructor().getName(),
                        c.getCategory().getTitle(),
                        c.getPriceAmount(),
                        c.getStatus().name(),
                        c.getRatingAvg(),
                        c.getStudentsCount(),
                        c.getCreatedAt()
                ))
                .toList();
    }
}
