package pl.klastbit.lexpage.domain.user.exception;

/**
 * Domain exception thrown when authentication fails due to invalid credentials.
 * Does not specify whether email or password was incorrect (security best practice).
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
