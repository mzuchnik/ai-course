package pl.klastbit.lexpage.application.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.klastbit.lexpage.application.user.command.AuthenticateUserCommand;
import pl.klastbit.lexpage.application.user.ports.PasswordEncoder;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.application.user.result.AuthenticationResult;
import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.domain.user.exception.InvalidCredentialsException;
import pl.klastbit.lexpage.domain.user.exception.UserDisabledException;
import pl.klastbit.lexpage.domain.user.exception.UserNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthenticationService Tests")
class UserAuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAuthenticationService authenticationService;

    private User testUser;
    private Email testEmail;
    private String rawPassword;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        testEmail = Email.of("test@example.com");
        rawPassword = "password123";
        encodedPassword = "$2a$12$hashedPassword";

        testUser = User.ofExisting(
                UserId.of(UUID.randomUUID()),
                "testuser",
                testEmail,
                encodedPassword,
                true
        );
    }

    @Test
    @DisplayName("Should successfully authenticate user with valid credentials")
    void shouldAuthenticateUserSuccessfully() {
        // Arrange
        AuthenticateUserCommand command = new AuthenticateUserCommand(
                testEmail.value(),
                rawPassword
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // Act
        AuthenticationResult result = authenticationService.execute(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(testUser.getUserId());
        assertThat(result.email()).isEqualTo(testEmail.value());
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.enabled()).isTrue();

        verify(userRepository).findByEmail(any(Email.class));
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        // Arrange
        AuthenticateUserCommand command = new AuthenticateUserCommand(
                "nonexistent@example.com",
                rawPassword
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.execute(command))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("nonexistent@example.com");

        verify(userRepository).findByEmail(any(Email.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw UserDisabledException when user account is disabled")
    void shouldThrowUserDisabledExceptionWhenAccountDisabled() {
        // Arrange
        User disabledUser = User.ofExisting(
                UserId.of(UUID.randomUUID()),
                "disableduser",
                testEmail,
                encodedPassword,
                false // disabled
        );

        AuthenticateUserCommand command = new AuthenticateUserCommand(
                testEmail.value(),
                rawPassword
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(disabledUser));

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.execute(command))
                .isInstanceOf(UserDisabledException.class)
                .hasMessageContaining(testEmail.value());

        verify(userRepository).findByEmail(any(Email.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password is incorrect")
    void shouldThrowInvalidCredentialsExceptionWhenPasswordIncorrect() {
        // Arrange
        AuthenticateUserCommand command = new AuthenticateUserCommand(
                testEmail.value(),
                "wrongpassword"
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", encodedPassword)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.execute(command))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");

        verify(userRepository).findByEmail(any(Email.class));
        verify(passwordEncoder).matches("wrongpassword", encodedPassword);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email format is invalid")
    void shouldThrowIllegalArgumentExceptionWhenEmailInvalid() {
        // Arrange
        String invalidEmail = "not-an-email";
        AuthenticateUserCommand command = new AuthenticateUserCommand(invalidEmail, rawPassword);

        // Act & Assert
        // Email validation happens in Email.of() during service execution
        assertThatThrownBy(() -> authenticationService.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");
    }
}
