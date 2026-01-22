package pl.klastbit.lexpage.application.contact.result;

/**
 * Result of contact form submission.
 */
public record ContactFormResult(
    Long messageId,
    String fullName,
    String email,
    boolean success,
    String message
) {
    public static ContactFormResult success(Long messageId, String fullName, String email) {
        return new ContactFormResult(
            messageId,
            fullName,
            email,
            true,
            "Dziękujemy! Odpowiemy w ciągu 24h."
        );
    }
}
