package pl.klastbit.lexpage.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;
import pl.klastbit.lexpage.domain.contact.MessageCategory;

/**
 * Request DTO for contact form submission.
 * Uses Bean Validation annotations for input validation.
 */
public record SubmitContactFormRequest(

    @NotBlank(message = "Imię jest wymagane")
    @Size(min = 2, max = 100, message = "Imię musi mieć od 2 do 100 znaków")
    String firstName,

    @NotBlank(message = "Nazwisko jest wymagane")
    @Size(min = 2, max = 100, message = "Nazwisko musi mieć od 2 do 100 znaków")
    String lastName,

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Nieprawidłowy format email")
    @Size(max = 255, message = "Email nie może przekraczać 255 znaków")
    String email,

    @Size(max = 20, message = "Numer telefonu nie może przekraczać 20 znaków")
    String phone,

    @NotNull(message = "Kategoria jest wymagana")
    MessageCategory category,

    @NotBlank(message = "Wiadomość jest wymagana")
    @Size(min = 50, max = 5000, message = "Wiadomość musi mieć od 50 do 5000 znaków")
    String message
) {}
