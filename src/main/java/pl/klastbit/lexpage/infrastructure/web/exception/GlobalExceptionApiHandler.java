package pl.klastbit.lexpage.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
    ) {}
}
