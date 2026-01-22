package pl.klastbit.lexpage.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

/**
 * JPA Entity for services table.
 * Includes FAQ as JSONB, SEO fields, audit trail, and soft delete support.
 */
@Entity
@Table(name = "services")
@SQLDelete(sql = "UPDATE services SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class ServiceEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, columnDefinition = "service_category_enum")
    private ServiceCategory category;

    @Column(name = "scope", columnDefinition = "TEXT")
    private String scope;

    @Column(name = "process", columnDefinition = "TEXT")
    private String process;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "faq", columnDefinition = "jsonb")
    private String faq;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    // SEO Fields
    @Column(name = "meta_title", length = 60)
    private String metaTitle;

    @Column(name = "meta_description", length = 160)
    private String metaDescription;

    @Column(name = "og_image_url", length = 500)
    private String ogImageUrl;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "keywords", columnDefinition = "text[]")
    private String[] keywords;

    // Full-text search vector (managed by database trigger)
    @Column(name = "search_vector", columnDefinition = "tsvector", insertable = false, updatable = false)
    private String searchVector;

    // Audit Trail
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, foreignKey = @ForeignKey(name = "fk_services_created_by"))
    private UserEntity createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false, foreignKey = @ForeignKey(name = "fk_services_updated_by"))
    private UserEntity updatedBy;
}
