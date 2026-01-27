package pl.klastbit.lexpage.application.article;

import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;

/**
 * Use case for publishing an article (DRAFT → PUBLISHED).
 * Inbound port in Hexagonal Architecture.
 */
public interface PublishArticleUseCase {

    /**
     * Publishes an article by changing its status from DRAFT to PUBLISHED.
     *
     * @param articleId ID of the article to publish
     * @return Published article details
     */
    ArticleDetailDto execute(Long articleId);
}
