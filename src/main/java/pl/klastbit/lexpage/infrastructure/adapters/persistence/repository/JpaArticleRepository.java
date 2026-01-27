package pl.klastbit.lexpage.infrastructure.adapters.persistence.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper.ArticleMapper;

import java.util.Optional;

/**
 * JPA implementation of ArticleRepository port.
 * Adapter that bridges domain layer with persistence layer using ArticleMapper.
 * Infrastructure layer in Hexagonal Architecture (Outbound Adapter).
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaArticleRepository implements ArticleRepository {

    private final SpringDataArticleRepository springDataRepository;
    private final SpringDataUserRepository userRepository;
    private final ArticleMapper articleMapper;

    @Override
    public Article save(Article article) {
        log.debug("Saving article: {}", article.getId());

        ArticleEntity entity;

        if (article.getId() == null) {
            // New article - create new entity
            entity = articleMapper.toEntity(article);
            setUserReferencesFromDomain(entity, article);
        } else {
            // Update existing article
            entity = springDataRepository.findById(article.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Article not found: " + article.getId()));
            articleMapper.updateEntity(entity, article);
            // Update only updatedBy reference
            setUpdatedByReference(entity, article.getUpdatedBy());
        }

        ArticleEntity savedEntity = springDataRepository.save(entity);
        return articleMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Article> findById(Long id) {
        log.debug("Finding article by id: {}", id);
        return springDataRepository.findById(id)
                .map(articleMapper::toDomain);
    }

    @Override
    public Optional<Article> findByIdAndDeletedAtIsNull(Long id) {
        log.debug("Finding non-deleted article by id: {}", id);
        return springDataRepository.findByIdAndDeletedAtIsNull(id)
                .map(articleMapper::toDomain);
    }

    @Override
    public Page<Article> findAll(Pageable pageable) {
        log.debug("Finding all articles with pageable: {}", pageable);
        return springDataRepository.findAll(pageable)
                .map(articleMapper::toDomain);
    }

    @Override
    public Page<Article> findAllByDeletedAtIsNull(Pageable pageable) {
        log.debug("Finding all non-deleted articles with pageable: {}", pageable);
        return springDataRepository.findAllByDeletedAtIsNull(pageable)
                .map(articleMapper::toDomain);
    }

    @Override
    public Page<Article> findAllByStatusAndDeletedAtIsNull(ArticleStatus status, Pageable pageable) {
        log.debug("Finding articles by status: {} with pageable: {}", status, pageable);
        return springDataRepository.findAllByStatusAndDeletedAtIsNull(status, pageable)
                .map(articleMapper::toDomain);
    }

    @Override
    public Page<Article> findAllByAuthorIdAndDeletedAtIsNull(UserId authorId, Pageable pageable) {
        log.debug("Finding articles by authorId: {} with pageable: {}", authorId, pageable);
        // Note: SpringDataArticleRepository uses UserEntity, so we need to pass UUID
        return springDataRepository.findAllByAuthor_IdAndDeletedAtIsNull(
                        authorId.userid(), pageable
                )
                .map(articleMapper::toDomain);
    }

    @Override
    public Page<Article> findAllByStatusAndAuthorIdAndDeletedAtIsNull(
            ArticleStatus status,
            UserId authorId,
            Pageable pageable
    ) {
        log.debug("Finding articles by status: {} and authorId: {} with pageable: {}",
                status, authorId, pageable);
        return springDataRepository.findAllByStatusAndAuthor_IdAndDeletedAtIsNull(
                        status, authorId.userid(), pageable
                )
                .map(articleMapper::toDomain);
    }

    @Override
    public Page<Article> searchByKeywordAndDeletedAtIsNull(String keyword, Pageable pageable) {
        log.debug("Searching articles by keyword: {} with pageable: {}", keyword, pageable);
        return springDataRepository.searchByKeyword(keyword, pageable)
                .map(articleMapper::toDomain);
    }

    @Override
    public void delete(Article article) {
        log.debug("Deleting article: {}", article.getId());
        ArticleEntity entity = springDataRepository.findById(article.getId())
                .orElseThrow(() -> new IllegalArgumentException("Article not found: " + article.getId()));
        springDataRepository.delete(entity);
    }

    @Override
    public boolean existsBySlugAndDeletedAtIsNull(String slug) {
        log.debug("Checking if slug exists: {}", slug);
        return springDataRepository.existsBySlugAndDeletedAtIsNull(slug);
    }

    @Override
    public Optional<Article> findBySlugAndStatusAndDeletedAtIsNull(String slug, ArticleStatus status) {
        log.debug("Finding article by slug: {} and status: {}", slug, status);
        return springDataRepository.findBySlugAndStatusAndDeletedAtIsNull(slug, status)
                .map(articleMapper::toDomain);
    }

    // ==================== Private Helper Methods ====================

    /**
     * Sets UserEntity references on ArticleEntity from domain Article.
     */
    private void setUserReferencesFromDomain(ArticleEntity entity, Article domain) {
        UserEntity author = loadUserEntity(domain.getAuthorId());
        UserEntity createdBy = loadUserEntity(domain.getCreatedBy());
        UserEntity updatedBy = loadUserEntity(domain.getUpdatedBy());

        articleMapper.setUserReferences(entity, author, createdBy, updatedBy);
    }

    /**
     * Updates only updatedBy reference on ArticleEntity.
     */
    private void setUpdatedByReference(ArticleEntity entity, UserId updatedById) {
        UserEntity updatedBy = loadUserEntity(updatedById);
        entity.setUpdatedBy(updatedBy);
    }

    /**
     * Loads UserEntity by UserId.
     */
    private UserEntity loadUserEntity(UserId userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId.userid())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId.userid()));
    }
}
