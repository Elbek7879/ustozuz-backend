package uz.ustozuz.backend.contact;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.contact.dto.ContactMessageResponse;
import uz.ustozuz.backend.contact.dto.ContactRequest;

@RestController
@RequiredArgsConstructor
public class ContactController {

    private final ContactMessageRepository contactMessageRepository;

    // Ochiq: saytdagi "Aloqa" formasi
    @PostMapping("/api/contact")
    public void send(@Valid @RequestBody ContactRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.name().trim());
        message.setEmail(request.email().trim());
        message.setMessage(request.message().trim());
        contactMessageRepository.save(message);
    }

    // Faqat admin (SecurityConfig: /api/admin/**)
    @GetMapping("/api/admin/messages")
    @Transactional(readOnly = true)
    public List<ContactMessageResponse> latest() {
        return contactMessageRepository.findTop20ByOrderByCreatedAtDesc().stream()
                .map(m -> new ContactMessageResponse(m.getId(), m.getName(), m.getEmail(), m.getMessage(), m.getCreatedAt()))
                .toList();
    }
}
