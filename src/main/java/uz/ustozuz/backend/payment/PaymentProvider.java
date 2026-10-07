package uz.ustozuz.backend.payment;

import uz.ustozuz.backend.order.Order;

// Payme/Click ulanganda shu interfeysning haqiqiy amalga oshirilishi yoziladi
public interface PaymentProvider {

    PaymentResult charge(Order order);
}
