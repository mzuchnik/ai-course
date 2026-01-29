package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;

/**
 * Mapper for converting between User domain object and UserEntity.
 * Stateless mapper with static methods.
 */
public final class UserMapper {

    private UserMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts UserEntity to User domain object.
     *
     * @param entity the JPA entity
     * @return the domain object
     */
    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.ofExisting(
                UserId.of(entity.getId()),
                entity.getUsername(),
                Email.of(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getEnabled()
        );
    }

    /**
     * Converts User domain object to UserEntity.
     * Used for persisting new or updated users.
     *
     * @param user the domain object
     * @return the JPA entity
     */
    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(user.getUserId().userid());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmailValue());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setEnabled(user.isEnabled());

        return entity;
    }

    /**
     * Updates an existing entity with data from domain object.
     * Preserves entity metadata (timestamps, etc.).
     *
     * @param entity the existing entity to update
     * @param user   the domain object with new data
     */
    public static void updateEntity(UserEntity entity, User user) {
        if (entity == null || user == null) {
            return;
        }

        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmailValue());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setEnabled(user.isEnabled());
    }
}
