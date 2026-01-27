package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.application.article.CreateArticleUseCase;
import pl.klastbit.lexpage.application.article.command.CreateArticleCommand;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;

import java.text.Normalizer;

/**
 * Implementation of CreateArticleUseCase.
 * Creates a new draft article with automatic slug generation and meta description.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateArticleUseCaseImpl implements CreateArticleUseCase {

    private final ArticleRepository articleRepository;

    @Override
    public ArticleDetailDto execute(CreateArticleCommand command) {
        log.info("Creating new article with title: {}", command.title());

        // Generate slug from title
        String slug = generateSlug(command.title());

        // Ensure slug uniqueness
        if (articleRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            slug = makeSlugUnique(slug);
        }

        // Auto-generate meta description if not provided
        String metaDescription = command.metaDescription();
        if (metaDescription == null || metaDescription.isBlank()) {
            metaDescription = generateMetaDescription(command.content());
        }

        // Create draft article using domain factory method
        Article article = Article.createDraft(
                command.title(),
                slug,
                command.content(),
                command.excerpt(),
                command.metaTitle(),
                metaDescription,
                command.ogImageUrl(),
                command.canonicalUrl(),
                command.keywords(),
                command.authorId()
        );

        Article savedArticle = articleRepository.save(article);
        log.info("Article created successfully with ID: {}", savedArticle.getId());

        // TODO: Fetch real user names from UserRepository
        return ArticleDetailDto.from(savedArticle, "Author Name", "Creator Name", "Updater Name");
    }

    // ==================== Helper Methods ====================

    /**
     * Generates URL-friendly slug from title.
     * Handles Polish characters transliteration.
     */
    private String generateSlug(String title) {
        if (title == null || title.isBlank()) {
            return "";
        }

        // Normalize to NFD (decomposed form) and remove diacritics
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");

        // Convert to lowercase and replace spaces/special chars with hyphens
        return withoutDiacritics.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Makes a slug unique by appending a counter.
     */
    private String makeSlugUnique(String slug) {
        int counter = 1;
        String uniqueSlug = slug;
        while (articleRepository.existsBySlugAndDeletedAtIsNull(uniqueSlug)) {
            uniqueSlug = slug + "-" + counter++;
        }
        return uniqueSlug;
    }

    /**
     * Generates meta description from content (first 160 chars without HTML tags).
     */
    private String generateMetaDescription(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        // Remove HTML tags
        String plainText = content.replaceAll("<[^>]+>", "").trim();

        // Truncate to 160 characters
        if (plainText.length() > 160) {
            return plainText.substring(0, 157) + "...";
        }

        return plainText;
    }
}
