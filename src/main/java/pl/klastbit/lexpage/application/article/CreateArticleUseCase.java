package pl.klastbit.lexpage.application.article;

import pl.klastbit.lexpage.application.article.command.CreateArticleCommand;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;

/**
 * Use case for creating a new article in DRAFT status.
 * Inbound port in Hexagonal Architecture.
 */
public interface CreateArticleUseCase {

    /**
     * Creates a new draft article.
     *
     * @param command Article creation command with title, content, and metadata
     * @return Created article details
     */
    ArticleDetailDto execute(CreateArticleCommand command);
}
