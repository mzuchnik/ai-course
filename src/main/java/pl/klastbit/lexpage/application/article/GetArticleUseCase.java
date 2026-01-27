package pl.klastbit.lexpage.application.article;

import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;

/**
 * Use case for retrieving a single article by ID.
 * Inbound port in Hexagonal Architecture.
 */
public interface GetArticleUseCase {

    /**
     * Retrieves a single article by ID.
     *
     * @param articleId ID of the article to retrieve
     * @return Article details
     */
    ArticleDetailDto execute(Long articleId);
}
