package pl.klastbit.lexpage.application.article.dto;

import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for detailed article view.
 * Immutable data transfer object (Record) for read operations.
 */
public record ArticleDetailDto(
        Long id,
        String title,
        String slug,
        String content,
        String excerpt,
        ArticleStatus status,
        String authorId,
        String authorName,
        LocalDateTime publishedAt,
        String metaTitle,
        String metaDescription,
        String ogImageUrl,
        String canonicalUrl,
        List<String> keywords,
        String createdBy,
        String createdByName,
        String updatedBy,
        String updatedByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * Factory method to create DTO from domain entity.
     *
     * @param article        Domain article entity
     * @param authorName     Author's full name
     * @param createdByName  Creator's full name
     * @param updatedByName  Last updater's full name
     * @return Article detail DTO
     */
    public static ArticleDetailDto from(
            Article article,
            String authorName,
            String createdByName,
            String updatedByName
    ) {
        return new ArticleDetailDto(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getContent(),
                article.getExcerpt(),
                article.getStatus(),
                article.getAuthorId() != null ? article.getAuthorId().userid().toString() : null,
                authorName,
                article.getPublishedAt(),
                article.getMetaTitle(),
                article.getMetaDescription(),
                article.getOgImageUrl(),
                article.getCanonicalUrl(),
                article.getKeywords(),
                article.getCreatedBy() != null ? article.getCreatedBy().userid().toString() : null,
                createdByName,
                article.getUpdatedBy() != null ? article.getUpdatedBy().userid().toString() : null,
                updatedByName,
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
