package pl.klastbit.lexpage.infrastructure.adapters.persistence.entity;

/**
 * Message status enum matching database message_status_enum type.
 */
public enum MessageStatus {
    NEW,
    READ,
    REPLIED,
    ARCHIVED
}
