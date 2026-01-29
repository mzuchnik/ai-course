package pl.klastbit.lexpage.domain.user;

import lombok.Getter;

import java.util.Objects;

/**
 * User Aggregate Root
 * Represents a user in the system with authentication details.
 * Immutable domain entity with factory methods for creation.
 */
@Getter
public class User {

    private final UserId userId;
    private final String username;
    private final Email email;
    private final String passwordHash;
    private final boolean enabled;

    private User(UserId userId, String username, Email email, String passwordHash, boolean enabled) {
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "PasswordHash cannot be null");
        this.enabled = enabled;
    }

    /**
     * Factory method for creating a new user (not yet persisted).
     * Generates a new UserId.
     */
    public static User ofNew(String username, Email email, String passwordHash) {
        return new User(UserId.createNew(), username, email, passwordHash, true);
    }

    /**
     * Factory method for creating a user from existing data (from database).
     * Used by infrastructure layer when loading users.
     */
    public static User ofExisting(UserId userId, String username, Email email, String passwordHash, boolean enabled) {
        return new User(userId, username, email, passwordHash, enabled);
    }

    /**
     * Returns the email value as String for convenience.
     */
    public String getEmailValue() {
        return email.value();
    }
}
