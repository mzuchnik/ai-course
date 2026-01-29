package pl.klastbit.lexpage.infrastructure.adapters.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.klastbit.lexpage.application.user.ports.PasswordEncoder;

/**
 * Adapter implementing PasswordEncoder port using BCrypt.
 * Delegates to Spring Security's BCryptPasswordEncoder.
 * Outbound adapter in Hexagonal Architecture.
 */
@Component
@RequiredArgsConstructor
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
