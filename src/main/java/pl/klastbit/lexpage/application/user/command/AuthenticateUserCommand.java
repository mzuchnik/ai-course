package pl.klastbit.lexpage.application.user.command;

import java.util.Objects;

/**
 * Command for user authentication use case.
 * Contains email and raw password for authentication.
 * Immutable Record with validation in compact constructor.
 */
public record AuthenticateUserCommand(
        String email,
        String rawPassword
) {

    public AuthenticateUserCommand {
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(rawPassword, "Password cannot be null");

        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
    }
}
