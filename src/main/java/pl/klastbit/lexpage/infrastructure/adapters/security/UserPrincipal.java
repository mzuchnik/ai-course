package pl.klastbit.lexpage.infrastructure.adapters.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Collection;
import java.util.Collections;

/**
 * UserPrincipal implementing Spring Security UserDetails.
 * Lightweight object stored in Security Context.
 * Contains minimal information needed for authentication and authorization.
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final UserId userId;
    private final String email;
    private final String displayName;
    private final String passwordHash;
    private final boolean enabled;
    private final Collection<GrantedAuthority> authorities;

    private UserPrincipal(
            UserId userId,
            String email,
            String displayName,
            String passwordHash,
            boolean enabled,
            Collection<GrantedAuthority> authorities
    ) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    /**
     * Factory method to create UserPrincipal from User domain object.
     *
     * @param user the domain user
     * @return UserPrincipal for Spring Security context
     */
    public static UserPrincipal from(User user) {
        // For now, all authenticated users have ROLE_USER
        // In the future, this can be extended with roles from database
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new UserPrincipal(
                user.getUserId(),
                user.getEmailValue(),
                user.getUsername(),
                user.getPasswordHash(),
                user.isEnabled(),
                authorities
        );
    }

    // UserDetails interface methods

    @Override
    public String getUsername() {
        // Spring Security uses this as the principal identifier
        return email;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // No expiration logic for now
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // No locking logic for now
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // No credential expiration for now
    }

    // Custom getters for domain-specific data

    public UserId getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
