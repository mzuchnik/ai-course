package pl.klastbit.lexpage.application.user.ports;

import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Optional;

/**
 * Outbound port for User repository operations.
 * Interface defines operations on user aggregates.
 * Implementation in infrastructure layer (JPA adapter).
 */
public interface UserRepository {

    /**
     * Finds a user by email address.
     *
     * @param email the email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(Email email);

    /**
     * Finds a user by user ID.
     *
     * @param userId the user ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(UserId userId);

    /**
     * Saves a user (create or update).
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);
}
