package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.article.GetArticleUseCase;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;

/**
 * Implementation of GetArticleUseCase.
 * Retrieves a single article by ID.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetArticleUseCaseImpl implements GetArticleUseCase {

    private final ArticleRepository articleRepository;

    @Override
    public ArticleDetailDto execute(Long articleId) {
        log.info("Fetching article with ID: {}", articleId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        // TODO: Fetch real user names from UserRepository
        return ArticleDetailDto.from(article, "Author Name", "Creator Name", "Updater Name");
    }

    @Override
    public ArticleDetailDto executeBySlug(String slug) {
        log.info("Fetching published article with slug: {}", slug);

        Article article = articleRepository.findBySlugAndStatusAndDeletedAtIsNull(slug, ArticleStatus.PUBLISHED)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found or not published with slug: " + slug));

        // TODO: Fetch real user names from UserRepository
        return ArticleDetailDto.from(article, "Author Name", "Creator Name", "Updater Name");
    }
}
