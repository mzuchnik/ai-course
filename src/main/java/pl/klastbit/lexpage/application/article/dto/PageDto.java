package pl.klastbit.lexpage.application.article.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic DTO for paginated results.
 * Immutable data transfer object (Record) wrapping Spring Data Page.
 */
public record PageDto<T>(
        List<T> content,
        PageInfo page
) {

    /**
     * Page metadata (pagination info).
     */
    public record PageInfo(
            int number,
            int size,
            long totalElements,
            int totalPages
    ) {
    }

    /**
     * Factory method to create PageDto from Spring Data Page.
     *
     * @param page Spring Data Page object
     * @param <T>  Content type
     * @return PageDto with content and metadata
     */
    public static <T> PageDto<T> from(Page<T> page) {
        return new PageDto<>(
                page.getContent(),
                new PageInfo(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }
}
