package pl.klastbit.lexpage.infrastructure.adapters.persistence.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.klastbit.lexpage.domain.contact.ContactMessage;
import pl.klastbit.lexpage.domain.contact.ContactRepository;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ContactMessageEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper.ContactMessageMapper;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Adapter implementation of ContactRepository port.
 * Bridges domain layer with JPA infrastructure.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContactRepositoryAdapter implements ContactRepository {

    private final JpaContactMessageRepository jpaRepository;
    private final ContactMessageMapper mapper;

    @Override
    public ContactMessage save(ContactMessage contactMessage) {
        ContactMessageEntity entity = mapper.toEntity(contactMessage);
        ContactMessageEntity savedEntity = jpaRepository.save(entity);
        log.debug("Saved ContactMessageEntity with ID: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ContactMessage> findById(Long id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public int countByIpAddressAndCreatedAtAfter(String ipAddress, LocalDateTime since) {
        return jpaRepository.countByIpAddressAndCreatedAtAfter(ipAddress, since);
    }
}
