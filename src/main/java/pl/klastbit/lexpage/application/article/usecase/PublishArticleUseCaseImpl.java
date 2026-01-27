package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.article.PublishArticleUseCase;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;

/**
 * Implementation of PublishArticleUseCase.
 * Changes article status from DRAFT to PUBLISHED.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PublishArticleUseCaseImpl implements PublishArticleUseCase {

    private final ArticleRepository articleRepository;

    @Override
    public ArticleDetailDto execute(Long articleId) {
        log.info("Publishing article with ID: {}", articleId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        // Domain method handles business rules and throws IllegalStateException if already published
        article.publish();
        Article publishedArticle = articleRepository.save(article);

        log.info("Article published successfully with ID: {}", articleId);

        // TODO: Fetch real user names from UserRepository
        return ArticleDetailDto.from(publishedArticle, "Author Name", "Creator Name", "Updater Name");
    }
}
