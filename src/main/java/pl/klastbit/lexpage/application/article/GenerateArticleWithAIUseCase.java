package pl.klastbit.lexpage.application.article;

import pl.klastbit.lexpage.application.article.command.GenerateArticleWithAICommand;
import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;

/**
 * Use case for generating article content using AI.
 */
public interface GenerateArticleWithAIUseCase {
    AIGeneratedContentDto execute(GenerateArticleWithAICommand command);
}
