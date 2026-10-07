package uz.ustozuz.backend.order;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.common.exception.BadRequestException;
import uz.ustozuz.backend.common.exception.NotFoundException;
import uz.ustozuz.backend.course.Course;
import uz.ustozuz.backend.course.CourseRepository;
import uz.ustozuz.backend.course.CourseStatus;
import uz.ustozuz.backend.enrollment.EnrollmentService;
import uz.ustozuz.backend.order.dto.CheckoutRequest;
import uz.ustozuz.backend.order.dto.OrderResponse;
import uz.ustozuz.backend.payment.PaymentProvider;
import uz.ustozuz.backend.payment.PaymentResult;
import uz.ustozuz.backend.user.User;
import uz.ustozuz.backend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;
    private final PaymentProvider paymentProvider;

    // Savatdagi kurslar uchun buyurtma yaratadi, to'lovni o'tkazadi va talabani kurslarga yozadi
    @Transactional
    public OrderResponse checkout(User buyer, CheckoutRequest request) {
        List<Long> ids = request.courseIds().stream().distinct().toList();
        Map<Long, Course> byId = courseRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setBuyerName(request.buyerName().trim());
        order.setBuyerPhone(request.buyerPhone());
        order.setMethod(request.method());

        long total = 0;
        for (Long id : ids) {
            Course course = byId.get(id);
            if (course == null) {
                throw new NotFoundException("Kurs topilmadi (id: " + id + ")");
            }
            if (course.getStatus() != CourseStatus.ACTIVE) {
                throw new BadRequestException("Bu kurs hozir sotuvda emas: " + course.getTitle());
            }
            if (course.getInstructor().getId().equals(buyer.getId())) {
                throw new BadRequestException("O'z kursingizni sotib ololmaysiz: " + course.getTitle());
            }
            if (enrollmentService.isEnrolled(buyer.getId(), course.getId())) {
                throw new BadRequestException("Siz bu kursni allaqachon sotib olgansiz: " + course.getTitle());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setCourse(course);
            item.setPrice(course.getPriceAmount());
            order.getItems().add(item);
            total += course.getPriceAmount();
        }
        order.setTotalAmount(total);
        orderRepository.save(order);

        PaymentResult payment = paymentProvider.charge(order);
        if (!payment.success()) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw new BadRequestException("To'lov amalga oshmadi: " + payment.message());
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        for (OrderItem item : order.getItems()) {
            enrollmentService.enroll(buyer, item.getCourse());
        }

        // Telefon raqami profilda bo'lmasa, buyurtmadagisini saqlab qo'yamiz
        if (buyer.getPhone() == null || buyer.getPhone().isBlank()) {
            buyer.setPhone(request.buyerPhone());
            userRepository.save(buyer);
        }

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findMyOrders(User buyer) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderResponse.Item> items = order.getItems().stream()
                .map(i -> new OrderResponse.Item(
                        i.getCourse().getId(), i.getCourse().getSlug(), i.getCourse().getTitle(), i.getPrice()))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getMethod().name(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items
        );
    }
}
