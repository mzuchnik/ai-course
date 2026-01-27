package pl.klastbit.lexpage.infrastructure.adapters.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for ArticleEntity.
 * Works with persistence layer entities, NOT domain entities.
 * Infrastructure layer in Hexagonal Architecture.
 */
public interface SpringDataArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Optional<ArticleEntity> findByIdAndDeletedAtIsNull(Long id);

    Page<ArticleEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Page<ArticleEntity> findAllByStatusAndDeletedAtIsNull(ArticleStatus status, Pageable pageable);

    // Query by author.id (relationship navigation using UUID)
    Page<ArticleEntity> findAllByAuthor_IdAndDeletedAtIsNull(UUID authorId, Pageable pageable);

    Page<ArticleEntity> findAllByStatusAndAuthor_IdAndDeletedAtIsNull(
            ArticleStatus status,
            UUID authorId,
            Pageable pageable
    );

    boolean existsBySlugAndDeletedAtIsNull(String slug);

    /**
     * Full-text search using PostgreSQL's tsvector.
     * Searches in title and content using the search_vector column.
     */
    @Query(value = """
            SELECT a FROM ArticleEntity a
            WHERE a.deletedAt IS NULL
            AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    Page<ArticleEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
