package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.article.UpdateArticleUseCase;
import pl.klastbit.lexpage.application.article.command.UpdateArticleCommand;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;
import pl.klastbit.lexpage.domain.user.UserId;

import java.text.Normalizer;

/**
 * Implementation of UpdateArticleUseCase.
 * Updates an existing article with automatic slug regeneration when title changes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateArticleUseCaseImpl implements UpdateArticleUseCase {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Override
    public ArticleDetailDto execute(UpdateArticleCommand command) {
        log.info("Updating article with ID: {}", command.id());

        Article article = articleRepository.findByIdAndDeletedAtIsNull(command.id())
                .orElseThrow(() -> new ArticleNotFoundException(command.id()));

        // Regenerate slug if title changed
        String newSlug = generateSlug(command.title());
        if (!article.getTitle().equals(command.title())) {
            if (!newSlug.equals(article.getSlug()) &&
                    articleRepository.existsBySlugAndDeletedAtIsNull(newSlug)) {
                newSlug = makeSlugUnique(newSlug);
            }
        } else {
            newSlug = article.getSlug(); // Keep existing slug if title unchanged
        }

        // Auto-generate meta description if not provided
        String metaDescription = command.metaDescription();
        if (metaDescription == null || metaDescription.isBlank()) {
            metaDescription = generateMetaDescription(command.content());
        }

        // Update article content using domain method
        article.updateContent(
                command.title(),
                newSlug,
                command.content(),
                command.excerpt(),
                command.metaTitle(),
                metaDescription,
                command.ogImageUrl(),
                command.canonicalUrl(),
                command.keywords(),
                command.updatedBy()
        );

        Article updatedArticle = articleRepository.save(article);
        log.info("Article updated successfully with ID: {}", updatedArticle.getId());

        // Fetch real user names from UserRepository
        String authorName = getUsernameById(updatedArticle.getAuthorId());
        String createdByName = getUsernameById(updatedArticle.getCreatedBy());
        String updatedByName = getUsernameById(updatedArticle.getUpdatedBy());

        return ArticleDetailDto.from(updatedArticle, authorName, createdByName, updatedByName);
    }

    // ==================== Helper Methods ====================

    private String generateSlug(String title) {
        if (title == null || title.isBlank()) {
            return "";
        }

        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");

        return withoutDiacritics.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private String makeSlugUnique(String slug) {
        int counter = 1;
        String uniqueSlug = slug;
        while (articleRepository.existsBySlugAndDeletedAtIsNull(uniqueSlug)) {
            uniqueSlug = slug + "-" + counter++;
        }
        return uniqueSlug;
    }

    private String generateMetaDescription(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        String plainText = content.replaceAll("<[^>]+>", "").trim();

        if (plainText.length() > 160) {
            return plainText.substring(0, 157) + "...";
        }

        return plainText;
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
