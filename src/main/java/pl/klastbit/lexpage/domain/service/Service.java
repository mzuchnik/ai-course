package pl.klastbit.lexpage.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Service Domain Entity (DDD Aggregate Root).
 * Encapsulates legal service business logic including categorization and display ordering.
 */
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
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Service() {}

    /**
     * Factory method to create a new service.
     */
    public static Service create(String name, String description, ServiceCategory category, Long userId) {
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
            Long createdBy,
            Long updatedBy,
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
    public void updateDetails(String name, String description, String scope, String process, Long userId) {
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

    // Getters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public String getScope() {
        return scope;
    }

    public String getProcess() {
        return process;
    }

    public List<FaqItem> getFaqItems() {
        return faqItems;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
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

    public void setOgImageUrl(String ogImageUrl) {
        this.ogImageUrl = ogImageUrl;
    }

    // Package-private setters for reconstruction
    void setCategory(ServiceCategory category) {
        this.category = category;
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
