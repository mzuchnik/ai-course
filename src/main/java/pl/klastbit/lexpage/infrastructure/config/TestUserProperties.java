package pl.klastbit.lexpage.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for test user initialization.
 * Loaded from application-test.properties when 'test' profile is active.
 */
@Configuration
@ConfigurationProperties(prefix = "app.test.user")
@Getter
@Setter
public class TestUserProperties {

    /**
     * Test user's username.
     */
    private String username;

    /**
     * Test user's email address.
     */
    private String email;

    /**
     * Test user's plain-text password (will be encoded before storing).
     */
    private String password;
}
