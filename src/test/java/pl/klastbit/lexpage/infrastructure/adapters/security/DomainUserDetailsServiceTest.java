package pl.klastbit.lexpage.infrastructure.adapters.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DomainUserDetailsService Tests")
class DomainUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DomainUserDetailsService userDetailsService;

    private User testUser;
    private Email testEmail;

    @BeforeEach
    void setUp() {
        testEmail = Email.of("test@example.com");

        testUser = User.ofExisting(
                UserId.of(UUID.randomUUID()),
                "testuser",
                testEmail,
                "$2a$12$hashedPassword",
                true
        );
    }

    @Test
    @DisplayName("Should load user by username (email) successfully")
    void shouldLoadUserByUsernameSuccessfully() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail.value());

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(UserPrincipal.class);

        UserPrincipal principal = (UserPrincipal) userDetails;
        assertThat(principal.getUsername()).isEqualTo(testEmail.value());
        assertThat(principal.getDisplayName()).isEqualTo("testuser");
        assertThat(principal.isEnabled()).isTrue();
        assertThat(principal.getPassword()).isEqualTo("$2a$12$hashedPassword");

        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(email);

        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when email format is invalid")
    void shouldThrowUsernameNotFoundExceptionWhenEmailFormatInvalid() {
        // Arrange
        String invalidEmail = "not-an-email";

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(invalidEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Invalid email format");

        verify(userRepository, never()).findByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Should return UserPrincipal with correct authorities")
    void shouldReturnUserPrincipalWithAuthorities() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail.value());

        // Assert
        assertThat(userDetails.getAuthorities()).isNotEmpty();
        assertThat(userDetails.getAuthorities())
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
    }
}
