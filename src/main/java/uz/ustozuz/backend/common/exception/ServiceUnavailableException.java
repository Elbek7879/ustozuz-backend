package uz.ustozuz.backend.common.exception;

// Tashqi xizmat (masalan, email) vaqtincha ishlamayotganda
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
