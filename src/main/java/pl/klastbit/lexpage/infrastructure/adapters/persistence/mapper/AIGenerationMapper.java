package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.klastbit.lexpage.domain.ai.AIGeneration;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.AIGenerationEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;

/**
 * Mapper between AIGeneration domain entity and AIGenerationEntity persistence entity.
 * Part of the infrastructure layer (Hexagonal Architecture outbound adapter).
 */
@RequiredArgsConstructor
@Slf4j
public class AIGenerationMapper {

    /**
     * Maps AIGenerationEntity (JPA) to AIGeneration (domain).
     *
     * @param entity JPA entity from database
     * @return Domain entity
     */
    public AIGeneration toDomain(AIGenerationEntity entity) {
        if (entity == null) {
            return null;
        }

        return AIGeneration.ofExisting(
            entity.getId(),
            getUserId(entity),
            entity.getPrompt(),
            entity.getKeywords(),
            entity.getWordCount(),
            entity.getGeneratedContent(),
            entity.getModel(),
            entity.getTokensUsed(),
            entity.getGenerationTimeMs(),
            entity.getStatus(),
            entity.getErrorMessage(),
            getArticleId(entity),
            entity.getCreatedAt()
        );
    }

    /**
     * Maps AIGeneration (domain) to AIGenerationEntity (JPA).
     *
     * @param domain Domain entity
     * @return JPA entity for database persistence
     */
    public AIGenerationEntity toEntity(AIGeneration domain) {
        if (domain == null) {
            return null;
        }

        AIGenerationEntity entity = new AIGenerationEntity();
        entity.setId(domain.getId());
        entity.setPrompt(domain.getPrompt());
        entity.setKeywords(domain.getKeywords());
        entity.setWordCount(domain.getWordCount());
        entity.setGeneratedContent(domain.getGeneratedContent());
        entity.setModel(domain.getModel());
        entity.setTokensUsed(domain.getTokensUsed());
        entity.setGenerationTimeMs(domain.getGenerationTimeMs());
        entity.setStatus(domain.getStatus());
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setCreatedAt(domain.getCreatedAt());

        // Note: UserEntity and ArticleEntity references need to be set separately
        // by the repository or service layer

        return entity;
    }

    /**
     * Updates an existing AIGenerationEntity with data from AIGeneration domain entity.
     * Preserves entity relationships (user, article).
     *
     * @param entity Existing JPA entity to update
     * @param domain Domain entity with new data
     */
    public void updateEntity(AIGenerationEntity entity, AIGeneration domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.setPrompt(domain.getPrompt());
        entity.setKeywords(domain.getKeywords());
        entity.setWordCount(domain.getWordCount());
        entity.setGeneratedContent(domain.getGeneratedContent());
        entity.setModel(domain.getModel());
        entity.setTokensUsed(domain.getTokensUsed());
        entity.setGenerationTimeMs(domain.getGenerationTimeMs());
        entity.setStatus(domain.getStatus());
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setCreatedAt(domain.getCreatedAt());
    }

    /**
     * Extracts user ID from AIGenerationEntity.
     *
     * @param entity JPA entity
     * @return User ID or null if not set
     */
    public UserId getUserId(AIGenerationEntity entity) {
        return entity != null && entity.getUser() != null
            ? UserId.of(entity.getUser().getId())
            : null;
    }

    /**
     * Extracts article ID from AIGenerationEntity.
     *
     * @param entity JPA entity
     * @return Article ID or null if not set
     */
    public Long getArticleId(AIGenerationEntity entity) {
        return entity != null && entity.getArticle() != null
            ? entity.getArticle().getId()
            : null;
    }

    /**
     * Sets UserEntity reference on AIGenerationEntity.
     *
     * @param entity JPA entity to update
     * @param user User entity
     */
    public void setUserReference(AIGenerationEntity entity, UserEntity user) {
        if (entity == null) {
            return;
        }
        entity.setUser(user);
    }

    /**
     * Sets ArticleEntity reference on AIGenerationEntity.
     *
     * @param entity JPA entity to update
     * @param article Article entity
     */
    public void setArticleReference(AIGenerationEntity entity, ArticleEntity article) {
        if (entity == null) {
            return;
        }
        entity.setArticle(article);
    }

    // Private helper methods for enum conversion

}
