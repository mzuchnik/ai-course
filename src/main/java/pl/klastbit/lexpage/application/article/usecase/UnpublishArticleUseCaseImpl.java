package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.article.UnpublishArticleUseCase;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;

/**
 * Implementation of UnpublishArticleUseCase.
 * Changes article status from PUBLISHED back to DRAFT.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UnpublishArticleUseCaseImpl implements UnpublishArticleUseCase {

    private final ArticleRepository articleRepository;

    @Override
    public ArticleDetailDto execute(Long articleId) {
        log.info("Unpublishing article with ID: {}", articleId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        // Domain method handles business rules and throws IllegalStateException if not published
        article.unpublish();
        Article unpublishedArticle = articleRepository.save(article);

        log.info("Article unpublished successfully with ID: {}", articleId);

        // TODO: Fetch real user names from UserRepository
        return ArticleDetailDto.from(unpublishedArticle, "Author Name", "Creator Name", "Updater Name");
    }
}
