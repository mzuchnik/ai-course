package pl.klastbit.lexpage.domain.article;

import lombok.Getter;
import pl.klastbit.lexpage.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Article Domain Entity (DDD Aggregate Root).
 * Encapsulates article business logic including publication workflow, SEO, and lifecycle management.
 */
@Getter
public class Article {

    private Long id;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private ArticleStatus status;
    private UserId authorId;
    private LocalDateTime publishedAt;

    // SEO fields
    private String metaTitle;
    private String metaDescription;
    private String ogImageUrl;
    private String canonicalUrl;
    private List<String> keywords;

    // Audit
    private UserId createdBy;
    private UserId updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Private constructor to enforce factory methods
    private Article() {}

    /**
     * Factory method to create a new draft article.
     */
    public static Article createDraft(
            String title,
            String slug,
            String content,
            String excerpt,
            String metaTitle,
            String metaDescription,
            String ogImageUrl,
            String canonicalUrl,
            List<String> keywords,
            UserId authorId
    ) {
        Article article = new Article();
        article.title = Objects.requireNonNull(title, "Title cannot be null");
        article.slug = Objects.requireNonNull(slug, "Slug cannot be null");
        article.content = Objects.requireNonNull(content, "Content cannot be null");
        article.excerpt = excerpt;
        article.metaTitle = metaTitle;
        article.metaDescription = metaDescription;
        article.ogImageUrl = ogImageUrl;
        article.canonicalUrl = canonicalUrl;
        article.keywords = keywords;
        article.authorId = Objects.requireNonNull(authorId, "Author ID cannot be null");
        article.status = ArticleStatus.DRAFT;
        article.createdAt = LocalDateTime.now();
        article.updatedAt = LocalDateTime.now();
        article.createdBy = authorId;
        article.updatedBy = authorId;
        return article;
    }

    /**
     * Factory method to reconstruct an existing article from database.
     * Used by infrastructure layer mappers. No business validation applied.
     */
    public static Article ofExisting(
            Long id,
            String title,
            String slug,
            String content,
            String excerpt,
            ArticleStatus status,
            UserId authorId,
            LocalDateTime publishedAt,
            String metaTitle,
            String metaDescription,
            String ogImageUrl,
            String canonicalUrl,
            List<String> keywords,
            UserId createdBy,
            UserId updatedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        Article article = new Article();
        article.id = id;
        article.title = title;
        article.slug = slug;
        article.content = content;
        article.excerpt = excerpt;
        article.status = status;
        article.authorId = authorId;
        article.publishedAt = publishedAt;
        article.metaTitle = metaTitle;
        article.metaDescription = metaDescription;
        article.ogImageUrl = ogImageUrl;
        article.canonicalUrl = canonicalUrl;
        article.keywords = keywords;
        article.createdBy = createdBy;
        article.updatedBy = updatedBy;
        article.createdAt = createdAt;
        article.updatedAt = updatedAt;
        article.deletedAt = deletedAt;
        return article;
    }

    /**
     * Publishes the article.
     * Business rule: Only DRAFT or ARCHIVED articles can be published.
     */
    public void publish() {
        if (status == ArticleStatus.PUBLISHED) {
            throw new IllegalStateException("Article is already published");
        }

        validatePublicationRequirements();

        this.status = ArticleStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Archives the article.
     * Business rule: Only PUBLISHED articles can be archived.
     */
    public void archive() {
        if (status != ArticleStatus.PUBLISHED) {
            throw new IllegalStateException(
                "Cannot archive article that is not in PUBLISHED status. Current status: " + status
            );
        }

        this.status = ArticleStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Unpublishes the article back to DRAFT status.
     */
    public void unpublish() {
        if (status != ArticleStatus.PUBLISHED) {
            throw new IllegalStateException(
                "Cannot unpublish article that is not in PUBLISHED status. Current status: " + status
            );
        }

        this.status = ArticleStatus.DRAFT;
        this.publishedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates article content and metadata.
     */
    public void updateContent(
            String newTitle,
            String newSlug,
            String newContent,
            String newExcerpt,
            String newMetaTitle,
            String newMetaDescription,
            String newOgImageUrl,
            String newCanonicalUrl,
            List<String> newKeywords,
            UserId userId
    ) {
        this.title = Objects.requireNonNull(newTitle, "Title cannot be null");
        this.slug = Objects.requireNonNull(newSlug, "Slug cannot be null");
        this.content = Objects.requireNonNull(newContent, "Content cannot be null");

        if (newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        this.excerpt = newExcerpt;
        this.metaTitle = newMetaTitle;
        this.metaDescription = newMetaDescription;
        this.ogImageUrl = newOgImageUrl;
        this.canonicalUrl = newCanonicalUrl;
        this.keywords = newKeywords;
        this.updatedBy = userId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Soft deletes the article.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Checks if article is published.
     */
    public boolean isPublished() {
        return status == ArticleStatus.PUBLISHED;
    }

    /**
     * Checks if article is deleted (soft delete).
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Validates requirements for publication.
     */
    private void validatePublicationRequirements() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalStateException("Cannot publish article without title");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalStateException("Cannot publish article without content");
        }

        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalStateException("Cannot publish article without slug");
        }
    }

    /**
     * Calculates approximate word count.
     */
    public int getWordCount() {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }
}
