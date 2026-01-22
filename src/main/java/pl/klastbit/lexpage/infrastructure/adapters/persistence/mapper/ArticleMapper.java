package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Mapper between Article domain entity and ArticleEntity persistence entity.
 * Part of the infrastructure layer (Hexagonal Architecture outbound adapter).
 */
@RequiredArgsConstructor
@Slf4j
public class ArticleMapper {

    /**
     * Maps ArticleEntity (JPA) to Article (domain).
     *
     * @param entity JPA entity from database
     * @return Domain entity
     */
    public Article toDomain(ArticleEntity entity) {
        if (entity == null) {
            return null;
        }

        return Article.ofExisting(
            entity.getId(),
            entity.getTitle(),
            entity.getSlug(),
            entity.getContent(),
            entity.getExcerpt(),
            toDomainStatus(entity.getStatus()),
            getAuthorId(entity),
            entity.getPublishedAt(),
            entity.getMetaTitle(),
            entity.getMetaDescription(),
            entity.getOgImageUrl(),
            entity.getCanonicalUrl(),
            arrayToList(entity.getKeywords()),
            getCreatedById(entity),
            getUpdatedById(entity),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt()
        );
    }

    /**
     * Maps Article (domain) to ArticleEntity (JPA).
     *
     * @param domain Domain entity
     * @return JPA entity for database persistence
     */
    public ArticleEntity toEntity(Article domain) {
        if (domain == null) {
            return null;
        }

        ArticleEntity entity = new ArticleEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setSlug(domain.getSlug());
        entity.setContent(domain.getContent());
        entity.setExcerpt(domain.getExcerpt());
        entity.setStatus(toEntityStatus(domain.getStatus()));
        entity.setPublishedAt(domain.getPublishedAt());

        // SEO fields
        entity.setMetaTitle(domain.getMetaTitle());
        entity.setMetaDescription(domain.getMetaDescription());
        entity.setOgImageUrl(domain.getOgImageUrl());
        entity.setCanonicalUrl(domain.getCanonicalUrl());
        entity.setKeywords(listToArray(domain.getKeywords()));

        // Audit fields - note: UserEntity references need to be set separately
        // by the repository or service layer, as we only have IDs in domain
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());

        return entity;
    }

    /**
     * Updates an existing ArticleEntity with data from Article domain entity.
     * Preserves entity relationships (author, createdBy, updatedBy).
     *
     * @param entity Existing JPA entity to update
     * @param domain Domain entity with new data
     */
    public void updateEntity(ArticleEntity entity, Article domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.setTitle(domain.getTitle());
        entity.setSlug(domain.getSlug());
        entity.setContent(domain.getContent());
        entity.setExcerpt(domain.getExcerpt());
        entity.setStatus(toEntityStatus(domain.getStatus()));
        entity.setPublishedAt(domain.getPublishedAt());

        // SEO fields
        entity.setMetaTitle(domain.getMetaTitle());
        entity.setMetaDescription(domain.getMetaDescription());
        entity.setOgImageUrl(domain.getOgImageUrl());
        entity.setCanonicalUrl(domain.getCanonicalUrl());
        entity.setKeywords(listToArray(domain.getKeywords()));

        // Update timestamps
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());
    }

    /**
     * Extracts author ID from ArticleEntity.
     *
     * @param entity JPA entity
     * @return Author ID or null if not set
     */
    public Long getAuthorId(ArticleEntity entity) {
        return entity != null && entity.getAuthor() != null
            ? entity.getAuthor().getId()
            : null;
    }

    /**
     * Extracts createdBy user ID from ArticleEntity.
     *
     * @param entity JPA entity
     * @return Created by user ID or null if not set
     */
    public Long getCreatedById(ArticleEntity entity) {
        return entity != null && entity.getCreatedBy() != null
            ? entity.getCreatedBy().getId()
            : null;
    }

    /**
     * Extracts updatedBy user ID from ArticleEntity.
     *
     * @param entity JPA entity
     * @return Updated by user ID or null if not set
     */
    public Long getUpdatedById(ArticleEntity entity) {
        return entity != null && entity.getUpdatedBy() != null
            ? entity.getUpdatedBy().getId()
            : null;
    }

    /**
     * Sets UserEntity references on ArticleEntity from IDs.
     * This method should be called by repository with loaded UserEntity references.
     *
     * @param entity JPA entity to update
     * @param author Author user entity
     * @param createdBy Created by user entity
     * @param updatedBy Updated by user entity
     */
    public void setUserReferences(ArticleEntity entity, UserEntity author,
                                   UserEntity createdBy, UserEntity updatedBy) {
        if (entity == null) {
            return;
        }

        entity.setAuthor(author);
        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(updatedBy);
    }

    // Private helper methods

    private String[] listToArray(List<String> list) {
        if (list == null) {
            return null;
        }
        return list.toArray(new String[0]);
    }

    private List<String> arrayToList(String[] array) {
        if (array == null) {
            return null;
        }
        return Arrays.asList(array);
    }

    /**
     * Converts domain ArticleStatus to entity ArticleStatus.
     */
    private pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleStatus toEntityStatus(
            pl.klastbit.lexpage.domain.article.ArticleStatus domainStatus) {
        if (domainStatus == null) {
            return null;
        }
        return pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleStatus.valueOf(domainStatus.name());
    }

    /**
     * Converts entity ArticleStatus to domain ArticleStatus.
     */
    private pl.klastbit.lexpage.domain.article.ArticleStatus toDomainStatus(
            pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleStatus entityStatus) {
        if (entityStatus == null) {
            return null;
        }
        return pl.klastbit.lexpage.domain.article.ArticleStatus.valueOf(entityStatus.name());
    }
}
