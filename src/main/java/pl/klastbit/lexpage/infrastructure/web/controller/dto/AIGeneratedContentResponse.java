package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;

/**
 * Response DTO for AI-generated article content.
 */
public record AIGeneratedContentResponse(
    String title,
    String content
) {
    public static AIGeneratedContentResponse from(AIGeneratedContentDto dto) {
        return new AIGeneratedContentResponse(dto.title(), dto.content());
    }
}
