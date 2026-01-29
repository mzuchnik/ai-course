package pl.klastbit.lexpage.application.user;

import pl.klastbit.lexpage.application.user.command.AuthenticateUserCommand;
import pl.klastbit.lexpage.application.user.result.AuthenticationResult;

/**
 * Use case interface for user authentication.
 * Defines the contract for authenticating users with email and password.
 */
public interface AuthenticateUserUseCase {

    /**
     * Authenticates a user with provided credentials.
     *
     * @param command the authentication command containing email and password
     * @return authentication result with user data
     * @throws pl.klastbit.lexpage.domain.user.exception.UserNotFoundException      if user not found
     * @throws pl.klastbit.lexpage.domain.user.exception.UserDisabledException      if account is disabled
     * @throws pl.klastbit.lexpage.domain.user.exception.InvalidCredentialsException if password is incorrect
     */
    AuthenticationResult execute(AuthenticateUserCommand command);
}
