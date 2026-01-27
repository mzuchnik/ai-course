package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.article.ListArticlesUseCase;
import pl.klastbit.lexpage.application.article.dto.ArticleListItemDto;
import pl.klastbit.lexpage.application.article.dto.PageDto;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.user.UserId;

/**
 * Implementation of ListArticlesUseCase.
 * Lists articles with optional filtering, sorting, and pagination.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListArticlesUseCaseImpl implements ListArticlesUseCase {

    private final ArticleRepository articleRepository;

    @Override
    public PageDto<ArticleListItemDto> execute(
            ArticleStatus status,
            UserId authorId,
            String keyword,
            Pageable pageable
    ) {
        log.info("Listing articles with status: {}, authorId: {}, keyword: {}", status, authorId, keyword);

        Page<Article> articlesPage;

        // Apply filters based on parameters
        if (status != null && authorId != null) {
            articlesPage = articleRepository.findAllByStatusAndAuthorIdAndDeletedAtIsNull(
                    status, authorId, pageable
            );
        } else if (status != null) {
            articlesPage = articleRepository.findAllByStatusAndDeletedAtIsNull(status, pageable);
        } else if (authorId != null) {
            articlesPage = articleRepository.findAllByAuthorIdAndDeletedAtIsNull(authorId, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            articlesPage = articleRepository.searchByKeywordAndDeletedAtIsNull(keyword, pageable);
        } else {
            articlesPage = articleRepository.findAllByDeletedAtIsNull(pageable);
        }

        // Map domain entities to DTOs
        Page<ArticleListItemDto> dtoPage = articlesPage.map(article ->
                ArticleListItemDto.from(article, "Author Name") // TODO: Fetch real author names
        );

        return PageDto.from(dtoPage);
    }
}
