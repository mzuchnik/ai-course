package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import lombok.extern.slf4j.Slf4j;
import pl.klastbit.lexpage.domain.contact.ContactMessage;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ContactMessageEntity;

/**
 * Mapper between ContactMessage domain entity and ContactMessageEntity persistence entity.
 * Part of the infrastructure layer (Hexagonal Architecture outbound adapter).
 */
@Slf4j
public class ContactMessageMapper {

    /**
     * Maps ContactMessageEntity (JPA) to ContactMessage (domain).
     *
     * @param entity JPA entity from database
     * @return Domain entity
     */
    public ContactMessage toDomain(ContactMessageEntity entity) {
        if (entity == null) {
            return null;
        }

        return ContactMessage.ofExisting(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getCategory(),
            entity.getMessage(),
            entity.getStatus(),
            entity.getRecaptchaScore(),
            entity.getIpAddress(),
            entity.getUserAgent(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Maps ContactMessage (domain) to ContactMessageEntity (JPA).
     *
     * @param domain Domain entity
     * @return JPA entity for database persistence
     */
    public ContactMessageEntity toEntity(ContactMessage domain) {
        if (domain == null) {
            return null;
        }

        ContactMessageEntity entity = new ContactMessageEntity();
        entity.setId(domain.getId());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setCategory(domain.getCategory());
        entity.setMessage(domain.getMessage());
        entity.setStatus(domain.getStatus());
        entity.setRecaptchaScore(domain.getRecaptchaScore());
        entity.setIpAddress(domain.getIpAddress());
        entity.setUserAgent(domain.getUserAgent());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }

    /**
     * Updates an existing ContactMessageEntity with data from ContactMessage domain entity.
     *
     * @param entity Existing JPA entity to update
     * @param domain Domain entity with new data
     */
    public void updateEntity(ContactMessageEntity entity, ContactMessage domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setCategory(domain.getCategory());
        entity.setMessage(domain.getMessage());
        entity.setStatus(domain.getStatus());
        entity.setRecaptchaScore(domain.getRecaptchaScore());
        entity.setIpAddress(domain.getIpAddress());
        entity.setUserAgent(domain.getUserAgent());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }

}
