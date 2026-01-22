package pl.klastbit.lexpage.application.contact.command;

import pl.klastbit.lexpage.domain.contact.MessageCategory;

/**
 * Command to submit a contact form.
 * Java Record for immutability (follows DDD command pattern).
 */
public record SubmitContactFormCommand(
    String firstName,
    String lastName,
    String email,
    String phone,
    MessageCategory category,
    String message,
    String ipAddress,
    String userAgent
) {
    public SubmitContactFormCommand {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }
    }
}
