package pl.klastbit.lexpage.domain.article.exception;

/**
 * Exception thrown when an invalid article status transition is attempted.
 * Domain exception in the article bounded context.
 */
public class InvalidArticleStatusTransitionException extends RuntimeException {

    public InvalidArticleStatusTransitionException(String message) {
        super(message);
    }
}
