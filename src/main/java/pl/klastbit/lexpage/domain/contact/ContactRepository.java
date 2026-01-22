package pl.klastbit.lexpage.domain.contact;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository port interface for ContactMessage aggregate.
 * Part of domain layer - defines contract without implementation details.
 */
public interface ContactRepository {

    /**
     * Saves a new or existing contact message.
     */
    ContactMessage save(ContactMessage contactMessage);

    /**
     * Finds contact message by ID.
     */
    Optional<ContactMessage> findById(Long id);

    /**
     * Counts messages from a specific IP address within a time range.
     * Used for rate limiting (max 3 messages per hour).
     */
    int countByIpAddressAndCreatedAtAfter(String ipAddress, LocalDateTime since);
}
