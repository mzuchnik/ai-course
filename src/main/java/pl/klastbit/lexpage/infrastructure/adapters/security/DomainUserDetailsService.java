package pl.klastbit.lexpage.infrastructure.adapters.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;

/**
 * Spring Security UserDetailsService implementation using domain repository.
 * Loads user from database and converts to UserPrincipal for authentication.
 * Security adapter in Hexagonal Architecture.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username (email): {}", username);

        try {
            // Username is email in our system
            Email email = Email.of(username);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found with email: {}", username);
                        throw new UsernameNotFoundException("User not found with email: " + username);
                    });

            log.debug("User loaded successfully: {}", username);

            return UserPrincipal.from(user);

        } catch (IllegalArgumentException e) {
            // Invalid email format
            log.warn("Invalid email format during authentication: {}", username);
            throw new UsernameNotFoundException("Invalid email format: " + username);
        }
    }
}
