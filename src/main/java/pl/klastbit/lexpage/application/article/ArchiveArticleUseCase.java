package pl.klastbit.lexpage.application.article;

import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;

/**
 * Use case for archiving an article (PUBLISHED → ARCHIVED).
 * Inbound port in Hexagonal Architecture.
 */
public interface ArchiveArticleUseCase {

    /**
     * Archives an article by changing its status from PUBLISHED to ARCHIVED.
     *
     * @param articleId ID of the article to archive
     * @return Archived article details
     */
    ArticleDetailDto execute(Long articleId);
}
