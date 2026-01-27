package pl.klastbit.lexpage.domain.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Optional;

/**
 * Repository port for Article aggregate root.
 * Defines the contract for article persistence operations.
 * Implementation resides in infrastructure layer (Hexagonal Architecture).
 */
public interface ArticleRepository {

    /**
     * Saves an article (create or update).
     *
     * @param article Article to save
     * @return Saved article with generated ID if new
     */
    Article save(Article article);

    /**
     * Finds an article by ID (including soft-deleted).
     *
     * @param id Article ID
     * @return Optional containing the article if found
     */
    Optional<Article> findById(Long id);

    /**
     * Finds a non-deleted article by ID.
     *
     * @param id Article ID
     * @return Optional containing the article if found and not deleted
     */
    Optional<Article> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Finds all articles with pagination (including soft-deleted).
     *
     * @param pageable Pagination parameters
     * @return Page of articles
     */
    Page<Article> findAll(Pageable pageable);

    /**
     * Finds all non-deleted articles with pagination.
     *
     * @param pageable Pagination parameters
     * @return Page of non-deleted articles
     */
    Page<Article> findAllByDeletedAtIsNull(Pageable pageable);

    /**
     * Finds all non-deleted articles by status with pagination.
     *
     * @param status   Article status
     * @param pageable Pagination parameters
     * @return Page of articles matching the status
     */
    Page<Article> findAllByStatusAndDeletedAtIsNull(ArticleStatus status, Pageable pageable);

    /**
     * Finds all non-deleted articles by author with pagination.
     *
     * @param authorId Author user ID
     * @param pageable Pagination parameters
     * @return Page of articles by the author
     */
    Page<Article> findAllByAuthorIdAndDeletedAtIsNull(UserId authorId, Pageable pageable);

    /**
     * Finds all non-deleted articles by status and author with pagination.
     *
     * @param status   Article status
     * @param authorId Author user ID
     * @param pageable Pagination parameters
     * @return Page of articles matching the criteria
     */
    Page<Article> findAllByStatusAndAuthorIdAndDeletedAtIsNull(
            ArticleStatus status,
            UserId authorId,
            Pageable pageable
    );

    /**
     * Searches non-deleted articles by keyword (full-text search in title and content).
     *
     * @param keyword  Search keyword
     * @param pageable Pagination parameters
     * @return Page of articles matching the keyword
     */
    Page<Article> searchByKeywordAndDeletedAtIsNull(String keyword, Pageable pageable);

    /**
     * Deletes an article (hard delete).
     *
     * @param article Article to delete
     */
    void delete(Article article);

    /**
     * Checks if a non-deleted article with the given slug exists.
     *
     * @param slug Article slug
     * @return true if exists, false otherwise
     */
    boolean existsBySlugAndDeletedAtIsNull(String slug);

    /**
     * Finds a non-deleted article by slug and status.
     *
     * @param slug   Article slug
     * @param status Article status
     * @return Optional containing the article if found and matching criteria
     */
    Optional<Article> findBySlugAndStatusAndDeletedAtIsNull(String slug, ArticleStatus status);
}
