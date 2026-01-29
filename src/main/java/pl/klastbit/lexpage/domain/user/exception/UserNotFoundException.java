package pl.klastbit.lexpage.domain.user.exception;

import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.UserId;

/**
 * Domain exception thrown when a user is not found by email or id.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Email email) {
        super("User not found with email: " + email.value());
    }

    public UserNotFoundException(UserId userId) {
        super("User not found with id: " + userId.userid());
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
