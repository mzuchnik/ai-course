package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.article.UnpublishArticleUseCase;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;
import pl.klastbit.lexpage.domain.user.UserId;

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
    private final UserRepository userRepository;

    @Override
    public ArticleDetailDto execute(Long articleId) {
        log.info("Unpublishing article with ID: {}", articleId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        // Domain method handles business rules and throws IllegalStateException if not published
        article.unpublish();
        Article unpublishedArticle = articleRepository.save(article);

        log.info("Article unpublished successfully with ID: {}", articleId);

        // Fetch real user names from UserRepository
        String authorName = getUsernameById(unpublishedArticle.getAuthorId());
        String createdByName = getUsernameById(unpublishedArticle.getCreatedBy());
        String updatedByName = getUsernameById(unpublishedArticle.getUpdatedBy());

        return ArticleDetailDto.from(unpublishedArticle, authorName, createdByName, updatedByName);
    }

    /**
     * Fetches username by user ID from UserRepository.
     * Returns "Unknown User" if user not found.
     */
    private String getUsernameById(UserId userId) {
        if (userId == null) {
            return "Unknown User";
        }

        return userRepository.findById(userId)
                .map(user -> user.getUsername())
                .orElse("Unknown User");
    }
}
