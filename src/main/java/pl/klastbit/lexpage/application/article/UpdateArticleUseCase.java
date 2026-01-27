package pl.klastbit.lexpage.application.article;

import pl.klastbit.lexpage.application.article.command.UpdateArticleCommand;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;

/**
 * Use case for updating an existing article (content and metadata).
 * Inbound port in Hexagonal Architecture.
 */
public interface UpdateArticleUseCase {

    /**
     * Updates an existing article.
     *
     * @param command Article update command with ID and new content
     * @return Updated article details
     */
    ArticleDetailDto execute(UpdateArticleCommand command);
}
