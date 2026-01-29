package pl.klastbit.lexpage.application.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.user.AuthenticateUserUseCase;
import pl.klastbit.lexpage.application.user.command.AuthenticateUserCommand;
import pl.klastbit.lexpage.application.user.ports.PasswordEncoder;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.application.user.result.AuthenticationResult;
import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.exception.InvalidCredentialsException;
import pl.klastbit.lexpage.domain.user.exception.UserDisabledException;
import pl.klastbit.lexpage.domain.user.exception.UserNotFoundException;

/**
 * Application service implementing user authentication use case.
 * Orchestrates authentication logic using domain objects and ports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserAuthenticationService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthenticationResult execute(AuthenticateUserCommand command) {
        log.debug("Authenticating user with email: {}", command.email());

        // 1. Create Email value object (validates format)
        Email email = Email.of(command.email());

        // 2. Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Authentication failed: User not found with email: {}", email.value());
                    throw new UserNotFoundException(email);
                });

        // 3. Check if account is enabled
        if (!user.isEnabled()) {
            log.warn("Authentication failed: Account disabled for email: {}", email.value());
            throw new UserDisabledException(email);
        }

        // 4. Verify password
        if (!passwordEncoder.matches(command.rawPassword(), user.getPasswordHash())) {
            log.warn("Authentication failed: Invalid password for email: {}", email.value());
            throw new InvalidCredentialsException();
        }

        log.info("User authenticated successfully: {}", email.value());

        // 5. Return authentication result
        return AuthenticationResult.from(user);
    }
}
