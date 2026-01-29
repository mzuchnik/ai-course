package pl.klastbit.lexpage.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.klastbit.lexpage.application.article.*;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.application.article.dto.ArticleListItemDto;
import pl.klastbit.lexpage.application.article.dto.PageDto;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.security.UserPrincipal;
import pl.klastbit.lexpage.infrastructure.web.controller.dto.*;

import java.util.UUID;

/**
 * REST Controller for Article management API.
 * Inbound adapter (Primary/Driving) in Hexagonal Architecture.
 * Provides 8 endpoints for CRUD operations and status management.
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final CreateArticleUseCase createArticleUseCase;
    private final UpdateArticleUseCase updateArticleUseCase;
    private final DeleteArticleUseCase deleteArticleUseCase;
    private final PublishArticleUseCase publishArticleUseCase;
    private final ArchiveArticleUseCase archiveArticleUseCase;
    private final UnpublishArticleUseCase unpublishArticleUseCase;
    private final GetArticleUseCase getArticleUseCase;
    private final ListArticlesUseCase listArticlesUseCase;

    /**
     * GET /api/articles - Lista artykułów z filtrowaniem, sortowaniem i paginacją.
     *
     * @param page     Numer strony (0-indexed)
     * @param size     Liczba elementów na stronie (max 100)
     * @param status   Filtr po statusie (DRAFT, PUBLISHED, ARCHIVED)
     * @param authorId Filtr po autorze (UUID)
     * @param keyword  Wyszukiwanie full-text w tytule i treści
     * @param sort     Sortowanie: field,direction (np. "createdAt,desc")
     * @return Paginowana lista artykułów
     */
    @GetMapping
    public ResponseEntity<ArticleListResponse> listArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        log.info("GET /api/articles - page: {}, size: {}, status: {}, authorId: {}, keyword: {}",
                page, size, status, authorId, keyword);

        // Validate page size
        if (size > 100) {
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }

        // Parse sorting
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        // Parse status
        ArticleStatus articleStatus = status != null ? ArticleStatus.valueOf(status.toUpperCase()) : null;

        // Parse authorId
        UserId authorUserId = authorId != null ? UserId.of(UUID.fromString(authorId)) : null;

        PageDto<ArticleListItemDto> result = listArticlesUseCase.execute(
                articleStatus, authorUserId, keyword, pageable
        );

        return ResponseEntity.ok(ArticleListResponse.from(result));
    }

    /**
     * GET /api/articles/{id} - Szczegóły pojedynczego artykułu.
     *
     * @param id ID artykułu
     * @return Pełne dane artykułu
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long id) {
        log.info("GET /api/articles/{}", id);

        ArticleDetailDto article = getArticleUseCase.execute(id);

        return ResponseEntity.ok(ArticleResponse.from(article));
    }

    /**
     * POST /api/articles - Tworzenie nowego artykułu w statusie DRAFT.
     *
     * @param request Dane nowego artykułu
     * @return Utworzony artykuł (201 Created)
     */
    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @Valid @RequestBody CreateArticleRequest request
    ) {
        log.info("POST /api/articles - title: {}", request.title());

        UUID userId = getCurrentUserId();

        ArticleDetailDto created = createArticleUseCase.execute(request.toCommand(userId));

        return ResponseEntity.status(HttpStatus.CREATED).body(ArticleResponse.from(created));
    }

    /**
     * PUT /api/articles/{id} - Aktualizacja istniejącego artykułu.
     *
     * @param id      ID artykułu
     * @param request Nowe dane artykułu
     * @return Zaktualizowany artykuł
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateArticleRequest request
    ) {
        log.info("PUT /api/articles/{} - title: {}", id, request.title());

        UUID userId = getCurrentUserId();

        ArticleDetailDto updated = updateArticleUseCase.execute(request.toCommand(id, userId));

        return ResponseEntity.ok(ArticleResponse.from(updated));
    }

    /**
     * DELETE /api/articles/{id} - Soft delete artykułu.
     *
     * @param id ID artykułu
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        log.info("DELETE /api/articles/{}", id);

        deleteArticleUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/articles/{id}/publish - Publikacja artykułu (DRAFT → PUBLISHED).
     *
     * @param id ID artykułu
     * @return Opublikowany artykuł
     */
    @PatchMapping("/{id}/publish")
    public ResponseEntity<ArticleResponse> publishArticle(@PathVariable Long id) {
        log.info("PATCH /api/articles/{}/publish", id);

        ArticleDetailDto published = publishArticleUseCase.execute(id);

        return ResponseEntity.ok(ArticleResponse.from(published));
    }

    /**
     * PATCH /api/articles/{id}/archive - Archiwizacja artykułu (PUBLISHED → ARCHIVED).
     *
     * @param id ID artykułu
     * @return Zarchiwizowany artykuł
     */
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ArticleResponse> archiveArticle(@PathVariable Long id) {
        log.info("PATCH /api/articles/{}/archive", id);

        ArticleDetailDto archived = archiveArticleUseCase.execute(id);

        return ResponseEntity.ok(ArticleResponse.from(archived));
    }

    /**
     * PATCH /api/articles/{id}/unpublish - Cofnięcie publikacji artykułu (PUBLISHED → DRAFT).
     *
     * @param id ID artykułu
     * @return Cofnięty do draftu artykuł
     */
    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<ArticleResponse> unpublishArticle(@PathVariable Long id) {
        log.info("PATCH /api/articles/{}/unpublish", id);

        ArticleDetailDto unpublished = unpublishArticleUseCase.execute(id);

        return ResponseEntity.ok(ArticleResponse.from(unpublished));
    }

    /**
     * Helper method to get currently authenticated user's ID from Spring Security context.
     *
     * @return UUID of the currently authenticated user
     * @throws IllegalStateException if user is not authenticated or authentication is invalid
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No authenticated user found in security context");
            throw new IllegalStateException("User must be authenticated to perform this action");
        }

        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            UUID userId = userPrincipal.getUserId().userid();
            log.debug("Retrieved userId from security context: {}", userId);
            return userId;
        }

        log.error("Authentication principal is not UserPrincipal: {}", authentication.getPrincipal().getClass());
        throw new IllegalStateException("Invalid authentication principal");
    }
}
