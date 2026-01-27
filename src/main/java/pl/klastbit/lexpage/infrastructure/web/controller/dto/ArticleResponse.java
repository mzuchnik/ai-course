package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for single article (detail view).
 * Immutable Record for REST API responses.
 */
public record ArticleResponse(
        Long id,
        String title,
        String slug,
        String content,
        String excerpt,
        String status,
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
     * Factory method to create response from application DTO.
     *
     * @param dto Application layer DTO
     * @return ArticleResponse
     */
    public static ArticleResponse from(ArticleDetailDto dto) {
        return new ArticleResponse(
                dto.id(),
                dto.title(),
                dto.slug(),
                dto.content(),
                dto.excerpt(),
                dto.status().name(),
                dto.authorId(),
                dto.authorName(),
                dto.publishedAt(),
                dto.metaTitle(),
                dto.metaDescription(),
                dto.ogImageUrl(),
                dto.canonicalUrl(),
                dto.keywords(),
                dto.createdBy(),
                dto.createdByName(),
                dto.updatedBy(),
                dto.updatedByName(),
                dto.createdAt(),
                dto.updatedAt()
        );
    }
}
