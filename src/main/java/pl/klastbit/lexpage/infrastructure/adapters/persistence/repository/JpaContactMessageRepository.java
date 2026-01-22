package pl.klastbit.lexpage.infrastructure.adapters.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ContactMessageEntity;

import java.time.LocalDateTime;

/**
 * Spring Data JPA repository for ContactMessageEntity.
 * Infrastructure concern - not exposed to domain.
 */
@Repository
public interface JpaContactMessageRepository extends JpaRepository<ContactMessageEntity, Long> {

    /**
     * Counts messages from specific IP address after a given timestamp.
     * Used for rate limiting.
     */
    @Query("SELECT COUNT(c) FROM ContactMessageEntity c " +
           "WHERE c.ipAddress = :ipAddress " +
           "AND c.createdAt > :since")
    int countByIpAddressAndCreatedAtAfter(
        @Param("ipAddress") String ipAddress,
        @Param("since") LocalDateTime since
    );
}
