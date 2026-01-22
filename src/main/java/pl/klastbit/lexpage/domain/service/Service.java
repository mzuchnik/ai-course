package pl.klastbit.lexpage.domain.service;

import lombok.Getter;
import pl.klastbit.lexpage.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Service Domain Entity (DDD Aggregate Root).
 * Encapsulates legal service business logic including categorization and display ordering.
 */
@Getter
public class Service {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private ServiceCategory category;
    private String scope;
    private String process;
    private List<FaqItem> faqItems;
    private Integer displayOrder;

    // SEO fields
    private String metaTitle;
    private String metaDescription;
    private String ogImageUrl;
    private List<String> keywords;

    // Audit
    private UserId createdBy;
    private UserId updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Service() {}

    /**
     * Factory method to create a new service.
     */
    public static Service create(String name, String description, ServiceCategory category, UserId userId) {
        Service service = new Service();
        service.name = Objects.requireNonNull(name, "Service name cannot be null");
        service.description = Objects.requireNonNull(description, "Description cannot be null");
        service.category = Objects.requireNonNull(category, "Category cannot be null");
        service.displayOrder = 0;
        service.createdAt = LocalDateTime.now();
        service.updatedAt = LocalDateTime.now();
        service.createdBy = userId;
        service.updatedBy = userId;
        return service;
    }

    /**
     * Factory method to reconstruct an existing service from database.
     * Used by infrastructure layer mappers. No business validation applied.
     */
    public static Service ofExisting(
            Long id,
            String name,
            String slug,
            String description,
            ServiceCategory category,
            String scope,
            String process,
            List<FaqItem> faqItems,
            Integer displayOrder,
            String metaTitle,
            String metaDescription,
            String ogImageUrl,
            List<String> keywords,
            UserId createdBy,
            UserId updatedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        Service service = new Service();
        service.id = id;
        service.name = name;
        service.slug = slug;
        service.description = description;
        service.category = category;
        service.scope = scope;
        service.process = process;
        service.faqItems = faqItems;
        service.displayOrder = displayOrder;
        service.metaTitle = metaTitle;
        service.metaDescription = metaDescription;
        service.ogImageUrl = ogImageUrl;
        service.keywords = keywords;
        service.createdBy = createdBy;
        service.updatedBy = updatedBy;
        service.createdAt = createdAt;
        service.updatedAt = updatedAt;
        service.deletedAt = deletedAt;
        return service;
    }

    /**
     * Updates service details.
     */
    public void updateDetails(String name, String description, String scope, String process, UserId userId) {
        this.name = Objects.requireNonNull(name, "Service name cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.scope = scope;
        this.process = process;
        this.updatedBy = userId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates FAQ items.
     */
    public void updateFaq(List<FaqItem> faqItems) {
        this.faqItems = faqItems;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Changes the display order.
     */
    public void changeDisplayOrder(int newOrder) {
        if (newOrder < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }
        this.displayOrder = newOrder;
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
     * Soft deletes the service.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Checks if service is for civil law.
     */
    public boolean isCivilLaw() {
        return category == ServiceCategory.CIVIL_LAW;
    }

    /**
     * Checks if service is for criminal law.
     */
    public boolean isCriminalLaw() {
        return category == ServiceCategory.CRIMINAL_LAW;
    }

    /**
     * Checks if service is deleted (soft delete).
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
