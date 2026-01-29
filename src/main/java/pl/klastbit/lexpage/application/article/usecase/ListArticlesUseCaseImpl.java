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
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final UserRepository userRepository;

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

        // Collect all unique author IDs from the articles
        Set<UserId> authorIds = articlesPage.getContent().stream()
                .map(Article::getAuthorId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // Batch fetch all users to avoid N+1 queries
        Map<UserId, String> authorNames = authorIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> userRepository.findById(id)
                                .map(User::getUsername)
                                .orElse("Unknown User")
                ));

        // Map domain entities to DTOs using the username map
        Page<ArticleListItemDto> dtoPage = articlesPage.map(article -> {
            String authorName = authorNames.getOrDefault(article.getAuthorId(), "Unknown User");
            return ArticleListItemDto.from(article, authorName);
        });

        return PageDto.from(dtoPage);
    }
}
