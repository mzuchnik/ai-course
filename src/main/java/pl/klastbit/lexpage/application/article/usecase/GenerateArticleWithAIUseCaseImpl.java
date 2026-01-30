package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.klastbit.lexpage.application.article.GenerateArticleWithAIUseCase;
import pl.klastbit.lexpage.application.article.command.GenerateArticleWithAICommand;
import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;
import pl.klastbit.lexpage.application.article.exception.AIGenerationException;
import pl.klastbit.lexpage.application.ports.ArticleAIPort;

/**
 * Implementation of AI article generation use case.
 * Orchestrates the AI content generation process.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateArticleWithAIUseCaseImpl implements GenerateArticleWithAIUseCase {

    private final ArticleAIPort articleAIPort;

    @Override
    public AIGeneratedContentDto execute(GenerateArticleWithAICommand command) {
        log.info("Generating article with AI for prompt: {}",
            command.userPrompt().substring(0, Math.min(50, command.userPrompt().length())));

        try {
            AIGeneratedContentDto result = articleAIPort.generateArticleContent(command.userPrompt());

            log.info("AI generation successful. Title: {}, Content length: {}",
                result.title(), result.content().length());

            return result;

        } catch (Exception e) {
            log.error("AI generation failed: {}", e.getMessage(), e);
            throw new AIGenerationException("Failed to generate article: " + e.getMessage(), e);
        }
    }
}
