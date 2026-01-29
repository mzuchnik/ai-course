package pl.klastbit.lexpage.infrastructure.adapters.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@DisplayName("BCryptPasswordEncoderAdapter Tests")
class BCryptPasswordEncoderAdapterTest {

    private BCryptPasswordEncoderAdapter adapter;
    private String rawPassword;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
        adapter = new BCryptPasswordEncoderAdapter(bCryptPasswordEncoder);
        rawPassword = "password123";
    }

    @Test
    @DisplayName("Should encode password successfully")
    void shouldEncodePasswordSuccessfully() {
        // Act
        String encoded = adapter.encode(rawPassword);

        // Assert
        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEmpty();
        assertThat(encoded).startsWith("$2a$12$"); // BCrypt format with strength 12
        assertThat(encoded).hasSize(60); // BCrypt hash is always 60 characters
    }

    @Test
    @DisplayName("Should generate different hashes for same password")
    void shouldGenerateDifferentHashesForSamePassword() {
        // Act
        String encoded1 = adapter.encode(rawPassword);
        String encoded2 = adapter.encode(rawPassword);

        // Assert
        assertThat(encoded1).isNotEqualTo(encoded2); // BCrypt uses salt, so hashes differ
    }

    @Test
    @DisplayName("Should match password with encoded hash")
    void shouldMatchPasswordWithEncodedHash() {
        // Arrange
        String encoded = adapter.encode(rawPassword);

        // Act
        boolean matches = adapter.matches(rawPassword, encoded);

        // Assert
        assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("Should not match incorrect password")
    void shouldNotMatchIncorrectPassword() {
        // Arrange
        String encoded = adapter.encode(rawPassword);
        String wrongPassword = "wrongpassword";

        // Act
        boolean matches = adapter.matches(wrongPassword, encoded);

        // Assert
        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("Should verify BCrypt hash format")
    void shouldVerifyBCryptHashFormat() {
        // Arrange
        String encoded = adapter.encode("testpassword");

        // Act & Assert
        assertThat(encoded).startsWith("$2a$12$");
        assertThat(encoded).hasSize(60);

        // Verify we can match the encoded password
        assertThat(adapter.matches("testpassword", encoded)).isTrue();
        assertThat(adapter.matches("wrongpassword", encoded)).isFalse();
    }
}
