package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.klastbit.lexpage.domain.ai.AIGeneration;
import pl.klastbit.lexpage.domain.ai.GenerationStatus;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.AIGenerationEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AIGenerationMapper Unit Tests")
class AIGenerationMapperTest {

    private AIGenerationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AIGenerationMapper();
    }

    @Nested
    @DisplayName("toDomain() method")
    class ToDomainTests {

        @Test
        @DisplayName("should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            // when
            AIGeneration result = mapper.toDomain(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields from entity to domain")
        void shouldMapAllFieldsFromEntityToDomain() {
            // given
            AIGenerationEntity entity = createFullAIGenerationEntity();

            // when
            AIGeneration result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUserId()).isEqualTo(UserId.of(entity.getUser().getId()));
            assertThat(result.getPrompt()).isEqualTo("Generate article about testing");
            assertThat(result.getKeywords()).isEqualTo("testing, junit, java");
            assertThat(result.getWordCount()).isEqualTo(500);
            assertThat(result.getGeneratedContent()).isEqualTo("Generated content here...");
            assertThat(result.getModel()).isEqualTo("gpt-4");
            assertThat(result.getTokensUsed()).isEqualTo(1000);
            assertThat(result.getGenerationTimeMs()).isEqualTo(5000);
            assertThat(result.getStatus()).isEqualTo(pl.klastbit.lexpage.domain.ai.GenerationStatus.SUCCESS);
            assertThat(result.getErrorMessage()).isNull();
            assertThat(result.getArticleId()).isEqualTo(200L);
            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should map failed generation with error message")
        void shouldMapFailedGenerationWithErrorMessage() {
            // given
            AIGenerationEntity entity = createMinimalAIGenerationEntity();
            entity.setStatus(GenerationStatus.FAILED);
            entity.setErrorMessage("API error occurred");
            entity.setGeneratedContent(null);

            // when
            AIGeneration result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(pl.klastbit.lexpage.domain.ai.GenerationStatus.FAILED);
            assertThat(result.getErrorMessage()).isEqualTo("API error occurred");
            assertThat(result.getGeneratedContent()).isNull();
        }

        @Test
        @DisplayName("should handle null user reference")
        void shouldHandleNullUserReference() {
            // given
            AIGenerationEntity entity = createMinimalAIGenerationEntity();
            entity.setUser(null);

            // when
            AIGeneration result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isNull();
        }

        @Test
        @DisplayName("should handle null article reference")
        void shouldHandleNullArticleReference() {
            // given
            AIGenerationEntity entity = createMinimalAIGenerationEntity();
            entity.setArticle(null);

            // when
            AIGeneration result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getArticleId()).isNull();
        }

        @Test
        @DisplayName("should handle null optional fields")
        void shouldHandleNullOptionalFields() {
            // given
            AIGenerationEntity entity = createMinimalAIGenerationEntity();
            entity.setKeywords(null);
            entity.setTokensUsed(null);
            entity.setGenerationTimeMs(null);
            entity.setErrorMessage(null);
            entity.setArticle(null);

            // when
            AIGeneration result = mapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getKeywords()).isNull();
            assertThat(result.getTokensUsed()).isNull();
            assertThat(result.getGenerationTimeMs()).isNull();
            assertThat(result.getErrorMessage()).isNull();
            assertThat(result.getArticleId()).isNull();
        }

        @Test
        @DisplayName("should convert entity status to domain status")
        void shouldConvertEntityStatusToDomainStatus() {
            // given
            AIGenerationEntity entity = createMinimalAIGenerationEntity();
            entity.setStatus(GenerationStatus.FAILED);

            // when
            AIGeneration result = mapper.toDomain(entity);

            // then
            assertThat(result.getStatus()).isEqualTo(pl.klastbit.lexpage.domain.ai.GenerationStatus.FAILED);
        }

        @Test
        @DisplayName("should handle null status")
        void shouldHandleNullStatus() {
            // given
            AIGenerationEntity entity = createMinimalAIGenerationEntity();
            entity.setStatus(null);

            // when
            AIGeneration result = mapper.toDomain(entity);

            // then
            assertThat(result.getStatus()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity() method")
    class ToEntityTests {

        @Test
        @DisplayName("should return null when domain is null")
        void shouldReturnNullWhenDomainIsNull() {
            // when
            AIGenerationEntity result = mapper.toEntity(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields from domain to entity")
        void shouldMapAllFieldsFromDomainToEntity() {
            // given
            AIGeneration domain = createFullAIGenerationDomain();

            // when
            AIGenerationEntity result = mapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getPrompt()).isEqualTo("Generate article about testing");
            assertThat(result.getKeywords()).isEqualTo("testing, junit, java");
            assertThat(result.getWordCount()).isEqualTo(500);
            assertThat(result.getGeneratedContent()).isEqualTo("Generated content here...");
            assertThat(result.getModel()).isEqualTo("gpt-4");
            assertThat(result.getTokensUsed()).isEqualTo(1000);
            assertThat(result.getGenerationTimeMs()).isEqualTo(5000);
            assertThat(result.getStatus()).isEqualTo(GenerationStatus.SUCCESS);
            assertThat(result.getErrorMessage()).isNull();
            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should not set user and article entity references")
        void shouldNotSetUserAndArticleEntityReferences() {
            // given
            AIGeneration domain = createFullAIGenerationDomain();

            // when
            AIGenerationEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getUser()).isNull();
            assertThat(result.getArticle()).isNull();
        }

        @Test
        @DisplayName("should handle null optional fields")
        void shouldHandleNullOptionalFields() {
            // given
            AIGeneration domain = AIGeneration.ofExisting(
                1L,
                UserId.createNew(),
                "Generate article about testing",
                null,
                500,
                "Generated content here...",
                "gpt-4",
                null,
                null,
                pl.klastbit.lexpage.domain.ai.GenerationStatus.SUCCESS,
                null,
                null,
                LocalDateTime.now()
            );

            // when
            AIGenerationEntity result = mapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getKeywords()).isNull();
            assertThat(result.getTokensUsed()).isNull();
            assertThat(result.getGenerationTimeMs()).isNull();
            assertThat(result.getErrorMessage()).isNull();
        }

        @Test
        @DisplayName("should convert domain status to entity status")
        void shouldConvertDomainStatusToEntityStatus() {
            // given
            AIGeneration domain = AIGeneration.ofExisting(
                1L,
                UserId.createNew(),
                "Generate article about testing",
                null,
                500,
                "Generated content here...",
                "gpt-4",
                null,
                null,
                pl.klastbit.lexpage.domain.ai.GenerationStatus.TIMEOUT,
                null,
                null,
                LocalDateTime.now()
            );

            // when
            AIGenerationEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getStatus()).isEqualTo(GenerationStatus.TIMEOUT);
        }

        @Test
        @DisplayName("should handle null status")
        void shouldHandleNullStatus() {
            // given
            AIGeneration domain = AIGeneration.ofExisting(
                1L,
                UserId.createNew(),
                "Generate article about testing",
                null,
                500,
                "Generated content here...",
                "gpt-4",
                null,
                null,
                null,
                null,
                null,
                LocalDateTime.now()
            );

            // when
            AIGenerationEntity result = mapper.toEntity(domain);

            // then
            assertThat(result.getStatus()).isNull();
        }
    }

    @Nested
    @DisplayName("updateEntity() method")
    class UpdateEntityTests {

        @Test
        @DisplayName("should do nothing when entity is null")
        void shouldDoNothingWhenEntityIsNull() {
            // given
            AIGeneration domain = createFullAIGenerationDomain();

            // when/then - no exception should be thrown
            mapper.updateEntity(null, domain);
        }

        @Test
        @DisplayName("should do nothing when domain is null")
        void shouldDoNothingWhenDomainIsNull() {
            // given
            AIGenerationEntity entity = createFullAIGenerationEntity();

            // when/then - no exception should be thrown
            mapper.updateEntity(entity, null);
        }

        @Test
        @DisplayName("should update all mutable fields from domain to entity")
        void shouldUpdateAllMutableFieldsFromDomainToEntity() {
            // given
            AIGenerationEntity entity = createFullAIGenerationEntity();
            LocalDateTime newCreatedAt = LocalDateTime.now().plusDays(1);
            AIGeneration domain = AIGeneration.ofExisting(
                1L,
                    UserId.createNew(),
                "Updated prompt",
                "updated, keywords",
                1000,
                "Updated content",
                "gpt-4-turbo",
                2000,
                10000,
                pl.klastbit.lexpage.domain.ai.GenerationStatus.FAILED,
                "New error",
                200L,
                newCreatedAt
            );

            // when
            mapper.updateEntity(entity, domain);

            // then
            assertThat(entity.getPrompt()).isEqualTo("Updated prompt");
            assertThat(entity.getKeywords()).isEqualTo("updated, keywords");
            assertThat(entity.getWordCount()).isEqualTo(1000);
            assertThat(entity.getGeneratedContent()).isEqualTo("Updated content");
            assertThat(entity.getModel()).isEqualTo("gpt-4-turbo");
            assertThat(entity.getTokensUsed()).isEqualTo(2000);
            assertThat(entity.getGenerationTimeMs()).isEqualTo(10000);
            assertThat(entity.getStatus()).isEqualTo(GenerationStatus.FAILED);
            assertThat(entity.getErrorMessage()).isEqualTo("New error");
            assertThat(entity.getCreatedAt()).isEqualTo(newCreatedAt);
        }

        @Test
        @DisplayName("should preserve entity relationships during update")
        void shouldPreserveEntityRelationshipsDuringUpdate() {
            // given
            AIGenerationEntity entity = createFullAIGenerationEntity();
            UserEntity originalUser = entity.getUser();
            ArticleEntity originalArticle = entity.getArticle();

            AIGeneration domain = AIGeneration.ofExisting(
                1L,
                UserId.createNew(),
                "Updated prompt",
                "testing, junit, java",
                500,
                "Generated content here...",
                "gpt-4",
                1000,
                5000,
                pl.klastbit.lexpage.domain.ai.GenerationStatus.SUCCESS,
                null,
                200L,
                LocalDateTime.now()
            );

            // when
            mapper.updateEntity(entity, domain);

            // then - relationships should not change
            assertThat(entity.getUser()).isSameAs(originalUser);
            assertThat(entity.getArticle()).isSameAs(originalArticle);
        }
    }

    @Nested
    @DisplayName("Entity reference extraction methods")
    class EntityReferenceExtractionTests {

        @Test
        @DisplayName("getUserId() should return user ID when user is set")
        void getUserIdShouldReturnUserIdWhenUserIsSet() {
            // given
            AIGenerationEntity entity = createFullAIGenerationEntity();

            // when
            UserId result = mapper.getUserId(entity);

            // then
            assertThat(result.userid()).isOfAnyClassIn(UUID.class);
        }

        @Test
        @DisplayName("getUserId() should return null when user is not set")
        void getUserIdShouldReturnNullWhenUserIsNotSet() {
            // given
            AIGenerationEntity entity = createMinimalAIGenerationEntity();
            entity.setUser(null);

            // when
            UserId result = mapper.getUserId(entity);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("getUserId() should return null when entity is null")
        void getUserIdShouldReturnNullWhenEntityIsNull() {
            // when
            UserId result = mapper.getUserId(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("getArticleId() should return article ID when article is set")
        void getArticleIdShouldReturnArticleIdWhenArticleIsSet() {
            // given
            AIGenerationEntity entity = createFullAIGenerationEntity();

            // when
            Long result = mapper.getArticleId(entity);

            // then
            assertThat(result).isEqualTo(200L);
        }

        @Test
        @DisplayName("getArticleId() should return null when article is not set")
        void getArticleIdShouldReturnNullWhenArticleIsNotSet() {
            // given
            AIGenerationEntity entity = createMinimalAIGenerationEntity();
            entity.setArticle(null);

            // when
            Long result = mapper.getArticleId(entity);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("getArticleId() should return null when entity is null")
        void getArticleIdShouldReturnNullWhenEntityIsNull() {
            // when
            Long result = mapper.getArticleId(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("setUserReference() method")
    class SetUserReferenceTests {

        @Test
        @DisplayName("should set user reference when entity is not null")
        void shouldSetUserReferenceWhenEntityIsNotNull() {
            // given
            AIGenerationEntity entity = new AIGenerationEntity();
            UserEntity user = createUserEntity(UUID.randomUUID(), "user@example.com");

            // when
            mapper.setUserReference(entity, user);

            // then
            assertThat(entity.getUser()).isSameAs(user);
        }

        @Test
        @DisplayName("should do nothing when entity is null")
        void shouldDoNothingWhenEntityIsNull() {
            // given
            UserEntity user = createUserEntity(UUID.randomUUID(), "user@example.com");

            // when/then - no exception should be thrown
            mapper.setUserReference(null, user);
        }

        @Test
        @DisplayName("should set null user reference")
        void shouldSetNullUserReference() {
            // given
            AIGenerationEntity entity = createFullAIGenerationEntity();

            // when
            mapper.setUserReference(entity, null);

            // then
            assertThat(entity.getUser()).isNull();
        }
    }

    @Nested
    @DisplayName("setArticleReference() method")
    class SetArticleReferenceTests {

        @Test
        @DisplayName("should set article reference when entity is not null")
        void shouldSetArticleReferenceWhenEntityIsNotNull() {
            // given
            AIGenerationEntity entity = new AIGenerationEntity();
            ArticleEntity article = createArticleEntity(200L);

            // when
            mapper.setArticleReference(entity, article);

            // then
            assertThat(entity.getArticle()).isSameAs(article);
        }

        @Test
        @DisplayName("should do nothing when entity is null")
        void shouldDoNothingWhenEntityIsNull() {
            // given
            ArticleEntity article = createArticleEntity(200L);

            // when/then - no exception should be thrown
            mapper.setArticleReference(null, article);
        }

        @Test
        @DisplayName("should set null article reference")
        void shouldSetNullArticleReference() {
            // given
            AIGenerationEntity entity = createFullAIGenerationEntity();

            // when
            mapper.setArticleReference(entity, null);

            // then
            assertThat(entity.getArticle()).isNull();
        }
    }

    @Nested
    @DisplayName("Round-trip conversion")
    class RoundTripConversionTests {

        @Test
        @DisplayName("should maintain data integrity in entity -> domain -> entity conversion")
        void shouldMaintainDataIntegrityInEntityToDomainToEntityConversion() {
            // given
            AIGenerationEntity originalEntity = createFullAIGenerationEntity();

            // when
            AIGeneration domain = mapper.toDomain(originalEntity);
            AIGenerationEntity resultEntity = mapper.toEntity(domain);

            // then
            assertThat(resultEntity.getId()).isEqualTo(originalEntity.getId());
            assertThat(resultEntity.getPrompt()).isEqualTo(originalEntity.getPrompt());
            assertThat(resultEntity.getKeywords()).isEqualTo(originalEntity.getKeywords());
            assertThat(resultEntity.getWordCount()).isEqualTo(originalEntity.getWordCount());
            assertThat(resultEntity.getGeneratedContent()).isEqualTo(originalEntity.getGeneratedContent());
            assertThat(resultEntity.getModel()).isEqualTo(originalEntity.getModel());
            assertThat(resultEntity.getTokensUsed()).isEqualTo(originalEntity.getTokensUsed());
            assertThat(resultEntity.getGenerationTimeMs()).isEqualTo(originalEntity.getGenerationTimeMs());
            assertThat(resultEntity.getStatus()).isEqualTo(originalEntity.getStatus());
            assertThat(resultEntity.getErrorMessage()).isEqualTo(originalEntity.getErrorMessage());
        }

        @Test
        @DisplayName("should maintain data integrity in domain -> entity -> domain conversion")
        void shouldMaintainDataIntegrityInDomainToEntityToDomainConversion() {
            // given
            AIGeneration originalDomain = createFullAIGenerationDomain();

            // when
            AIGenerationEntity entity = mapper.toEntity(originalDomain);
            entity.setUser(createUserEntity(originalDomain.getUserId().userid(), "user@example.com"));
            entity.setArticle(createArticleEntity(originalDomain.getArticleId()));
            AIGeneration resultDomain = mapper.toDomain(entity);

            // then
            assertThat(resultDomain.getId()).isEqualTo(originalDomain.getId());
            assertThat(resultDomain.getUserId()).isEqualTo(originalDomain.getUserId());
            assertThat(resultDomain.getPrompt()).isEqualTo(originalDomain.getPrompt());
            assertThat(resultDomain.getKeywords()).isEqualTo(originalDomain.getKeywords());
            assertThat(resultDomain.getWordCount()).isEqualTo(originalDomain.getWordCount());
            assertThat(resultDomain.getGeneratedContent()).isEqualTo(originalDomain.getGeneratedContent());
            assertThat(resultDomain.getModel()).isEqualTo(originalDomain.getModel());
            assertThat(resultDomain.getTokensUsed()).isEqualTo(originalDomain.getTokensUsed());
            assertThat(resultDomain.getGenerationTimeMs()).isEqualTo(originalDomain.getGenerationTimeMs());
            assertThat(resultDomain.getStatus()).isEqualTo(originalDomain.getStatus());
            assertThat(resultDomain.getErrorMessage()).isEqualTo(originalDomain.getErrorMessage());
            assertThat(resultDomain.getArticleId()).isEqualTo(originalDomain.getArticleId());
        }
    }

    // Helper methods to create test objects

    private AIGenerationEntity createMinimalAIGenerationEntity() {
        AIGenerationEntity entity = new AIGenerationEntity();
        entity.setId(1L);
        entity.setPrompt("Generate article about testing");
        entity.setWordCount(500);
        entity.setGeneratedContent("Generated content here...");
        entity.setModel("gpt-4");
        entity.setStatus(GenerationStatus.SUCCESS);
        entity.setCreatedAt(LocalDateTime.now());

        UserEntity user = createUserEntity(UUID.randomUUID(), "user@example.com");
        entity.setUser(user);

        return entity;
    }

    private AIGenerationEntity createFullAIGenerationEntity() {
        AIGenerationEntity entity = createMinimalAIGenerationEntity();
        entity.setKeywords("testing, junit, java");
        entity.setTokensUsed(1000);
        entity.setGenerationTimeMs(5000);

        ArticleEntity article = createArticleEntity(200L);
        entity.setArticle(article);

        return entity;
    }

    private AIGeneration createMinimalAIGenerationDomain() {
        return AIGeneration.ofExisting(
            1L,
            UserId.createNew(),
            "Generate article about testing",
            null,
            500,
            "Generated content here...",
            "gpt-4",
            null,
            null,
            pl.klastbit.lexpage.domain.ai.GenerationStatus.SUCCESS,
            null,
            null,
            LocalDateTime.now()
        );
    }

    private AIGeneration createFullAIGenerationDomain() {
        return AIGeneration.ofExisting(
            1L,
            UserId.createNew(),
            "Generate article about testing",
            "testing, junit, java",
            500,
            "Generated content here...",
            "gpt-4",
            1000,
            5000,
            pl.klastbit.lexpage.domain.ai.GenerationStatus.SUCCESS,
            null,
            200L,
            LocalDateTime.now()
        );
    }

    private UserEntity createUserEntity(UUID id, String email) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    private ArticleEntity createArticleEntity(Long id) {
        ArticleEntity article = new ArticleEntity();
        article.setId(id);
        return article;
    }
}
