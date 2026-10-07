package uz.ustozuz.backend.payment;

public record PaymentResult(
        boolean success,
        String transactionId,
        String message
) {
    public static PaymentResult ok(String transactionId) {
        return new PaymentResult(true, transactionId, null);
    }

    public static PaymentResult failed(String message) {
        return new PaymentResult(false, null, message);
    }
}
