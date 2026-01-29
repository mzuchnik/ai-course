package pl.klastbit.lexpage.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.klastbit.lexpage.application.user.ports.PasswordEncoder;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test for TestUserInitializer.
 * Verifies that test admin user is created/updated when application starts with 'test' profile.
 * <p>
 * Note: TestUserInitializer updates the user if it already exists, ensuring consistent credentials.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TestUserInitializer Integration Tests")
class TestUserInitializerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestUserProperties testUserProperties;

    @Test
    @DisplayName("Should create test admin user on application startup")
    void shouldCreateTestAdminUserOnStartup() {
        // Arrange
        Email testEmail = Email.of(testUserProperties.getEmail());

        // Act
        Optional<User> user = userRepository.findByEmail(testEmail);

        // Assert
        assertThat(user).isPresent();

        User testUser = user.get();
        assertThat(testUser.getEmailValue()).isEqualTo(testUserProperties.getEmail());
        assertThat(testUser.getUsername()).isEqualTo(testUserProperties.getUsername());
        assertThat(testUser.isEnabled()).isTrue();
        assertThat(testUser.getPasswordHash()).isNotBlank();

        // Verify password is correctly encoded
        boolean passwordMatches = passwordEncoder.matches(testUserProperties.getPassword(), testUser.getPasswordHash());
        assertThat(passwordMatches).isTrue();
    }

    @Test
    @DisplayName("Should be able to authenticate with test user credentials")
    void shouldAuthenticateWithTestUserCredentials() {
        // Arrange
        Email testEmail = Email.of(testUserProperties.getEmail());
        String testPassword = testUserProperties.getPassword();

        // Act
        Optional<User> user = userRepository.findByEmail(testEmail);

        // Assert
        assertThat(user).isPresent();
        User testUser = user.get();

        // Verify password authentication works
        boolean authenticated = passwordEncoder.matches(testPassword, testUser.getPasswordHash());
        assertThat(authenticated)
                .as("Test user should authenticate with password from configuration")
                .isTrue();
    }
}
