package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import pl.klastbit.lexpage.application.article.dto.ArticleListItemDto;
import pl.klastbit.lexpage.application.article.dto.PageDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for paginated article list.
 * Immutable Record for REST API responses.
 */
public record ArticleListResponse(
        List<ArticleListItem> content,
        PageInfo page
) {

    /**
     * Single article item in the list.
     */
    public record ArticleListItem(
            Long id,
            String title,
            String slug,
            String excerpt,
            String status,
            String authorId,
            String authorName,
            LocalDateTime publishedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {

        /**
         * Factory method to create item from application DTO.
         */
        public static ArticleListItem from(ArticleListItemDto dto) {
            return new ArticleListItem(
                    dto.id(),
                    dto.title(),
                    dto.slug(),
                    dto.excerpt(),
                    dto.status().name(),
                    dto.authorId(),
                    dto.authorName(),
                    dto.publishedAt(),
                    dto.createdAt(),
                    dto.updatedAt()
            );
        }
    }

    /**
     * Pagination metadata.
     */
    public record PageInfo(
            int number,
            int size,
            long totalElements,
            int totalPages
    ) {
    }

    /**
     * Factory method to create response from application PageDto.
     *
     * @param pageDto Application layer PageDto
     * @return ArticleListResponse
     */
    public static ArticleListResponse from(PageDto<ArticleListItemDto> pageDto) {
        List<ArticleListItem> items = pageDto.content().stream()
                .map(ArticleListItem::from)
                .toList();

        return new ArticleListResponse(
                items,
                new PageInfo(
                        pageDto.page().number(),
                        pageDto.page().size(),
                        pageDto.page().totalElements(),
                        pageDto.page().totalPages()
                )
        );
    }
}
