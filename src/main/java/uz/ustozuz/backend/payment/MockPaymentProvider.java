package uz.ustozuz.backend.payment;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uz.ustozuz.backend.order.Order;

// Namunaviy to'lov: har doim muvaffaqiyatli. Haqiqiy pul o'tmaydi.
@Component
public class MockPaymentProvider implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentProvider.class);

    @Override
    public PaymentResult charge(Order order) {
        String transactionId = "MOCK-" + UUID.randomUUID();
        log.info("Namunaviy to'lov: buyurtma #{}, {} so'm, {} -> {}",
                order.getId(), order.getTotalAmount(), order.getMethod(), transactionId);
        return PaymentResult.ok(transactionId);
    }
}
