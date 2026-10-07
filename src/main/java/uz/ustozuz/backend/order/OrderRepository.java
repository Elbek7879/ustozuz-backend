package uz.ustozuz.backend.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    boolean existsByItems_Course_Id(Long courseId);

    long countByStatus(OrderStatus status);

    List<Order> findTop5ByStatusOrderByCreatedAtDesc(OrderStatus status);

    @Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.status = :status")
    long sumTotalByStatus(@Param("status") OrderStatus status);
}
