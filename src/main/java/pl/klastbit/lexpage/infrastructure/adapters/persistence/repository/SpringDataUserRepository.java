package pl.klastbit.lexpage.infrastructure.adapters.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for UserEntity.
 * Infrastructure layer in Hexagonal Architecture.
 */
public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
