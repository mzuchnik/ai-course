package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.article.ArchiveArticleUseCase;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;

/**
 * Implementation of ArchiveArticleUseCase.
 * Changes article status from PUBLISHED to ARCHIVED.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ArchiveArticleUseCaseImpl implements ArchiveArticleUseCase {

    private final ArticleRepository articleRepository;

    @Override
    public ArticleDetailDto execute(Long articleId) {
        log.info("Archiving article with ID: {}", articleId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        // Domain method handles business rules and throws IllegalStateException if not published
        article.archive();
        Article archivedArticle = articleRepository.save(article);

        log.info("Article archived successfully with ID: {}", articleId);

        // TODO: Fetch real user names from UserRepository
        return ArticleDetailDto.from(archivedArticle, "Author Name", "Creator Name", "Updater Name");
    }
}
