package pl.klastbit.lexpage.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.klastbit.lexpage.infrastructure.adapters.security.DomainUserDetailsService;

/**
 * Spring Security configuration.
 * Configures authentication, authorization, form login, logout, and security headers.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final DomainUserDetailsService userDetailsService;

    /**
     * BCryptPasswordEncoder bean with strength 12.
     * Used for password hashing and verification.
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Security filter chain configuration.
     * Configures HTTP security, authorization, form login, logout, CSRF, and session management.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Authorization configuration
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").authenticated()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )

                .userDetailsService(userDetailsService)

                // Form login configuration
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // CSRF protection
                // Enabled for web forms (/admin/**, /login) - token automatically added to Thymeleaf forms
                // Disabled for REST API (/api/**) - stateless endpoints don't need CSRF
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )

                // Session management
                .sessionManagement(session -> session
                        .maximumSessions(1) // Max 1 session per user
                        .maxSessionsPreventsLogin(false) // Allow new session, invalidate old one
                );

        return http.build();
    }

    /**
     * Authentication manager bean.
     * Used for programmatic authentication if needed.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
