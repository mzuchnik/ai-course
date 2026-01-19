package pl.klastbit.lexpage.infrastructure.adapters.persistence.entity;

/**
 * AI generation status enum matching database generation_status_enum type.
 */
public enum GenerationStatus {
    SUCCESS,
    FAILED,
    TIMEOUT
}
