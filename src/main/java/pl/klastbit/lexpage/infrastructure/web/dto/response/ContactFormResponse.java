package pl.klastbit.lexpage.infrastructure.web.dto.response;

import pl.klastbit.lexpage.application.contact.result.ContactFormResult;

/**
 * Response DTO for successful contact form submission.
 */
public record ContactFormResponse(
    Long messageId,
    String fullName,
    String email,
    String message
) {
    public static ContactFormResponse fromResult(ContactFormResult result) {
        return new ContactFormResponse(
            result.messageId(),
            result.fullName(),
            result.email(),
            result.message()
        );
    }
}
