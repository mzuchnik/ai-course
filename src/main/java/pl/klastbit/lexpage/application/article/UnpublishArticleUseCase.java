package pl.klastbit.lexpage.application.article;

import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;

/**
 * Use case for unpublishing an article (PUBLISHED → DRAFT).
 * Inbound port in Hexagonal Architecture.
 */
public interface UnpublishArticleUseCase {

    /**
     * Unpublishes an article by changing its status from PUBLISHED back to DRAFT.
     *
     * @param articleId ID of the article to unpublish
     * @return Unpublished article details
     */
    ArticleDetailDto execute(Long articleId);
}
