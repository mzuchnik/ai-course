package pl.klastbit.lexpage.application.contact.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.contact.command.SubmitContactFormCommand;
import pl.klastbit.lexpage.application.contact.result.ContactFormResult;
import pl.klastbit.lexpage.domain.contact.ContactMessage;
import pl.klastbit.lexpage.domain.contact.ContactRepository;
import pl.klastbit.lexpage.domain.contact.exception.RateLimitExceededException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Application service for contact form use case.
 * Orchestrates domain logic, validation, and persistence.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContactFormApplicationService {

    private final ContactRepository contactRepository;

    private static final int MAX_MESSAGES_PER_HOUR = 3;
    private static final int RATE_LIMIT_HOURS = 1;

    /**
     * Submits contact form with validation and rate limiting.
     * Throws exceptions on validation or rate limit errors.
     */
    @Transactional
    public ContactFormResult submitContactForm(SubmitContactFormCommand command) {
        log.info("Processing contact form submission from: {} {}",
            command.firstName(), command.lastName());

        // 1. Check rate limit (throws RateLimitExceededException if exceeded)
        checkRateLimit(command.ipAddress());

        // 2. Create domain entity (throws IllegalArgumentException on validation errors)
        // Note: Using default reCAPTCHA score 0.9 for MVP (no verification yet)
        ContactMessage contactMessage = ContactMessage.create(
            command.firstName(),
            command.lastName(),
            command.email(),
            command.phone(),
            command.category(),
            command.message(),
            new BigDecimal("0.9"), // Default score for MVP
            command.ipAddress(),
            command.userAgent()
        );

        // 3. Persist to database
        ContactMessage savedMessage = contactRepository.save(contactMessage);
        log.info("Contact message saved with ID: {}", savedMessage.getId());

        // 4. TODO: Send email notification (deferred to future iteration)

        return ContactFormResult.success(
            savedMessage.getId(),
            savedMessage.getFullName(),
            savedMessage.getEmail()
        );
    }

    /**
     * Checks if IP address has exceeded rate limit.
     */
    private void checkRateLimit(String ipAddress) {
        if (ipAddress == null) {
            return; // Skip rate limiting if IP not available
        }

        LocalDateTime since = LocalDateTime.now().minusHours(RATE_LIMIT_HOURS);
        int messageCount = contactRepository.countByIpAddressAndCreatedAtAfter(ipAddress, since);

        if (messageCount >= MAX_MESSAGES_PER_HOUR) {
            log.warn("Rate limit exceeded for IP: {}", ipAddress);
            throw new RateLimitExceededException(MAX_MESSAGES_PER_HOUR, RATE_LIMIT_HOURS);
        }
    }
}
