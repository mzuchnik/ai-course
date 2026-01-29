package pl.klastbit.lexpage.application.user.result;

import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Objects;

/**
 * Result of user authentication use case.
 * Contains user data needed to create UserPrincipal in security context.
 * Immutable Record with factory method from User domain object.
 */
public record AuthenticationResult(
        UserId userId,
        String email,
        String username,
        boolean enabled
) {

    public AuthenticationResult {
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(username, "Username cannot be null");
    }

    /**
     * Factory method to create AuthenticationResult from User domain object.
     *
     * @param user the authenticated user
     * @return authentication result with user data
     */
    public static AuthenticationResult from(User user) {
        return new AuthenticationResult(
                user.getUserId(),
                user.getEmailValue(),
                user.getUsername(),
                user.isEnabled()
        );
    }
}
