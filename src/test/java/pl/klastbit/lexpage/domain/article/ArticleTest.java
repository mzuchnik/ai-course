package pl.klastbit.lexpage.domain.article;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.klastbit.lexpage.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Article domain entity.
 * Tests business logic and domain rules without any infrastructure dependencies.
 */
@DisplayName("Article Domain Entity Tests")
class ArticleTest {

    @Nested
    @DisplayName("createDraft() factory method")
    class CreateDraftTests {

        @Test
        @DisplayName("should create draft article with all fields")
        void shouldCreateDraftArticleWithAllFields() {
            // given
            UserId authorId = UserId.createNew();
            List<String> keywords = Arrays.asList("test", "article");

            // when
            Article article = Article.createDraft(
                    "Test Title",
                    "test-slug",
                    "Test content with enough characters to be valid content for article",
                    "Test excerpt",
                    "Test Meta Title",
                    "Test Meta Description",
                    "https://example.com/image.jpg",
                    "https://example.com/article",
                    keywords,
                    authorId
            );

            // then
            assertThat(article).isNotNull();
            assertThat(article.getTitle()).isEqualTo("Test Title");
            assertThat(article.getSlug()).isEqualTo("test-slug");
            assertThat(article.getContent()).contains("Test content");
            assertThat(article.getExcerpt()).isEqualTo("Test excerpt");
            assertThat(article.getStatus()).isEqualTo(ArticleStatus.DRAFT);
            assertThat(article.getAuthorId()).isEqualTo(authorId);
            assertThat(article.getMetaTitle()).isEqualTo("Test Meta Title");
            assertThat(article.getMetaDescription()).isEqualTo("Test Meta Description");
            assertThat(article.getOgImageUrl()).isEqualTo("https://example.com/image.jpg");
            assertThat(article.getCanonicalUrl()).isEqualTo("https://example.com/article");
            assertThat(article.getKeywords()).containsExactly("test", "article");
            assertThat(article.getCreatedBy()).isEqualTo(authorId);
            assertThat(article.getUpdatedBy()).isEqualTo(authorId);
            assertThat(article.getPublishedAt()).isNull();
            assertThat(article.getCreatedAt()).isNotNull().isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(article.getUpdatedAt()).isNotNull().isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("should create draft article with minimal fields")
        void shouldCreateDraftArticleWithMinimalFields() {
            // given
            UserId authorId = UserId.createNew();

            // when
            Article article = Article.createDraft(
                    "Test Title",
                    "test-slug",
                    "Test content",
                    null,  // excerpt
                    null,  // metaTitle
                    null,  // metaDescription
                    null,  // ogImageUrl
                    null,  // canonicalUrl
                    null,  // keywords
                    authorId
            );

            // then
            assertThat(article).isNotNull();
            assertThat(article.getTitle()).isEqualTo("Test Title");
            assertThat(article.getSlug()).isEqualTo("test-slug");
            assertThat(article.getContent()).isEqualTo("Test content");
            assertThat(article.getStatus()).isEqualTo(ArticleStatus.DRAFT);
            assertThat(article.getExcerpt()).isNull();
            assertThat(article.getMetaTitle()).isNull();
            assertThat(article.getKeywords()).isNull();
        }

        @Test
        @DisplayName("should throw exception when title is null")
        void shouldThrowExceptionWhenTitleIsNull() {
            // given
            UserId authorId = UserId.createNew();

            // when/then
            assertThatThrownBy(() ->
                    Article.createDraft(
                            null,
                            "test-slug",
                            "Test content",
                            null, null, null, null, null, null,
                            authorId
                    )
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Title cannot be null");
        }

        @Test
        @DisplayName("should throw exception when slug is null")
        void shouldThrowExceptionWhenSlugIsNull() {
            // given
            UserId authorId = UserId.createNew();

            // when/then
            assertThatThrownBy(() ->
                    Article.createDraft(
                            "Test Title",
                            null,
                            "Test content",
                            null, null, null, null, null, null,
                            authorId
                    )
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Slug cannot be null");
        }

        @Test
        @DisplayName("should throw exception when content is null")
        void shouldThrowExceptionWhenContentIsNull() {
            // given
            UserId authorId = UserId.createNew();

            // when/then
            assertThatThrownBy(() ->
                    Article.createDraft(
                            "Test Title",
                            "test-slug",
                            null,
                            null, null, null, null, null, null,
                            authorId
                    )
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Content cannot be null");
        }

        @Test
        @DisplayName("should throw exception when authorId is null")
        void shouldThrowExceptionWhenAuthorIdIsNull() {
            // when/then
            assertThatThrownBy(() ->
                    Article.createDraft(
                            "Test Title",
                            "test-slug",
                            "Test content",
                            null, null, null, null, null, null,
                            null
                    )
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Author ID cannot be null");
        }
    }

    @Nested
    @DisplayName("publish() method")
    class PublishTests {

        @Test
        @DisplayName("should publish draft article")
        void shouldPublishDraftArticle() {
            // given
            Article article = Article.createDraft(
                    "Test Title",
                    "test-slug",
                    "Test content",
                    null, null, null, null, null, null,
                    UserId.createNew()
            );
            LocalDateTime beforePublish = LocalDateTime.now();

            // when
            article.publish();

            // then
            assertThat(article.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
            assertThat(article.getPublishedAt()).isNotNull()
                    .isAfterOrEqualTo(beforePublish)
                    .isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(article.getUpdatedAt()).isNotNull()
                    .isAfterOrEqualTo(beforePublish);
        }

        @Test
        @DisplayName("should publish archived article")
        void shouldPublishArchivedArticle() {
            // given
            Article article = createPublishedArticle();
            article.archive();

            // when
            article.publish();

            // then
            assertThat(article.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
            assertThat(article.getPublishedAt()).isNotNull();
        }

        @Test
        @DisplayName("should throw exception when article is already published")
        void shouldThrowExceptionWhenArticleIsAlreadyPublished() {
            // given
            Article article = createPublishedArticle();

            // when/then
            assertThatThrownBy(article::publish)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already published");
        }

        @Test
        @DisplayName("should throw exception when publishing article without title")
        void shouldThrowExceptionWhenPublishingArticleWithoutTitle() {
            // given
            Article article = Article.ofExisting(
                    1L, null, "test-slug", "Content", null,
                    ArticleStatus.DRAFT, UserId.createNew(), null,
                    null, null, null, null, null,
                    UserId.createNew(), UserId.createNew(),
                    LocalDateTime.now(), LocalDateTime.now(), null
            );

            // when/then
            assertThatThrownBy(article::publish)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot publish article without title");
        }

        @Test
        @DisplayName("should throw exception when publishing article without content")
        void shouldThrowExceptionWhenPublishingArticleWithoutContent() {
            // given
            Article article = Article.ofExisting(
                    1L, "Title", "test-slug", "", null,
                    ArticleStatus.DRAFT, UserId.createNew(), null,
                    null, null, null, null, null,
                    UserId.createNew(), UserId.createNew(),
                    LocalDateTime.now(), LocalDateTime.now(), null
            );

            // when/then
            assertThatThrownBy(article::publish)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot publish article without content");
        }

        @Test
        @DisplayName("should throw exception when publishing article without slug")
        void shouldThrowExceptionWhenPublishingArticleWithoutSlug() {
            // given
            Article article = Article.ofExisting(
                    1L, "Title", "", "Content", null,
                    ArticleStatus.DRAFT, UserId.createNew(), null,
                    null, null, null, null, null,
                    UserId.createNew(), UserId.createNew(),
                    LocalDateTime.now(), LocalDateTime.now(), null
            );

            // when/then
            assertThatThrownBy(article::publish)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot publish article without slug");
        }
    }

    @Nested
    @DisplayName("archive() method")
    class ArchiveTests {

        @Test
        @DisplayName("should archive published article")
        void shouldArchivePublishedArticle() {
            // given
            Article article = createPublishedArticle();
            LocalDateTime beforeArchive = LocalDateTime.now();

            // when
            article.archive();

            // then
            assertThat(article.getStatus()).isEqualTo(ArticleStatus.ARCHIVED);
            assertThat(article.getPublishedAt()).isNotNull(); // publishedAt should remain unchanged
            assertThat(article.getUpdatedAt()).isNotNull()
                    .isAfterOrEqualTo(beforeArchive);
        }

        @Test
        @DisplayName("should throw exception when archiving draft article")
        void shouldThrowExceptionWhenArchivingDraftArticle() {
            // given
            Article article = Article.createDraft(
                    "Test Title",
                    "test-slug",
                    "Test content",
                    null, null, null, null, null, null,
                    UserId.createNew()
            );

            // when/then
            assertThatThrownBy(article::archive)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot archive article that is not in PUBLISHED status")
                    .hasMessageContaining("DRAFT");
        }

        @Test
        @DisplayName("should throw exception when archiving already archived article")
        void shouldThrowExceptionWhenArchivingAlreadyArchivedArticle() {
            // given
            Article article = createPublishedArticle();
            article.archive();

            // when/then
            assertThatThrownBy(article::archive)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot archive article that is not in PUBLISHED status")
                    .hasMessageContaining("ARCHIVED");
        }
    }

    @Nested
    @DisplayName("unpublish() method")
    class UnpublishTests {

        @Test
        @DisplayName("should unpublish published article back to draft")
        void shouldUnpublishPublishedArticleBackToDraft() {
            // given
            Article article = createPublishedArticle();
            LocalDateTime beforeUnpublish = LocalDateTime.now();

            // when
            article.unpublish();

            // then
            assertThat(article.getStatus()).isEqualTo(ArticleStatus.DRAFT);
            assertThat(article.getPublishedAt()).isNull(); // publishedAt should be cleared
            assertThat(article.getUpdatedAt()).isNotNull()
                    .isAfterOrEqualTo(beforeUnpublish);
        }

        @Test
        @DisplayName("should throw exception when unpublishing draft article")
        void shouldThrowExceptionWhenUnpublishingDraftArticle() {
            // given
            Article article = Article.createDraft(
                    "Test Title",
                    "test-slug",
                    "Test content",
                    null, null, null, null, null, null,
                    UserId.createNew()
            );

            // when/then
            assertThatThrownBy(article::unpublish)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot unpublish article that is not in PUBLISHED status")
                    .hasMessageContaining("DRAFT");
        }

        @Test
        @DisplayName("should throw exception when unpublishing archived article")
        void shouldThrowExceptionWhenUnpublishingArchivedArticle() {
            // given
            Article article = createPublishedArticle();
            article.archive();

            // when/then
            assertThatThrownBy(article::unpublish)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot unpublish article that is not in PUBLISHED status")
                    .hasMessageContaining("ARCHIVED");
        }
    }

    @Nested
    @DisplayName("updateContent() method")
    class UpdateContentTests {

        @Test
        @DisplayName("should update all content fields")
        void shouldUpdateAllContentFields() {
            // given
            Article article = Article.createDraft(
                    "Old Title",
                    "old-slug",
                    "Old content",
                    "Old excerpt",
                    null, null, null, null, null,
                    UserId.createNew()
            );
            UserId updaterId = UserId.createNew();
            List<String> newKeywords = Arrays.asList("new", "keywords");
            LocalDateTime beforeUpdate = LocalDateTime.now();

            // when
            article.updateContent(
                    "New Title",
                    "new-slug",
                    "New content with updated text",
                    "New excerpt",
                    "New Meta Title",
                    "New Meta Description",
                    "https://example.com/new-image.jpg",
                    "https://example.com/new-article",
                    newKeywords,
                    updaterId
            );

            // then
            assertThat(article.getTitle()).isEqualTo("New Title");
            assertThat(article.getSlug()).isEqualTo("new-slug");
            assertThat(article.getContent()).isEqualTo("New content with updated text");
            assertThat(article.getExcerpt()).isEqualTo("New excerpt");
            assertThat(article.getMetaTitle()).isEqualTo("New Meta Title");
            assertThat(article.getMetaDescription()).isEqualTo("New Meta Description");
            assertThat(article.getOgImageUrl()).isEqualTo("https://example.com/new-image.jpg");
            assertThat(article.getCanonicalUrl()).isEqualTo("https://example.com/new-article");
            assertThat(article.getKeywords()).containsExactly("new", "keywords");
            assertThat(article.getUpdatedBy()).isEqualTo(updaterId);
            assertThat(article.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
        }

        @Test
        @DisplayName("should throw exception when new title is null")
        void shouldThrowExceptionWhenNewTitleIsNull() {
            // given
            Article article = createDraftArticle();
            UserId updaterId = UserId.createNew();

            // when/then
            assertThatThrownBy(() ->
                    article.updateContent(
                            null, "slug", "content", null, null, null, null, null, null, updaterId
                    )
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Title cannot be null");
        }

        @Test
        @DisplayName("should throw exception when new slug is null")
        void shouldThrowExceptionWhenNewSlugIsNull() {
            // given
            Article article = createDraftArticle();
            UserId updaterId = UserId.createNew();

            // when/then
            assertThatThrownBy(() ->
                    article.updateContent(
                            "Title", null, "content", null, null, null, null, null, null, updaterId
                    )
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Slug cannot be null");
        }

        @Test
        @DisplayName("should throw exception when new content is null")
        void shouldThrowExceptionWhenNewContentIsNull() {
            // given
            Article article = createDraftArticle();
            UserId updaterId = UserId.createNew();

            // when/then
            assertThatThrownBy(() ->
                    article.updateContent(
                            "Title", "slug", null, null, null, null, null, null, null, updaterId
                    )
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Content cannot be null");
        }

        @Test
        @DisplayName("should throw exception when new content is empty")
        void shouldThrowExceptionWhenNewContentIsEmpty() {
            // given
            Article article = createDraftArticle();
            UserId updaterId = UserId.createNew();

            // when/then
            assertThatThrownBy(() ->
                    article.updateContent(
                            "Title", "slug", "   ", null, null, null, null, null, null, updaterId
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Content cannot be empty");
        }
    }

    @Nested
    @DisplayName("softDelete() method")
    class SoftDeleteTests {

        @Test
        @DisplayName("should set deletedAt timestamp")
        void shouldSetDeletedAtTimestamp() {
            // given
            Article article = createDraftArticle();
            LocalDateTime beforeDelete = LocalDateTime.now();

            // when
            article.softDelete();

            // then
            assertThat(article.getDeletedAt()).isNotNull()
                    .isAfterOrEqualTo(beforeDelete)
                    .isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(article.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("should update updatedAt timestamp")
        void shouldUpdateUpdatedAtTimestamp() {
            // given
            Article article = createDraftArticle();
            LocalDateTime originalUpdatedAt = article.getUpdatedAt();

            // when
            article.softDelete();

            // then
            assertThat(article.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        }
    }

    @Nested
    @DisplayName("isPublished() method")
    class IsPublishedTests {

        @Test
        @DisplayName("should return true for published article")
        void shouldReturnTrueForPublishedArticle() {
            // given
            Article article = createPublishedArticle();

            // when/then
            assertThat(article.isPublished()).isTrue();
        }

        @Test
        @DisplayName("should return false for draft article")
        void shouldReturnFalseForDraftArticle() {
            // given
            Article article = createDraftArticle();

            // when/then
            assertThat(article.isPublished()).isFalse();
        }

        @Test
        @DisplayName("should return false for archived article")
        void shouldReturnFalseForArchivedArticle() {
            // given
            Article article = createPublishedArticle();
            article.archive();

            // when/then
            assertThat(article.isPublished()).isFalse();
        }
    }

    @Nested
    @DisplayName("isDeleted() method")
    class IsDeletedTests {

        @Test
        @DisplayName("should return true for soft-deleted article")
        void shouldReturnTrueForSoftDeletedArticle() {
            // given
            Article article = createDraftArticle();
            article.softDelete();

            // when/then
            assertThat(article.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("should return false for non-deleted article")
        void shouldReturnFalseForNonDeletedArticle() {
            // given
            Article article = createDraftArticle();

            // when/then
            assertThat(article.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("getWordCount() method")
    class GetWordCountTests {

        @Test
        @DisplayName("should calculate word count correctly")
        void shouldCalculateWordCountCorrectly() {
            // given
            Article article = Article.createDraft(
                    "Test Title",
                    "test-slug",
                    "This is a test content with ten words in it",
                    null, null, null, null, null, null,
                    UserId.createNew()
            );

            // when
            int wordCount = article.getWordCount();

            // then
            assertThat(wordCount).isEqualTo(10);
        }

        @Test
        @DisplayName("should return 0 for empty content")
        void shouldReturnZeroForEmptyContent() {
            // given
            Article article = Article.ofExisting(
                    1L, "Title", "slug", "", null,
                    ArticleStatus.DRAFT, UserId.createNew(), null,
                    null, null, null, null, null,
                    UserId.createNew(), UserId.createNew(),
                    LocalDateTime.now(), LocalDateTime.now(), null
            );

            // when
            int wordCount = article.getWordCount();

            // then
            assertThat(wordCount).isEqualTo(0);
        }

        @Test
        @DisplayName("should return 0 for null content")
        void shouldReturnZeroForNullContent() {
            // given
            Article article = Article.ofExisting(
                    1L, "Title", "slug", null, null,
                    ArticleStatus.DRAFT, UserId.createNew(), null,
                    null, null, null, null, null,
                    UserId.createNew(), UserId.createNew(),
                    LocalDateTime.now(), LocalDateTime.now(), null
            );

            // when
            int wordCount = article.getWordCount();

            // then
            assertThat(wordCount).isEqualTo(0);
        }

        @Test
        @DisplayName("should handle multiple spaces between words")
        void shouldHandleMultipleSpacesBetweenWords() {
            // given
            Article article = Article.createDraft(
                    "Test Title",
                    "test-slug",
                    "Word1    Word2     Word3",
                    null, null, null, null, null, null,
                    UserId.createNew()
            );

            // when
            int wordCount = article.getWordCount();

            // then
            assertThat(wordCount).isEqualTo(3);
        }
    }

    // Helper methods

    private Article createDraftArticle() {
        return Article.createDraft(
                "Test Title",
                "test-slug",
                "Test content for draft article",
                null, null, null, null, null, null,
                UserId.createNew()
        );
    }

    private Article createPublishedArticle() {
        Article article = Article.createDraft(
                "Published Article",
                "published-article",
                "Content for published article",
                null, null, null, null, null, null,
                UserId.createNew()
        );
        article.publish();
        return article;
    }
}
