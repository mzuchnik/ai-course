package pl.klastbit.lexpage.application.ports;

import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;

/**
 * Outbound port for AI article generation.
 * Infrastructure layer provides the implementation.
 *
 * This port follows the Hexagonal Architecture pattern,
 * allowing the application layer to remain independent of
 * specific AI service implementations.
 */
public interface ArticleAIPort {

    /**
     * Generates article content based on user's prompt.
     *
     * @param userPrompt User's description of what article to generate
     * @return AI-generated title and HTML content
     * @throws pl.klastbit.lexpage.application.article.exception.AIGenerationException if generation fails
     */
    AIGeneratedContentDto generateArticleContent(String userPrompt);
}
