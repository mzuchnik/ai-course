package pl.klastbit.lexpage.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.user.ports.PasswordEncoder;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;

/**
 * Initializer for creating test admin user after Spring context is ready.
 * Only active when 'test' profile is enabled.
 * <p>
 * Listens to ApplicationReadyEvent and creates a test admin user if it doesn't exist.
 * This approach is preferred over Liquibase migrations for test data.
 */
@Component
@Profile("test")
@RequiredArgsConstructor
@Slf4j
public class TestUserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TestUserProperties testUserProperties;

    /**
     * Creates or updates test admin user after application context is fully initialized.
     * Triggered by ApplicationReadyEvent.
     * <p>
     * Test user credentials are loaded from application-test.properties:
     * - app.test.user.email
     * - app.test.user.password
     * - app.test.user.username
     * <p>
     * If user already exists, the password will be updated to ensure consistency.
     * <p>
     * IMPORTANT: This is for testing purposes only! Change password in production.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeTestUser() {
        log.info("Initializing test user (profile: test)...");

        Email testEmail = Email.of(testUserProperties.getEmail());
        String rawPassword = testUserProperties.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Check if test user already exists
        var existingUser = userRepository.findByEmail(testEmail);

        if (existingUser.isPresent()) {
            log.info("Test admin user already exists, updating password to ensure consistency");

            // Update existing user with new password
            User updatedUser = User.ofExisting(
                    existingUser.get().getUserId(),
                    testUserProperties.getUsername(),
                    testEmail,
                    encodedPassword,
                    true
            );

            userRepository.save(updatedUser);
            log.info("Test admin user password updated successfully!");
        } else {
            // Create new test admin user
            User testUser = User.ofNew(
                    testUserProperties.getUsername(),
                    testEmail,
                    encodedPassword
            );

            userRepository.save(testUser);
            log.info("Test admin user created successfully!");
        }

        log.info("  Email: {}", testUserProperties.getEmail());
        log.info("  Password: {}", testUserProperties.getPassword());
        log.warn("IMPORTANT: This is a test user. Do NOT use in production!");
    }
}
