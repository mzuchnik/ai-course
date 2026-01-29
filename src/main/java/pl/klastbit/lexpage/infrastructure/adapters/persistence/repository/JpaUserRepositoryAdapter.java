package pl.klastbit.lexpage.infrastructure.adapters.persistence.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper.UserMapper;

import java.util.Optional;

/**
 * JPA adapter implementing UserRepository port.
 * Adapts Spring Data JPA repository to domain repository interface.
 * Outbound adapter in Hexagonal Architecture.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataRepository;

    @Override
    public Optional<User> findByEmail(Email email) {
        log.debug("Finding user by email: {}", email.value());

        return springDataRepository.findByEmail(email.value())
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        log.debug("Finding user by id: {}", userId.userid());

        return springDataRepository.findById(userId.userid())
                .map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        log.debug("Saving user with email: {}", user.getEmailValue());

        // Check if user already exists (update scenario)
        Optional<UserEntity> existingEntity = springDataRepository.findById(user.getUserId().userid());

        UserEntity entityToSave;
        if (existingEntity.isPresent()) {
            // Update existing entity
            entityToSave = existingEntity.get();
            UserMapper.updateEntity(entityToSave, user);
            log.debug("Updating existing user: {}", user.getUserId().userid());
        } else {
            // Create new entity
            entityToSave = UserMapper.toEntity(user);
            log.debug("Creating new user: {}", user.getUserId().userid());
        }

        UserEntity savedEntity = springDataRepository.save(entityToSave);
        log.info("User saved successfully: {}", savedEntity.getId());

        return UserMapper.toDomain(savedEntity);
    }
}
