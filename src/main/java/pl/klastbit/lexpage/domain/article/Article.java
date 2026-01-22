package pl.klastbit.lexpage.domain.article;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Article Domain Entity (DDD Aggregate Root).
 * Encapsulates article business logic including publication workflow, SEO, and lifecycle management.
 */
public class Article {

    private Long id;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private ArticleStatus status;
    private Long authorId;
    private LocalDateTime publishedAt;

    // SEO fields
    private String metaTitle;
    private String metaDescription;
    private String ogImageUrl;
    private String canonicalUrl;
    private List<String> keywords;

    // Audit
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Private constructor to enforce factory methods
    private Article() {}

    /**
     * Factory method to create a new draft article.
     */
    public static Article createDraft(String title, String content, Long authorId) {
        Article article = new Article();
        article.title = Objects.requireNonNull(title, "Title cannot be null");
        article.content = Objects.requireNonNull(content, "Content cannot be null");
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
            Long authorId,
            LocalDateTime publishedAt,
            String metaTitle,
            String metaDescription,
            String ogImageUrl,
            String canonicalUrl,
            List<String> keywords,
            Long createdBy,
            Long updatedBy,
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
            throw new IllegalStateException("Only published articles can be archived");
        }

        this.status = ArticleStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Unpublishes the article back to DRAFT status.
     */
    public void unpublish() {
        if (status != ArticleStatus.PUBLISHED) {
            throw new IllegalStateException("Only published articles can be unpublished");
        }

        this.status = ArticleStatus.DRAFT;
        this.publishedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates article content.
     * Business rule: Content must not be empty.
     */
    public void updateContent(String newTitle, String newContent, Long userId) {
        this.title = Objects.requireNonNull(newTitle, "Title cannot be null");
        this.content = Objects.requireNonNull(newContent, "Content cannot be null");

        if (newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        this.updatedBy = userId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates SEO metadata.
     */
    public void updateSeoMetadata(String metaTitle, String metaDescription, List<String> keywords) {
        this.metaTitle = metaTitle;
        this.metaDescription = metaDescription;
        this.keywords = keywords;
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

    // Getters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getContent() {
        return content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public String getOgImageUrl() {
        return ogImageUrl;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    // Setters for infrastructure layer (reconstruction from DB)

    public void setId(Long id) {
        this.id = id;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public void setOgImageUrl(String ogImageUrl) {
        this.ogImageUrl = ogImageUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    // Package-private setters for reconstruction
    void setStatus(ArticleStatus status) {
        this.status = status;
    }

    void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
