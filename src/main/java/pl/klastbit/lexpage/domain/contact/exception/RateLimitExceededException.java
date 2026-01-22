package pl.klastbit.lexpage.domain.contact.exception;

import lombok.Getter;

/**
 * Domain exception thrown when rate limit is exceeded.
 */
@Getter
public class RateLimitExceededException extends RuntimeException {

    private final int maxAllowed;
    private final int periodInHours;

    public RateLimitExceededException(int maxAllowed, int periodInHours) {
        super(String.format("Rate limit exceeded. Maximum %d messages allowed per %d hour(s)",
            maxAllowed, periodInHours));
        this.maxAllowed = maxAllowed;
        this.periodInHours = periodInHours;
    }
}
