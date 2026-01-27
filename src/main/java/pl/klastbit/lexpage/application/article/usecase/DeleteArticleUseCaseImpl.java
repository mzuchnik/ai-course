package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.article.DeleteArticleUseCase;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;

/**
 * Implementation of DeleteArticleUseCase.
 * Performs soft delete on an article by setting deletedAt timestamp.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteArticleUseCaseImpl implements DeleteArticleUseCase {

    private final ArticleRepository articleRepository;

    @Override
    public void execute(Long articleId) {
        log.info("Soft deleting article with ID: {}", articleId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        article.softDelete();
        articleRepository.save(article);

        log.info("Article soft deleted successfully with ID: {}", articleId);
    }
}
