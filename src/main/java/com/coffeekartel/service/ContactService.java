package com.coffeekartel.service;

import com.coffeekartel.dto.request.ContactRequest;
import com.coffeekartel.dto.response.ContactResponse;
import com.coffeekartel.entity.ContactMessage;
import com.coffeekartel.exception.ResourceNotFoundException;
import com.coffeekartel.repository.ContactMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final ContactMessageRepository contactMessageRepository;

    public ContactService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    @Transactional
    public ContactResponse submitMessage(ContactRequest request) {
        ContactMessage message = new ContactMessage(
                request.getName(),
                request.getContactInfo(),
                request.getMessage()
        );

        ContactMessage saved = contactMessageRepository.save(message);
        log.info("Received contact inquiry from {}", saved.getName());
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getAllMessages() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContactResponse markResolved(Long id, boolean resolved) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact inquiry not found with id: " + id));

        message.setResolved(resolved);
        ContactMessage updated = contactMessageRepository.save(message);
        return mapToResponse(updated);
    }

    private ContactResponse mapToResponse(ContactMessage m) {
        return new ContactResponse(
                m.getId(),
                m.getName(),
                m.getContactInfo(),
                m.getMessage(),
                m.isResolved(),
                m.getCreatedAt()
        );
    }
}
