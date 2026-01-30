package pl.klastbit.lexpage.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pl.klastbit.lexpage.application.article.exception.AIGenerationException;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;
import pl.klastbit.lexpage.domain.contact.exception.RateLimitExceededException;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API endpoints.
 * Uses ProblemDetail (RFC 7807) for standardized error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionApiHandler {

    /**
     * Handles rate limit exceeded exceptions.
     * Returns 429 Too Many Requests.
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ProblemDetail handleRateLimitExceeded(RateLimitExceededException ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.TOO_MANY_REQUESTS,
                "Osiągnięto limit wiadomości. Spróbuj ponownie za godzinę."
        );

        problemDetail.setTitle("Rate Limit Exceeded");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/rate-limit-exceeded"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("maxAllowed", ex.getMaxAllowed());
        problemDetail.setProperty("periodInHours", ex.getPeriodInHours());

        return problemDetail;
    }

    /**
     * Handles Bean Validation errors (e.g., @NotBlank, @Email, @Size).
     * Returns 400 Bad Request with detailed field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );

        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/validation-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("fieldErrors", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .collect(Collectors.toList())
        );

        return problemDetail;
    }

    /**
     * Handles article not found exceptions.
     * Returns 404 Not Found.
     */
    @ExceptionHandler(ArticleNotFoundException.class)
    public ProblemDetail handleArticleNotFound(ArticleNotFoundException ex) {
        log.warn("Article not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problemDetail.setTitle("Article Not Found");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/article-not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("articleId", ex.getArticleId());

        return problemDetail;
    }

    /**
     * Handles AI generation failures.
     * Returns 503 Service Unavailable.
     */
    @ExceptionHandler(AIGenerationException.class)
    public ProblemDetail handleAIGenerationException(AIGenerationException ex) {
        log.warn("AI generation failed: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Nie udało się wygenerować artykułu przez AI. Spróbuj ponownie."
        );

        problemDetail.setTitle("AI Generation Failed");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/ai-generation-failed"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles invalid article status transition exceptions.
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        log.warn("Invalid state: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problemDetail.setTitle("Invalid Article Status Transition");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/invalid-status-transition"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles domain validation errors (IllegalArgumentException).
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problemDetail.setTitle("Invalid Request");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/invalid-request"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFoundException(NoResourceFoundException ex) {
        log.info(ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles Spring Security authentication exceptions (for API endpoints).
     * Returns 401 Unauthorized.
     * Does not reveal whether user exists (security best practice).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed. Please check your credentials."
        );

        problemDetail.setTitle("Unauthorized");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/unauthorized"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles Spring Security access denied exceptions (for API endpoints).
     * Returns 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Access denied. You do not have permission to access this resource."
        );

        problemDetail.setTitle("Forbidden");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/forbidden"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles username not found exceptions (for API endpoints).
     * Returns 401 Unauthorized.
     * Does not reveal whether user exists (security best practice).
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("User not found during authentication: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed. Please check your credentials."
        );

        problemDetail.setTitle("Unauthorized");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/unauthorized"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles all other unexpected exceptions.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Wystąpił nieoczekiwany błąd. Spróbuj ponownie lub zadzwoń."
        );

        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Record for field error details in validation responses.
     */
    private record FieldError(
            String field,
            String message,
            Object rejectedValue
    ) {
    }
}
