package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pl.klastbit.lexpage.application.article.command.CreateArticleCommand;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new article.
 * Immutable Record with Bean Validation annotations.
 */
public record CreateArticleRequest(
        @NotBlank(message = "Tytuł jest wymagany")
        @Size(max = 255, message = "Tytuł nie może przekraczać 255 znaków")
        String title,

        @NotBlank(message = "Treść jest wymagana")
        @Size(min = 50, max = 25000, message = "Treść musi mieć od 50 do 25000 znaków")
        String content,

        @Size(max = 500, message = "Excerpt nie może przekraczać 500 znaków")
        String excerpt,

        @Size(max = 60, message = "Meta title nie może przekraczać 60 znaków")
        String metaTitle,

        @Size(max = 160, message = "Meta description nie może przekraczać 160 znaków")
        String metaDescription,

        @Size(max = 500, message = "OG Image URL nie może przekraczać 500 znaków")
        String ogImageUrl,

        @Size(max = 500, message = "Canonical URL nie może przekraczać 500 znaków")
        String canonicalUrl,

        @Size(max = 10, message = "Maksymalnie 10 keywords")
        List<@Size(max = 50, message = "Keyword nie może przekraczać 50 znaków") String> keywords
) {

    /**
     * Converts request DTO to application command.
     *
     * @param userId ID of the authenticated user (from Spring Security context)
     * @return CreateArticleCommand
     */
    public CreateArticleCommand toCommand(UUID userId) {
        UserId userIdObj = UserId.of(userId);
        return new CreateArticleCommand(
                title,
                content,
                excerpt,
                metaTitle,
                metaDescription,
                ogImageUrl,
                canonicalUrl,
                keywords,
                userIdObj,  // authorId
                userIdObj,  // createdBy
                userIdObj   // updatedBy
        );
    }
}
