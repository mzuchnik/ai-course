package pl.klastbit.lexpage.application.article.dto;

import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleStatus;

import java.time.LocalDateTime;

/**
 * DTO for article list item (summary view).
 * Immutable data transfer object (Record) for read operations.
 */
public record ArticleListItemDto(
        Long id,
        String title,
        String slug,
        String excerpt,
        ArticleStatus status,
        String authorId,
        String authorName,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * Factory method to create DTO from domain entity.
     *
     * @param article    Domain article entity
     * @param authorName Author's full name (fetched from user repository)
     * @return Article list item DTO
     */
    public static ArticleListItemDto from(Article article, String authorName) {
        return new ArticleListItemDto(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getExcerpt(),
                article.getStatus(),
                article.getAuthorId() != null ? article.getAuthorId().userid().toString() : null,
                authorName,
                article.getPublishedAt(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
