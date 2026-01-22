package pl.klastbit.lexpage.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * JPA Entity for articles table.
 * Includes SEO fields, full-text search, audit trail, and soft delete support.
 */
@Entity
@Table(name = "articles")
@SQLDelete(sql = "UPDATE articles SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class ArticleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "excerpt", length = 500)
    private String excerpt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "article_status_enum")
    private ArticleStatus status = ArticleStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_articles_author"))
    private UserEntity author;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // SEO Fields
    @Column(name = "meta_title", length = 60)
    private String metaTitle;

    @Column(name = "meta_description", length = 160)
    private String metaDescription;

    @Column(name = "og_image_url", length = 500)
    private String ogImageUrl;

    @Column(name = "canonical_url", length = 500)
    private String canonicalUrl;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "keywords", columnDefinition = "text[]")
    private String[] keywords;

    // Full-text search vector (managed by database trigger)
    @Column(name = "search_vector", columnDefinition = "tsvector", insertable = false, updatable = false)
    private String searchVector;

    // Audit Trail
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, foreignKey = @ForeignKey(name = "fk_articles_created_by"))
    private UserEntity createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false, foreignKey = @ForeignKey(name = "fk_articles_updated_by"))
    private UserEntity updatedBy;
}
