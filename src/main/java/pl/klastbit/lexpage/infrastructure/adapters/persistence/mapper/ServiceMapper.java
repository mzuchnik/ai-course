package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.klastbit.lexpage.domain.service.FaqItem;
import pl.klastbit.lexpage.domain.service.Service;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ServiceEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Mapper between Service domain entity and ServiceEntity persistence entity.
 * Part of the infrastructure layer (Hexagonal Architecture outbound adapter).
 * Handles JSON serialization/deserialization for FAQ items.
 */
@RequiredArgsConstructor
@Slf4j
public class ServiceMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Maps ServiceEntity (JPA) to Service (domain).
     *
     * @param entity JPA entity from database
     * @return Domain entity
     */
    public Service toDomain(ServiceEntity entity) {
        if (entity == null) {
            return null;
        }

        return Service.ofExisting(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getDescription(),
                toDomainCategory(entity.getCategory()),
                entity.getScope(),
                entity.getProcess(),
                jsonToFaqList(entity.getFaq()),
                entity.getDisplayOrder(),
                entity.getMetaTitle(),
                entity.getMetaDescription(),
                entity.getOgImageUrl(),
                arrayToList(entity.getKeywords()),
                getCreatedById(entity),
                getUpdatedById(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    /**
     * Maps Service (domain) to ServiceEntity (JPA).
     *
     * @param domain Domain entity
     * @return JPA entity for database persistence
     */
    public ServiceEntity toEntity(Service domain) {
        if (domain == null) {
            return null;
        }

        ServiceEntity entity = new ServiceEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setSlug(domain.getSlug());
        entity.setDescription(domain.getDescription());
        entity.setCategory(toEntityCategory(domain.getCategory()));
        entity.setScope(domain.getScope());
        entity.setProcess(domain.getProcess());
        entity.setFaq(faqListToJson(domain.getFaqItems()));
        entity.setDisplayOrder(domain.getDisplayOrder());

        // SEO fields
        entity.setMetaTitle(domain.getMetaTitle());
        entity.setMetaDescription(domain.getMetaDescription());
        entity.setOgImageUrl(domain.getOgImageUrl());
        entity.setKeywords(listToArray(domain.getKeywords()));

        // Audit fields
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());

        return entity;
    }

    /**
     * Updates an existing ServiceEntity with data from Service domain entity.
     * Preserves entity relationships (createdBy, updatedBy).
     *
     * @param entity Existing JPA entity to update
     * @param domain Domain entity with new data
     */
    public void updateEntity(ServiceEntity entity, Service domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.setName(domain.getName());
        entity.setSlug(domain.getSlug());
        entity.setDescription(domain.getDescription());
        entity.setCategory(toEntityCategory(domain.getCategory()));
        entity.setScope(domain.getScope());
        entity.setProcess(domain.getProcess());
        entity.setFaq(faqListToJson(domain.getFaqItems()));
        entity.setDisplayOrder(domain.getDisplayOrder());

        // SEO fields
        entity.setMetaTitle(domain.getMetaTitle());
        entity.setMetaDescription(domain.getMetaDescription());
        entity.setOgImageUrl(domain.getOgImageUrl());
        entity.setKeywords(listToArray(domain.getKeywords()));

        // Update timestamps
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());
    }

    /**
     * Extracts createdBy user ID from ServiceEntity.
     *
     * @param entity JPA entity
     * @return Created by user ID or null if not set
     */
    public Long getCreatedById(ServiceEntity entity) {
        return entity != null && entity.getCreatedBy() != null
                ? entity.getCreatedBy().getId()
                : null;
    }

    /**
     * Extracts updatedBy user ID from ServiceEntity.
     *
     * @param entity JPA entity
     * @return Updated by user ID or null if not set
     */
    public Long getUpdatedById(ServiceEntity entity) {
        return entity != null && entity.getUpdatedBy() != null
                ? entity.getUpdatedBy().getId()
                : null;
    }

    /**
     * Sets UserEntity references on ServiceEntity from IDs.
     *
     * @param entity    JPA entity to update
     * @param createdBy Created by user entity
     * @param updatedBy Updated by user entity
     */
    public void setUserReferences(ServiceEntity entity, UserEntity createdBy, UserEntity updatedBy) {
        if (entity == null) {
            return;
        }

        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(updatedBy);
    }

    // Private helper methods

    /**
     * Converts FAQ list to simple JSON string.
     * Simple implementation without Jackson dependency.
     */
    private String faqListToJson(List<FaqItem> faqItems) {
        if (faqItems == null || faqItems.isEmpty()) {
            return null;
        }
        return objectMapper.writeValueAsString(faqItems);
    }

    /**
     * Parses JSON string to FAQ list.
     * Simple implementation without Jackson dependency.
     */
    private List<FaqItem> jsonToFaqList(String faqJson) {
        if (faqJson == null || faqJson.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(faqJson, new TypeReference<List<FaqItem>>() {
            });
        } catch (Exception e) {
            log.warn("Cannot map: %s to List<FaqItem>".formatted(faqJson), e);
            return List.of();
        }
    }


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
     * Converts domain ServiceCategory to entity ServiceCategory.
     */
    private pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ServiceCategory toEntityCategory(
            pl.klastbit.lexpage.domain.service.ServiceCategory domainCategory) {
        if (domainCategory == null) {
            return null;
        }
        return pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ServiceCategory.valueOf(domainCategory.name());
    }

    /**
     * Converts entity ServiceCategory to domain ServiceCategory.
     */
    private pl.klastbit.lexpage.domain.service.ServiceCategory toDomainCategory(
            pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ServiceCategory entityCategory) {
        if (entityCategory == null) {
            return null;
        }
        return pl.klastbit.lexpage.domain.service.ServiceCategory.valueOf(entityCategory.name());
    }
}
