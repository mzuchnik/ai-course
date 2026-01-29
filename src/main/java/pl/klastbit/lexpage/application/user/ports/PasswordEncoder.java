package pl.klastbit.lexpage.application.user.ports;

/**
 * Outbound port for password encoding operations.
 * Interface abstracts password hashing implementation.
 * Implementation in infrastructure layer (BCrypt).
 */
public interface PasswordEncoder {

    /**
     * Encodes (hashes) a raw password.
     *
     * @param rawPassword the plain text password
     * @return the encoded (hashed) password
     */
    String encode(String rawPassword);

    /**
     * Verifies a raw password against an encoded password.
     *
     * @param rawPassword     the plain text password to verify
     * @param encodedPassword the encoded password to compare against
     * @return true if the passwords match, false otherwise
     */
    boolean matches(String rawPassword, String encodedPassword);
}
