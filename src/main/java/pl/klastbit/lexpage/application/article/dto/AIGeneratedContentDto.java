package pl.klastbit.lexpage.application.article.dto;

/**
 * DTO containing AI-generated article content.
 * Content is in HTML format (converted from Markdown by the infrastructure layer).
 */
public record AIGeneratedContentDto(
    String title,
    String content  // HTML content (converted from Markdown)
) {
    public static AIGeneratedContentDto of(String title, String content) {
        return new AIGeneratedContentDto(title, content);
    }
}
