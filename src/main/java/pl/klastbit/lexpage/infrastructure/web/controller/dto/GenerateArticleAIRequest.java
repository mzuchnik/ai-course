package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for AI article generation.
 */
public record GenerateArticleAIRequest(
    @NotBlank(message = "Prompt jest wymagany")
    @Size(max = 1000, message = "Prompt nie może przekraczać 1000 znaków")
    String userPrompt
) {}
