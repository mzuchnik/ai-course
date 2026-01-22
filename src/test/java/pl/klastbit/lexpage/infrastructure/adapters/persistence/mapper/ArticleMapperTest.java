package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ArticleMapper Unit Tests")
class ArticleMapperTest {

    private ArticleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArticleMapper();
    }

    @Nested
    @DisplayName("toDomain() method")
    class ToDomainTests {

        @Test
        @DisplayName("should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            // when
            Article result = mapper.toDomain(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields from entity to domain")
        void shouldMapAllFieldsFromEntityToDomain() {
            // given
            ArticleEntity entity = createFullArticleEntity();

            // when
            Article result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Article");
            assertThat(result.getSlug()).isEqualTo("test-article");
            assertThat(result.getContent()).isEqualTo("Test content");
            assertThat(result.getExcerpt()).isEqualTo("Test excerpt");
            assertThat(result.getStatus()).isEqualTo(pl.klastbit.lexpage.domain.article.ArticleStatus.PUBLISHED);
            assertThat(result.getAuthorId()).isEqualTo(UserId.of(entity.getAuthor().getId()));
            assertThat(result.getPublishedAt()).isNotNull();
            assertThat(result.getMetaTitle()).isEqualTo("Meta Title");
            assertThat(result.getMetaDescription()).isEqualTo("Meta Description");
            assertThat(result.getOgImageUrl()).isEqualTo("https://example.com/image.jpg");
            assertThat(result.getCanonicalUrl()).isEqualTo("https://example.com/article");
            assertThat(result.getKeywords()).containsExactly("java", "spring", "testing");
            assertThat(result.getCreatedBy()).isEqualTo(UserId.of(entity.getCreatedBy().getId()));
            assertThat(result.getUpdatedBy()).isEqualTo(UserId.of(entity.getUpdatedBy().getId()));
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("should handle null author reference")
        void shouldHandleNullAuthorReference() {
            // given
            ArticleEntity entity = createMinimalArticleEntity();
            entity.setAuthor(null);

            // when
            Article result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAuthorId()).isNull();
        }

        @Test
        @DisplayName("should handle null audit references")
        void shouldHandleNullAuditReferences() {
            // given
            ArticleEntity entity = createMinimalArticleEntity();
            entity.setCreatedBy(null);
            entity.setUpdatedBy(null);

            // when
            Article result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCreatedBy()).isNull();
            assertThat(result.getUpdatedBy()).isNull();
        }

        @Test
        @DisplayName("should convert keywords array to list")
        void shouldConvertKeywordsArrayToList() {
            // given
            ArticleEntity entity = createMinimalArticleEntity();
            entity.setKeywords(new String[]{"keyword1", "keyword2", "keyword3"});

            // when
            Article result = mapper.toDomain(entity);

            // then
            assertThat(result.getKeywords())
                .isNotNull()
                .hasSize(3)
                .containsExactly("keyword1", "keyword2", "keyword3");
        }

        @Test
        @DisplayName("should handle null keywords array")
        void shouldHandleNullKeywordsArray() {
            // given
            ArticleEntity entity = createMinimalArticleEntity();
            entity.setKeywords(null);

            // when
            Article result = mapper.toDomain(entity);

            // then
            assertThat(result.getKeywords()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity() method")
    class ToEntityTests {

        @Test
        @DisplayName("should return null when domain is null")
        void shouldReturnNullWhenDomainIsNull() {
            // when
            ArticleEntity result = mapper.toEntity(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields from domain to entity")
        void shouldMapAllFieldsFromDomainToEntity() {
            // given
            Article domain = createFullArticleDomain();

            // when
            ArticleEntity result = mapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Article");
            assertThat(result.getSlug()).isEqualTo("test-article");
            assertThat(result.getContent()).isEqualTo("Test content");
            assertThat(result.getExcerpt()).isEqualTo("Test excerpt");
            assertThat(result.getStatus()).isEqualTo(pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleStatus.PUBLISHED);
            assertThat(result.getPublishedAt()).isNotNull();
            assertThat(result.getMetaTitle()).isEqualTo("Meta Title");
            assertThat(result.getMetaDescription()).isEqualTo("Meta Description");
            assertThat(result.getOgImageUrl()).isEqualTo("https://example.com/image.jpg");
            assertThat(result.getCanonicalUrl()).isEqualTo("https://example.com/article");
            assertThat(result.getKeywords()).containsExactly("java", "spring", "testing");
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("should not set user entity references")
        void shouldNotSetUserEntityReferences() {
            // given
            Article domain = createFullArticleDomain();

            // when
            ArticleEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getAuthor()).isNull();
            assertThat(result.getCreatedBy()).isNull();
            assertThat(result.getUpdatedBy()).isNull();
        }

        @Test
        @DisplayName("should convert keywords list to array")
        void shouldConvertKeywordsListToArray() {
            // given
            Article domain = Article.ofExisting(
                1L,
                "Test Article",
                "test-article",
                "Test content",
                null,
                pl.klastbit.lexpage.domain.article.ArticleStatus.DRAFT,
                UserId.createNew(),
                null,
                null,
                null,
                null,
                null,
                Arrays.asList("keyword1", "keyword2"),
                    UserId.createNew(),
                    UserId.createNew(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
            );

            // when
            ArticleEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getKeywords())
                .isNotNull()
                .hasSize(2)
                .containsExactly("keyword1", "keyword2");
        }

        @Test
        @DisplayName("should handle null keywords list")
        void shouldHandleNullKeywordsList() {
            // given
            Article domain = Article.ofExisting(
                1L,
                "Test Article",
                "test-article",
                "Test content",
                null,
                pl.klastbit.lexpage.domain.article.ArticleStatus.DRAFT,
                    UserId.createNew(),
                null,
                null,
                null,
                null,
                null,
                null,
                    UserId.createNew(),
                    UserId.createNew(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
            );

            // when
            ArticleEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getKeywords()).isNull();
        }
    }

    @Nested
    @DisplayName("updateEntity() method")
    class UpdateEntityTests {

        @Test
        @DisplayName("should do nothing when entity is null")
        void shouldDoNothingWhenEntityIsNull() {
            // given
            Article domain = createFullArticleDomain();

            // when/then - no exception should be thrown
            mapper.updateEntity(null, domain);
        }

        @Test
        @DisplayName("should do nothing when domain is null")
        void shouldDoNothingWhenDomainIsNull() {
            // given
            ArticleEntity entity = createFullArticleEntity();

            // when/then - no exception should be thrown
            mapper.updateEntity(entity, null);
        }

        @Test
        @DisplayName("should update all mutable fields from domain to entity")
        void shouldUpdateAllMutableFieldsFromDomainToEntity() {
            // given
            ArticleEntity entity = createFullArticleEntity();
            LocalDateTime newUpdatedAt = LocalDateTime.now().plusDays(1);
            Article domain = Article.ofExisting(
                1L,
                "Updated Title",
                "updated-slug",
                "Updated content",
                "Updated excerpt",
                pl.klastbit.lexpage.domain.article.ArticleStatus.DRAFT,
                    UserId.createNew(),
                null,
                "Updated Meta Title",
                "Updated Meta Description",
                "https://example.com/new-image.jpg",
                "https://example.com/new-article",
                Arrays.asList("updated", "keywords"),
                    UserId.createNew(),
                    UserId.createNew(),
                LocalDateTime.now(),
                newUpdatedAt,
                null
            );

            // when
            mapper.updateEntity(entity, domain);

            // then
            assertThat(entity.getTitle()).isEqualTo("Updated Title");
            assertThat(entity.getSlug()).isEqualTo("updated-slug");
            assertThat(entity.getContent()).isEqualTo("Updated content");
            assertThat(entity.getExcerpt()).isEqualTo("Updated excerpt");
            assertThat(entity.getStatus()).isEqualTo(pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleStatus.DRAFT);
            assertThat(entity.getPublishedAt()).isNull();
            assertThat(entity.getMetaTitle()).isEqualTo("Updated Meta Title");
            assertThat(entity.getMetaDescription()).isEqualTo("Updated Meta Description");
            assertThat(entity.getOgImageUrl()).isEqualTo("https://example.com/new-image.jpg");
            assertThat(entity.getCanonicalUrl()).isEqualTo("https://example.com/new-article");
            assertThat(entity.getKeywords()).containsExactly("updated", "keywords");
            assertThat(entity.getUpdatedAt()).isEqualTo(newUpdatedAt);
        }

        @Test
        @DisplayName("should preserve entity relationships during update")
        void shouldPreserveEntityRelationshipsDuringUpdate() {
            // given
            ArticleEntity entity = createFullArticleEntity();
            UserEntity originalAuthor = entity.getAuthor();
            UserEntity originalCreatedBy = entity.getCreatedBy();
            UserEntity originalUpdatedBy = entity.getUpdatedBy();

            Article domain = Article.ofExisting(
                1L,
                "Updated Title",
                "test-article",
                "Test content",
                "Test excerpt",
                pl.klastbit.lexpage.domain.article.ArticleStatus.PUBLISHED,
                    UserId.createNew(),
                LocalDateTime.now(),
                "Meta Title",
                "Meta Description",
                "https://example.com/image.jpg",
                "https://example.com/article",
                Arrays.asList("java", "spring", "testing"),
                    UserId.createNew(),
                    UserId.createNew(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
            );

            // when
            mapper.updateEntity(entity, domain);

            // then - relationships should not change
            assertThat(entity.getAuthor()).isSameAs(originalAuthor);
            assertThat(entity.getCreatedBy()).isSameAs(originalCreatedBy);
            assertThat(entity.getUpdatedBy()).isSameAs(originalUpdatedBy);
        }
    }

    @Nested
    @DisplayName("User reference extraction methods")
    class UserReferenceExtractionTests {

        @Test
        @DisplayName("getAuthorId() should return author ID when author is set")
        void getAuthorIdShouldReturnAuthorIdWhenAuthorIsSet() {
            // given
            ArticleEntity entity = createFullArticleEntity();

            // when
            UserId result = mapper.getAuthorId(entity);

            // then
            assertThat(result.userid()).isOfAnyClassIn(UUID.class);
        }

        @Test
        @DisplayName("getAuthorId() should return null when author is not set")
        void getAuthorIdShouldReturnNullWhenAuthorIsNotSet() {
            // given
            ArticleEntity entity = createMinimalArticleEntity();
            entity.setAuthor(null);

            // when
            UserId result = mapper.getAuthorId(entity);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("getAuthorId() should return null when entity is null")
        void getAuthorIdShouldReturnNullWhenEntityIsNull() {
            // when
            UserId result = mapper.getAuthorId(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("getCreatedById() should return created by user ID when set")
        void getCreatedByIdShouldReturnCreatedByUserIdWhenSet() {
            // given
            ArticleEntity entity = createFullArticleEntity();

            // when
            UserId result = mapper.getCreatedById(entity);

            // then
            assertThat(result.userid()).isOfAnyClassIn(UUID.class);
        }

        @Test
        @DisplayName("getCreatedById() should return null when createdBy is not set")
        void getCreatedByIdShouldReturnNullWhenCreatedByIsNotSet() {
            // given
            ArticleEntity entity = createMinimalArticleEntity();
            entity.setCreatedBy(null);

            // when
            UserId result = mapper.getCreatedById(entity);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("getUpdatedById() should return null when updatedBy is not set")
        void getUpdatedByIdShouldReturnNullWhenUpdatedByIsNotSet() {
            // given
            ArticleEntity entity = createMinimalArticleEntity();
            entity.setUpdatedBy(null);

            // when
            UserId result = mapper.getUpdatedById(entity);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("setUserReferences() method")
    class SetUserReferencesTests {

        @Test
        @DisplayName("should set all user references when entity is not null")
        void shouldSetAllUserReferencesWhenEntityIsNotNull() {
            // given
            ArticleEntity entity = new ArticleEntity();
            UserEntity author = createUserEntity(UUID.randomUUID(), "author@example.com");
            UserEntity createdBy = createUserEntity(UUID.randomUUID(), "creator@example.com");
            UserEntity updatedBy = createUserEntity(UUID.randomUUID(), "updater@example.com");

            // when
            mapper.setUserReferences(entity, author, createdBy, updatedBy);

            // then
            assertThat(entity.getAuthor()).isSameAs(author);
            assertThat(entity.getCreatedBy()).isSameAs(createdBy);
            assertThat(entity.getUpdatedBy()).isSameAs(updatedBy);
        }

        @Test
        @DisplayName("should do nothing when entity is null")
        void shouldDoNothingWhenEntityIsNull() {
            // given
            UserEntity author = createUserEntity(UUID.randomUUID(), "author@example.com");

            // when/then - no exception should be thrown
            mapper.setUserReferences(null, author, author, author);
        }

        @Test
        @DisplayName("should set null user references")
        void shouldSetNullUserReferences() {
            // given
            ArticleEntity entity = createFullArticleEntity();

            // when
            mapper.setUserReferences(entity, null, null, null);

            // then
            assertThat(entity.getAuthor()).isNull();
            assertThat(entity.getCreatedBy()).isNull();
            assertThat(entity.getUpdatedBy()).isNull();
        }
    }

    // Helper methods to create test objects

    private ArticleEntity createMinimalArticleEntity() {
        ArticleEntity entity = new ArticleEntity();
        entity.setId(1L);
        entity.setTitle("Test Article");
        entity.setSlug("test-article");
        entity.setContent("Test content");
        entity.setStatus(pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleStatus.DRAFT);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private ArticleEntity createFullArticleEntity() {
        ArticleEntity entity = createMinimalArticleEntity();
        entity.setExcerpt("Test excerpt");
        entity.setStatus(pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleStatus.PUBLISHED);
        entity.setPublishedAt(LocalDateTime.now());
        entity.setMetaTitle("Meta Title");
        entity.setMetaDescription("Meta Description");
        entity.setOgImageUrl("https://example.com/image.jpg");
        entity.setCanonicalUrl("https://example.com/article");
        entity.setKeywords(new String[]{"java", "spring", "testing"});

        UserEntity author = createUserEntity(UUID.randomUUID(), "author@example.com");
        UserEntity createdBy = createUserEntity(UUID.randomUUID(), "creator@example.com");
        UserEntity updatedBy = createUserEntity(UUID.randomUUID(), "updater@example.com");

        entity.setAuthor(author);
        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(updatedBy);

        return entity;
    }

    private Article createMinimalArticleDomain() {
        return Article.ofExisting(
            1L,
            "Test Article",
            "test-article",
            "Test content",
            null,
            pl.klastbit.lexpage.domain.article.ArticleStatus.DRAFT,
                UserId.createNew(),
            null,
            null,
            null,
            null,
            null,
            null,
                UserId.createNew(),
                UserId.createNew(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );
    }

    private Article createFullArticleDomain() {
        return Article.ofExisting(
            1L,
            "Test Article",
            "test-article",
            "Test content",
            "Test excerpt",
            pl.klastbit.lexpage.domain.article.ArticleStatus.PUBLISHED,
                UserId.createNew(),
            LocalDateTime.now(),
            "Meta Title",
            "Meta Description",
            "https://example.com/image.jpg",
            "https://example.com/article",
            Arrays.asList("java", "spring", "testing"),
                UserId.createNew(),
                UserId.createNew(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );
    }

    private UserEntity createUserEntity(UUID id, String email) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
