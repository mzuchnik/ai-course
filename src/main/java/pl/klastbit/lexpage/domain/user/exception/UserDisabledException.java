package pl.klastbit.lexpage.domain.user.exception;

import pl.klastbit.lexpage.domain.user.Email;

/**
 * Domain exception thrown when attempting to authenticate with a disabled account.
 */
public class UserDisabledException extends RuntimeException {

    public UserDisabledException(Email email) {
        super("User account is disabled: " + email.value());
    }

    public UserDisabledException(String message) {
        super(message);
    }
}
