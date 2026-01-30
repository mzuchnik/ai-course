package pl.klastbit.lexpage.application.article.exception;

/**
 * Exception thrown when AI article generation fails.
 * This can happen due to:
 * - AI API unavailability
 * - Invalid AI response format
 * - Network errors
 * - Rate limiting on AI API
 */
public class AIGenerationException extends RuntimeException {
    public AIGenerationException(String message) {
        super(message);
    }

    public AIGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
