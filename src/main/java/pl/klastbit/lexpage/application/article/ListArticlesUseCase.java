package pl.klastbit.lexpage.application.article;

import org.springframework.data.domain.Pageable;
import pl.klastbit.lexpage.application.article.dto.ArticleListItemDto;
import pl.klastbit.lexpage.application.article.dto.PageDto;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.user.UserId;

/**
 * Use case for listing articles with filtering, sorting, and pagination.
 * Inbound port in Hexagonal Architecture.
 */
public interface ListArticlesUseCase {

    /**
     * Lists articles with optional filtering by status, author, and keyword.
     *
     * @param status   Optional status filter (DRAFT, PUBLISHED, ARCHIVED)
     * @param authorId Optional author ID filter
     * @param keyword  Optional keyword for full-text search in title and content
     * @param pageable Pagination and sorting parameters
     * @return Page of article list items
     */
    PageDto<ArticleListItemDto> execute(
            ArticleStatus status,
            UserId authorId,
            String keyword,
            Pageable pageable
    );
}
