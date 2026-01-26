# Blog Management API - Plan Implementacji

## 1. Przegląd Funkcjonalności

API do zarządzania artykułami blogowymi (backend-only MVP) z następującymi funkcjami:

- **Pobieranie listy artykułów** - z paginacją, sortowaniem i filtrowaniem
- **Pobieranie szczegółów artykułu** - pojedynczy artykuł po ID
- **Tworzenie artykułu** - nowy artykuł w statusie DRAFT
- **Edycja artykułu** - aktualizacja treści i metadanych
- **Soft delete artykułu** - usunięcie logiczne (deleted_at)
- **Publikacja artykułu** - zmiana statusu DRAFT → PUBLISHED
- **Archiwizacja artykułu** - zmiana statusu PUBLISHED → ARCHIVED

## 2. Endpointy API

### 2.1. GET /api/articles - Lista artykułów

**Opis**: Pobiera paginowaną listę artykułów z możliwością filtrowania i sortowania.

**Query Parameters**:
- `page` (int, default: 0) - numer strony (0-indexed)
- `size` (int, default: 10, max: 100) - liczba elementów na stronie
- `status` (string, optional) - filtrowanie po statusie: DRAFT, PUBLISHED, ARCHIVED
- `authorId` (long, optional) - filtrowanie po autorze
- `keyword` (string, optional) - wyszukiwanie full-text w tytule i treści
- `sort` (string, default: "createdAt,desc") - sortowanie: `field,direction`
  - Możliwe pola: `createdAt`, `updatedAt`, `publishedAt`, `title`
  - Kierunki: `asc`, `desc`

**Response 200 OK**:
```json
{
  "content": [
    {
      "id": 1,
      "title": "Jak napisać pozew o zapłatę?",
      "slug": "jak-napisac-pozew-o-zaplate",
      "excerpt": "Krótki opis artykułu do wyświetlenia na liście...",
      "status": "PUBLISHED",
      "authorId": 1,
      "authorName": "Jan Kowalski",
      "publishedAt": "2026-01-20T10:30:00Z",
      "createdAt": "2026-01-19T15:00:00Z",
      "updatedAt": "2026-01-20T10:30:00Z"
    }
  ],
  "page": {
    "number": 0,
    "size": 10,
    "totalElements": 25,
    "totalPages": 3
  }
}
```

**Response 400 Bad Request** - nieprawidłowe parametry (np. size > 100)

---

### 2.2. GET /api/articles/{id} - Szczegóły artykułu

**Opis**: Pobiera pełne dane pojedynczego artykułu.

**Path Parameters**:
- `id` (long) - ID artykułu

**Response 200 OK**:
```json
{
  "id": 1,
  "title": "Jak napisać pozew o zapłatę?",
  "slug": "jak-napisac-pozew-o-zaplate",
  "content": "<h2>Wprowadzenie</h2><p>Pozew o zapłatę...</p>",
  "excerpt": "Krótki opis artykułu...",
  "status": "PUBLISHED",
  "authorId": 1,
  "authorName": "Jan Kowalski",
  "publishedAt": "2026-01-20T10:30:00Z",
  "metaTitle": "Jak napisać pozew o zapłatę? - Poradnik prawny",
  "metaDescription": "Kompleksowy przewodnik jak prawidłowo napisać pozew o zapłatę...",
  "ogImageUrl": "https://example.com/images/pozew-o-zaplate.jpg",
  "canonicalUrl": "https://example.com/blog/jak-napisac-pozew-o-zaplate",
  "keywords": ["pozew", "zapłata", "prawo cywilne"],
  "createdBy": 1,
  "createdByName": "Jan Kowalski",
  "updatedBy": 1,
  "updatedByName": "Jan Kowalski",
  "createdAt": "2026-01-19T15:00:00Z",
  "updatedAt": "2026-01-20T10:30:00Z"
}
```

**Response 404 Not Found** - artykuł nie istnieje lub jest soft-deleted

---

### 2.3. POST /api/articles - Tworzenie artykułu

**Opis**: Tworzy nowy artykuł w statusie DRAFT.

**Request Body**:
```json
{
  "title": "Jak napisać pozew o zapłatę?",
  "content": "<h2>Wprowadzenie</h2><p>Pozew o zapłatę...</p>",
  "excerpt": "Krótki opis artykułu...",
  "metaTitle": "Jak napisać pozew o zapłatę? - Poradnik prawny",
  "metaDescription": "Kompleksowy przewodnik jak prawidłowo napisać pozew o zapłatę...",
  "ogImageUrl": "https://example.com/images/pozew-o-zaplate.jpg",
  "canonicalUrl": "https://example.com/blog/jak-napisac-pozew-o-zaplate",
  "keywords": ["pozew", "zapłata", "prawo cywilne"]
}
```

**Walidacja**:
- `title` - **WYMAGANE**, 1-255 znaków, nie może być puste
- `content` - **WYMAGANE**, 50-25000 znaków (limit 5000 słów ≈ 25000 znaków)
- `excerpt` - opcjonalne, max 500 znaków
- `metaTitle` - opcjonalne, max 60 znaków
- `metaDescription` - opcjonalne, max 160 znaków
- `ogImageUrl` - opcjonalne, max 500 znaków, musi być poprawnym URL
- `canonicalUrl` - opcjonalne, max 500 znaków, musi być poprawnym URL
- `keywords` - opcjonalne, max 10 elementów, każdy max 50 znaków

**Response 201 Created**:
```json
{
  "id": 1,
  "title": "Jak napisać pozew o zapłatę?",
  "slug": "jak-napisac-pozew-o-zaplate",
  "content": "<h2>Wprowadzenie</h2><p>Pozew o zapłatę...</p>",
  "excerpt": "Krótki opis artykułu...",
  "status": "DRAFT",
  "authorId": 1,
  "authorName": "Jan Kowalski",
  "publishedAt": null,
  "metaTitle": "Jak napisać pozew o zapłatę? - Poradnik prawny",
  "metaDescription": "Kompleksowy przewodnik jak prawidłowo napisać pozew o zapłatę...",
  "ogImageUrl": "https://example.com/images/pozew-o-zaplate.jpg",
  "canonicalUrl": "https://example.com/blog/jak-napisac-pozew-o-zaplate",
  "keywords": ["pozew", "zapłata", "prawo cywilne"],
  "createdBy": 1,
  "createdByName": "Jan Kowalski",
  "updatedBy": 1,
  "updatedByName": "Jan Kowalski",
  "createdAt": "2026-01-26T14:30:00Z",
  "updatedAt": "2026-01-26T14:30:00Z"
}
```

**Response 400 Bad Request** - błędy walidacji (MethodArgumentNotValidException)

**Uwagi**:
- `slug` generowany automatycznie z `title` (transliteracja PL → ASCII, lowercase, `-` zamiast spacji)
- `status` domyślnie ustawiony na `DRAFT`
- `authorId`, `createdBy`, `updatedBy` ustawiane automatycznie z kontekstu zalogowanego użytkownika (Spring Security)
- Auto-generowanie `metaDescription` jeśli nie podano (pierwsze 160 znaków content bez HTML tags)

---

### 2.4. PUT /api/articles/{id} - Edycja artykułu

**Opis**: Aktualizuje istniejący artykuł (treść i metadane). Status pozostaje bez zmian.

**Path Parameters**:
- `id` (long) - ID artykułu

**Request Body**:
```json
{
  "title": "Jak napisać pozew o zapłatę? [ZAKTUALIZOWANE]",
  "content": "<h2>Wprowadzenie</h2><p>Zaktualizowana treść...</p>",
  "excerpt": "Zaktualizowany opis...",
  "metaTitle": "Jak napisać pozew o zapłatę? - Poradnik prawny 2026",
  "metaDescription": "Najnowszy przewodnik...",
  "ogImageUrl": "https://example.com/images/pozew-o-zaplate-2026.jpg",
  "canonicalUrl": "https://example.com/blog/jak-napisac-pozew-o-zaplate",
  "keywords": ["pozew", "zapłata", "prawo cywilne", "2026"]
}
```

**Walidacja**: Taka sama jak w POST /api/articles

**Response 200 OK**:
```json
{
  "id": 1,
  "title": "Jak napisać pozew o zapłatę? [ZAKTUALIZOWANE]",
  "slug": "jak-napisac-pozew-o-zaplate-zaktualizowane",
  "content": "<h2>Wprowadzenie</h2><p>Zaktualizowana treść...</p>",
  "excerpt": "Zaktualizowany opis...",
  "status": "DRAFT",
  "authorId": 1,
  "authorName": "Jan Kowalski",
  "publishedAt": null,
  "metaTitle": "Jak napisać pozew o zapłatę? - Poradnik prawny 2026",
  "metaDescription": "Najnowszy przewodnik...",
  "ogImageUrl": "https://example.com/images/pozew-o-zaplate-2026.jpg",
  "canonicalUrl": "https://example.com/blog/jak-napisac-pozew-o-zaplate",
  "keywords": ["pozew", "zapłata", "prawo cywilne", "2026"],
  "createdBy": 1,
  "createdByName": "Jan Kowalski",
  "updatedBy": 1,
  "updatedByName": "Jan Kowalski",
  "createdAt": "2026-01-19T15:00:00Z",
  "updatedAt": "2026-01-26T15:00:00Z"
}
```

**Response 404 Not Found** - artykuł nie istnieje lub jest soft-deleted

**Response 400 Bad Request** - błędy walidacji

**Uwagi**:
- `slug` regenerowany z nowego `title` (może się zmienić!)
- `updatedBy` i `updatedAt` aktualizowane automatycznie
- `status` NIE jest aktualizowany przez ten endpoint (użyj PATCH /publish lub /archive)
- Można edytować artykuł w dowolnym statusie (DRAFT, PUBLISHED, ARCHIVED)

---

### 2.5. DELETE /api/articles/{id} - Soft delete artykułu

**Opis**: Usuwa logicznie artykuł (ustawia `deleted_at`).

**Path Parameters**:
- `id` (long) - ID artykułu

**Response 204 No Content** - sukces (brak body)

**Response 404 Not Found** - artykuł nie istnieje lub jest już soft-deleted

**Uwagi**:
- Soft delete: ustawienie `deleted_at = NOW()`
- Artykuł przestaje być widoczny w listach (GET /api/articles)
- Możliwość odzyskania przez zmianę `deleted_at = NULL` (wymaga osobnego endpointu w przyszłości)

---

### 2.6. PATCH /api/articles/{id}/publish - Publikacja artykułu

**Opis**: Publikuje artykuł (zmiana statusu DRAFT → PUBLISHED).

**Path Parameters**:
- `id` (long) - ID artykułu

**Response 200 OK**:
```json
{
  "id": 1,
  "title": "Jak napisać pozew o zapłatę?",
  "slug": "jak-napisac-pozew-o-zaplate",
  "status": "PUBLISHED",
  "publishedAt": "2026-01-26T15:30:00Z",
  "updatedAt": "2026-01-26T15:30:00Z"
}
```

**Response 404 Not Found** - artykuł nie istnieje

**Response 400 Bad Request** - artykuł nie jest w statusie DRAFT (ProblemDetail)

**Uwagi**:
- Tylko artykuły w statusie `DRAFT` mogą być publikowane
- `publishedAt` ustawiane automatycznie na `NOW()`
- `updatedBy` i `updatedAt` aktualizowane

---

### 2.7. PATCH /api/articles/{id}/archive - Archiwizacja artykułu

**Opis**: Archiwizuje artykuł (zmiana statusu PUBLISHED → ARCHIVED).

**Path Parameters**:
- `id` (long) - ID artykułu

**Response 200 OK**:
```json
{
  "id": 1,
  "title": "Jak napisać pozew o zapłatę?",
  "slug": "jak-napisac-pozew-o-zaplate",
  "status": "ARCHIVED",
  "publishedAt": "2026-01-20T10:30:00Z",
  "updatedAt": "2026-01-26T16:00:00Z"
}
```

**Response 404 Not Found** - artykuł nie istnieje

**Response 400 Bad Request** - artykuł nie jest w statusie PUBLISHED (ProblemDetail)

**Uwagi**:
- Tylko artykuły w statusie `PUBLISHED` mogą być archiwizowane
- `publishedAt` pozostaje bez zmian
- `updatedBy` i `updatedAt` aktualizowane

---

## 3. Struktura Pakietów (Hexagonal Architecture)

```
pl.klastbit.lexpage/
├── domain/
│   ├── article/
│   │   ├── Article.java                          # Aggregate Root (czysta domena, bez JPA)
│   │   ├── ArticleStatus.java                    # Enum (DRAFT, PUBLISHED, ARCHIVED)
│   │   ├── ArticleRepository.java                # Port (interface)
│   │   └── exception/
│   │       ├── ArticleNotFoundException.java
│   │       └── InvalidArticleStatusTransitionException.java
│   │
│   └── user/
│       └── UserId.java                           # Value Object (już istnieje)
│
├── application/
│   └── article/
│       ├── CreateArticleUseCase.java             # Use Case interface
│       ├── UpdateArticleUseCase.java             # Use Case interface
│       ├── DeleteArticleUseCase.java             # Use Case interface
│       ├── PublishArticleUseCase.java            # Use Case interface
│       ├── ArchiveArticleUseCase.java            # Use Case interface
│       ├── GetArticleUseCase.java                # Use Case interface
│       ├── ListArticlesUseCase.java              # Use Case interface
│       │
│       ├── ArticleApplicationService.java        # Application Service (implementuje use cases)
│       │
│       ├── command/
│       │   ├── CreateArticleCommand.java         # Command (Record)
│       │   └── UpdateArticleCommand.java         # Command (Record)
│       │
│       └── dto/
│           ├── ArticleListItemDto.java           # DTO (Record) - item w liście
│           ├── ArticleDetailDto.java             # DTO (Record) - pełne dane
│           └── PageDto.java                      # DTO (Record) - paginacja
│
└── infrastructure/
    ├── web/
    │   └── controller/
    │       ├── ArticleController.java            # REST Controller
    │       └── dto/
    │           ├── CreateArticleRequest.java     # Request DTO (Record)
    │           ├── UpdateArticleRequest.java     # Request DTO (Record)
    │           ├── ArticleResponse.java          # Response DTO (Record)
    │           └── ArticleListResponse.java      # Response DTO (Record)
    │
    └── adapters/
        └── persistence/
            ├── entity/
            │   ├── ArticleEntity.java            # JPA Entity (z adnotacjami, relacjami UserEntity)
            │   ├── UserEntity.java               # JPA Entity (już istnieje)
            │   └── BaseEntity.java               # Base entity (już istnieje)
            │
            ├── repository/
            │   ├── JpaArticleRepository.java     # Adapter (implementuje ArticleRepository)
            │   └── SpringDataArticleRepository.java  # Spring Data JPA interface
            │
            └── mapper/
                └── ArticleMapper.java            # Mapper: ArticleEntity ↔ Article (domain)
```

**Uwaga**: Zgodnie z wzorcem w projekcie:
- **Article (domain)** - czysta logika biznesowa, używa `UserId` (Value Object), bez adnotacji JPA
- **ArticleEntity (infrastructure)** - JPA entity z adnotacjami, używa `UserEntity` (relacje FK)
- **ArticleMapper** - konwertuje między `Article` ↔ `ArticleEntity`

---

## 4. Definicje Klas

### 4.1. Domain Layer

#### Article.java (Domain Entity / Aggregate Root)
**WAŻNE**: Czysta encja domenowa BEZ adnotacji JPA!

```java
package pl.klastbit.lexpage.domain.article;

import lombok.Getter;
import pl.klastbit.lexpage.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Article Domain Entity (DDD Aggregate Root).
 * Encapsulates article business logic including publication workflow, SEO, and lifecycle management.
 *
 * IMPORTANT: This is a PURE domain entity with NO JPA annotations.
 * Persistence is handled by ArticleEntity in infrastructure layer.
 */
@Getter
public class Article {

    private Long id;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private ArticleStatus status;
    private UserId authorId;
    private LocalDateTime publishedAt;

    // SEO fields
    private String metaTitle;
    private String metaDescription;
    private String ogImageUrl;
    private String canonicalUrl;
    private List<String> keywords;

    // Audit
    private UserId createdBy;
    private UserId updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Private constructor to enforce factory methods
    private Article() {}

    /**
     * Factory method to create a new draft article.
     */
    public static Article createDraft(
            String title,
            String slug,
            String content,
            String excerpt,
            String metaTitle,
            String metaDescription,
            String ogImageUrl,
            String canonicalUrl,
            List<String> keywords,
            UserId authorId
    ) {
        Article article = new Article();
        article.title = Objects.requireNonNull(title, "Title cannot be null");
        article.slug = Objects.requireNonNull(slug, "Slug cannot be null");
        article.content = Objects.requireNonNull(content, "Content cannot be null");
        article.excerpt = excerpt;
        article.metaTitle = metaTitle;
        article.metaDescription = metaDescription;
        article.ogImageUrl = ogImageUrl;
        article.canonicalUrl = canonicalUrl;
        article.keywords = keywords;
        article.authorId = Objects.requireNonNull(authorId, "Author ID cannot be null");
        article.status = ArticleStatus.DRAFT;
        article.createdAt = LocalDateTime.now();
        article.updatedAt = LocalDateTime.now();
        article.createdBy = authorId;
        article.updatedBy = authorId;
        return article;
    }

    /**
     * Factory method to reconstruct an existing article from database.
     * Used by infrastructure layer mappers. No business validation applied.
     */
    public static Article ofExisting(
            Long id,
            String title,
            String slug,
            String content,
            String excerpt,
            ArticleStatus status,
            UserId authorId,
            LocalDateTime publishedAt,
            String metaTitle,
            String metaDescription,
            String ogImageUrl,
            String canonicalUrl,
            List<String> keywords,
            UserId createdBy,
            UserId updatedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        Article article = new Article();
        article.id = id;
        article.title = title;
        article.slug = slug;
        article.content = content;
        article.excerpt = excerpt;
        article.status = status;
        article.authorId = authorId;
        article.publishedAt = publishedAt;
        article.metaTitle = metaTitle;
        article.metaDescription = metaDescription;
        article.ogImageUrl = ogImageUrl;
        article.canonicalUrl = canonicalUrl;
        article.keywords = keywords;
        article.createdBy = createdBy;
        article.updatedBy = updatedBy;
        article.createdAt = createdAt;
        article.updatedAt = updatedAt;
        article.deletedAt = deletedAt;
        return article;
    }

    // ==================== Business Logic Methods ====================

    /**
     * Publishes the article.
     * Business rule: Only DRAFT or ARCHIVED articles can be published.
     */
    public void publish() {
        if (status == ArticleStatus.PUBLISHED) {
            throw new IllegalStateException("Article is already published");
        }

        validatePublicationRequirements();

        this.status = ArticleStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Archives the article.
     * Business rule: Only PUBLISHED articles can be archived.
     */
    public void archive() {
        if (status != ArticleStatus.PUBLISHED) {
            throw new InvalidArticleStatusTransitionException(
                "Cannot archive article that is not in PUBLISHED status. Current status: " + status
            );
        }

        this.status = ArticleStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Unpublishes the article back to DRAFT status.
     */
    public void unpublish() {
        if (status != ArticleStatus.PUBLISHED) {
            throw new InvalidArticleStatusTransitionException(
                "Cannot unpublish article that is not in PUBLISHED status. Current status: " + status
            );
        }

        this.status = ArticleStatus.DRAFT;
        this.publishedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates article content and metadata.
     */
    public void updateContent(
            String newTitle,
            String newSlug,
            String newContent,
            String newExcerpt,
            String newMetaTitle,
            String newMetaDescription,
            String newOgImageUrl,
            String newCanonicalUrl,
            List<String> newKeywords,
            UserId userId
    ) {
        this.title = Objects.requireNonNull(newTitle, "Title cannot be null");
        this.slug = Objects.requireNonNull(newSlug, "Slug cannot be null");
        this.content = Objects.requireNonNull(newContent, "Content cannot be null");

        if (newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        this.excerpt = newExcerpt;
        this.metaTitle = newMetaTitle;
        this.metaDescription = newMetaDescription;
        this.ogImageUrl = newOgImageUrl;
        this.canonicalUrl = newCanonicalUrl;
        this.keywords = newKeywords;
        this.updatedBy = userId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Soft deletes the article.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if article is published.
     */
    public boolean isPublished() {
        return status == ArticleStatus.PUBLISHED;
    }

    /**
     * Checks if article is deleted (soft delete).
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Validates requirements for publication.
     */
    private void validatePublicationRequirements() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalStateException("Cannot publish article without title");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalStateException("Cannot publish article without content");
        }

        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalStateException("Cannot publish article without slug");
        }
    }

    /**
     * Calculates approximate word count.
     */
    public int getWordCount() {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }
}
```

#### ArticleStatus.java (Enum)
```java
package pl.klastbit.lexpage.domain.article;

public enum ArticleStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}
```

**UWAGA**: W bazie danych kolumna `status` to `VARCHAR(50)`, NIE custom PostgreSQL ENUM! (zgodnie z backend.md)

#### ArticleRepository.java (Port)
```java
package pl.klastbit.lexpage.domain.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ArticleRepository {
    Article save(Article article);
    Optional<Article> findById(Long id);
    Optional<Article> findByIdAndDeletedAtIsNull(Long id);
    Page<Article> findAll(Pageable pageable);
    Page<Article> findAllByDeletedAtIsNull(Pageable pageable);
    Page<Article> findAllByStatusAndDeletedAtIsNull(ArticleStatus status, Pageable pageable);
    Page<Article> findAllByAuthorIdAndDeletedAtIsNull(Long authorId, Pageable pageable);
    void delete(Article article);
    boolean existsBySlugAndDeletedAtIsNull(String slug);
}
```

#### Exceptions
```java
package pl.klastbit.lexpage.domain.article.exception;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(Long articleId) {
        super("Article not found with ID: " + articleId);
    }
}
```

```java
package pl.klastbit.lexpage.domain.article.exception;

public class InvalidArticleStatusTransitionException extends RuntimeException {
    public InvalidArticleStatusTransitionException(String message) {
        super(message);
    }
}
```

---

### 4.2. Application Layer

#### Commands (Records)
```java
package pl.klastbit.lexpage.application.article.command;

import java.util.List;

public record CreateArticleCommand(
    String title,
    String content,
    String excerpt,
    String metaTitle,
    String metaDescription,
    String ogImageUrl,
    String canonicalUrl,
    List<String> keywords,
    Long authorId,  // z Spring Security context
    Long createdBy,  // z Spring Security context
    Long updatedBy   // z Spring Security context
) {}
```

```java
package pl.klastbit.lexpage.application.article.command;

import java.util.List;

public record UpdateArticleCommand(
    Long id,
    String title,
    String content,
    String excerpt,
    String metaTitle,
    String metaDescription,
    String ogImageUrl,
    String canonicalUrl,
    List<String> keywords,
    Long updatedBy  // z Spring Security context
) {}
```

#### DTOs (Records)
```java
package pl.klastbit.lexpage.application.article.dto;

import pl.klastbit.lexpage.domain.article.ArticleStatus;
import java.time.LocalDateTime;

public record ArticleListItemDto(
    Long id,
    String title,
    String slug,
    String excerpt,
    ArticleStatus status,
    Long authorId,
    String authorName,  // JOIN z users table
    LocalDateTime publishedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ArticleListItemDto from(Article article, String authorName) {
        return new ArticleListItemDto(
            article.getId(),
            article.getTitle(),
            article.getSlug(),
            article.getExcerpt(),
            article.getStatus(),
            article.getAuthorId(),
            authorName,
            article.getPublishedAt(),
            article.getCreatedAt(),
            article.getUpdatedAt()
        );
    }
}
```

```java
package pl.klastbit.lexpage.application.article.dto;

import pl.klastbit.lexpage.domain.article.ArticleStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ArticleDetailDto(
    Long id,
    String title,
    String slug,
    String content,
    String excerpt,
    ArticleStatus status,
    Long authorId,
    String authorName,
    LocalDateTime publishedAt,
    String metaTitle,
    String metaDescription,
    String ogImageUrl,
    String canonicalUrl,
    List<String> keywords,
    Long createdBy,
    String createdByName,
    Long updatedBy,
    String updatedByName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ArticleDetailDto from(Article article, String authorName,
                                        String createdByName, String updatedByName) {
        return new ArticleDetailDto(
            article.getId(),
            article.getTitle(),
            article.getSlug(),
            article.getContent(),
            article.getExcerpt(),
            article.getStatus(),
            article.getAuthorId(),
            authorName,
            article.getPublishedAt(),
            article.getMetaTitle(),
            article.getMetaDescription(),
            article.getOgImageUrl(),
            article.getCanonicalUrl(),
            article.getKeywords(),
            article.getCreatedBy(),
            createdByName,
            article.getUpdatedBy(),
            updatedByName,
            article.getCreatedAt(),
            article.getUpdatedAt()
        );
    }
}
```

```java
package pl.klastbit.lexpage.application.article.dto;

public record PageDto<T>(
    java.util.List<T> content,
    PageInfo page
) {
    public record PageInfo(
        int number,
        int size,
        long totalElements,
        int totalPages
    ) {}

    public static <T> PageDto<T> from(org.springframework.data.domain.Page<T> page) {
        return new PageDto<>(
            page.getContent(),
            new PageInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
            )
        );
    }
}
```

#### Use Cases (Interfaces)
```java
package pl.klastbit.lexpage.application.article;

public interface CreateArticleUseCase {
    ArticleDetailDto execute(CreateArticleCommand command);
}
```

```java
package pl.klastbit.lexpage.application.article;

public interface UpdateArticleUseCase {
    ArticleDetailDto execute(UpdateArticleCommand command);
}
```

```java
package pl.klastbit.lexpage.application.article;

public interface DeleteArticleUseCase {
    void execute(Long articleId);
}
```

```java
package pl.klastbit.lexpage.application.article;

public interface PublishArticleUseCase {
    ArticleDetailDto execute(Long articleId);
}
```

```java
package pl.klastbit.lexpage.application.article;

public interface ArchiveArticleUseCase {
    ArticleDetailDto execute(Long articleId);
}
```

```java
package pl.klastbit.lexpage.application.article;

import org.springframework.data.domain.Pageable;

public interface GetArticleUseCase {
    ArticleDetailDto execute(Long articleId);
}
```

```java
package pl.klastbit.lexpage.application.article;

import org.springframework.data.domain.Pageable;
import pl.klastbit.lexpage.domain.article.ArticleStatus;

public interface ListArticlesUseCase {
    PageDto<ArticleListItemDto> execute(ArticleStatus status, Long authorId, String keyword, Pageable pageable);
}
```

#### Application Service
```java
package pl.klastbit.lexpage.application.article;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klastbit.lexpage.domain.article.*;
import pl.klastbit.lexpage.domain.article.exception.*;
import pl.klastbit.lexpage.application.article.command.*;
import pl.klastbit.lexpage.application.article.dto.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ArticleApplicationService implements
    CreateArticleUseCase,
    UpdateArticleUseCase,
    DeleteArticleUseCase,
    PublishArticleUseCase,
    ArchiveArticleUseCase,
    GetArticleUseCase,
    ListArticlesUseCase {

    private final ArticleRepository articleRepository;
    // private final UserRepository userRepository; // dla pobrania nazw użytkowników

    @Override
    public ArticleDetailDto execute(CreateArticleCommand command) {
        log.info("Creating new article with title: {}", command.title());

        // Generowanie slug z title
        String slug = generateSlug(command.title());

        // Sprawdzenie unikalności slug
        if (articleRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            slug = makeSlugUnique(slug);
        }

        // Auto-generowanie meta description jeśli brak
        String metaDescription = command.metaDescription();
        if (metaDescription == null || metaDescription.isBlank()) {
            metaDescription = generateMetaDescription(command.content());
        }

        Article article = Article.builder()
            .title(command.title())
            .slug(slug)
            .content(command.content())
            .excerpt(command.excerpt())
            .status(ArticleStatus.DRAFT)
            .authorId(command.authorId())
            .metaTitle(command.metaTitle())
            .metaDescription(metaDescription)
            .ogImageUrl(command.ogImageUrl())
            .canonicalUrl(command.canonicalUrl())
            .keywords(command.keywords())
            .createdBy(command.createdBy())
            .updatedBy(command.updatedBy())
            .build();

        Article savedArticle = articleRepository.save(article);
        log.info("Article created successfully with ID: {}", savedArticle.getId());

        // Pobierz nazwy użytkowników z UserRepository i zwróć DTO
        return ArticleDetailDto.from(savedArticle, "Author Name", "Creator Name", "Updater Name");
    }

    @Override
    public ArticleDetailDto execute(UpdateArticleCommand command) {
        log.info("Updating article with ID: {}", command.id());

        Article article = articleRepository.findByIdAndDeletedAtIsNull(command.id())
            .orElseThrow(() -> new ArticleNotFoundException(command.id()));

        // Regeneracja slug jeśli title się zmienił
        if (!article.getTitle().equals(command.title())) {
            String newSlug = generateSlug(command.title());
            if (!newSlug.equals(article.getSlug()) &&
                articleRepository.existsBySlugAndDeletedAtIsNull(newSlug)) {
                newSlug = makeSlugUnique(newSlug);
            }
            article.setSlug(newSlug);
        }

        article.setTitle(command.title());
        article.setContent(command.content());
        article.setExcerpt(command.excerpt());
        article.setMetaTitle(command.metaTitle());
        article.setMetaDescription(command.metaDescription());
        article.setOgImageUrl(command.ogImageUrl());
        article.setCanonicalUrl(command.canonicalUrl());
        article.setKeywords(command.keywords());
        article.setUpdatedBy(command.updatedBy());

        Article updatedArticle = articleRepository.save(article);
        log.info("Article updated successfully with ID: {}", updatedArticle.getId());

        return ArticleDetailDto.from(updatedArticle, "Author Name", "Creator Name", "Updater Name");
    }

    @Override
    public void execute(Long articleId) {
        log.info("Soft deleting article with ID: {}", articleId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
            .orElseThrow(() -> new ArticleNotFoundException(articleId));

        article.softDelete();
        articleRepository.save(article);

        log.info("Article soft deleted successfully with ID: {}", articleId);
    }

    @Override
    public ArticleDetailDto execute(Long articleId) {
        log.info("Publishing article with ID: {}", articleId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
            .orElseThrow(() -> new ArticleNotFoundException(articleId));

        article.publish(); // throws InvalidArticleStatusTransitionException if not DRAFT
        Article publishedArticle = articleRepository.save(article);

        log.info("Article published successfully with ID: {}", articleId);

        return ArticleDetailDto.from(publishedArticle, "Author Name", "Creator Name", "Updater Name");
    }

    // Archive use case implementation - analogicznie do publish

    // Get use case implementation
    @Override
    @Transactional(readOnly = true)
    public ArticleDetailDto execute(Long articleId) {
        log.info("Fetching article with ID: {}", articleId);

        Article article = articleRepository.findByIdAndDeletedAtIsNull(articleId)
            .orElseThrow(() -> new ArticleNotFoundException(articleId));

        return ArticleDetailDto.from(article, "Author Name", "Creator Name", "Updater Name");
    }

    // List use case implementation
    @Override
    @Transactional(readOnly = true)
    public PageDto<ArticleListItemDto> execute(ArticleStatus status, Long authorId,
                                               String keyword, Pageable pageable) {
        log.info("Listing articles with status: {}, authorId: {}, keyword: {}", status, authorId, keyword);

        Page<Article> articlesPage;

        if (status != null && authorId != null) {
            // Filtrowanie po status i authorId
            articlesPage = articleRepository.findAllByStatusAndAuthorIdAndDeletedAtIsNull(status, authorId, pageable);
        } else if (status != null) {
            articlesPage = articleRepository.findAllByStatusAndDeletedAtIsNull(status, pageable);
        } else if (authorId != null) {
            articlesPage = articleRepository.findAllByAuthorIdAndDeletedAtIsNull(authorId, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            // Full-text search (wymaga dodania metody w repository)
            articlesPage = articleRepository.searchByKeywordAndDeletedAtIsNull(keyword, pageable);
        } else {
            articlesPage = articleRepository.findAllByDeletedAtIsNull(pageable);
        }

        Page<ArticleListItemDto> dtoPage = articlesPage.map(article ->
            ArticleListItemDto.from(article, "Author Name")
        );

        return PageDto.from(dtoPage);
    }

    // Helper methods
    private String generateSlug(String title) {
        // Transliteracja PL → ASCII, lowercase, usunięcie znaków specjalnych, zamiana spacji na -
        // Przykładowa implementacja (można użyć biblioteki typu Apache Commons Text)
        return title.toLowerCase()
            .replaceAll("[ąćęłńóśźż]", m -> transliterate(m.group()))
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }

    private String transliterate(String polishChar) {
        return switch (polishChar) {
            case "ą" -> "a";
            case "ć" -> "c";
            case "ę" -> "e";
            case "ł" -> "l";
            case "ń" -> "n";
            case "ó" -> "o";
            case "ś" -> "s";
            case "ź", "ż" -> "z";
            default -> polishChar;
        };
    }

    private String makeSlugUnique(String slug) {
        int counter = 1;
        String uniqueSlug = slug;
        while (articleRepository.existsBySlugAndDeletedAtIsNull(uniqueSlug)) {
            uniqueSlug = slug + "-" + counter++;
        }
        return uniqueSlug;
    }

    private String generateMetaDescription(String content) {
        // Usunięcie HTML tags i obcięcie do 160 znaków
        String plainText = content.replaceAll("<[^>]+>", "").trim();
        if (plainText.length() > 160) {
            return plainText.substring(0, 157) + "...";
        }
        return plainText;
    }
}
```

**UWAGA**: Metody w `ArticleApplicationService` mają ten sam sygnaturę `execute()` (przeciążone). W praktyce lepiej użyć osobnych metod lub osobnych klas implementujących use cases.

---

### 4.3. Infrastructure Layer - Persistence

#### ArticleEntity.java (JPA Entity)
**WAŻNE**: Encja JPA z adnotacjami, używa relacji `UserEntity`!

```java
package pl.klastbit.lexpage.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import pl.klastbit.lexpage.domain.article.ArticleStatus;

import java.time.LocalDateTime;

/**
 * JPA Entity for articles table.
 * Includes SEO fields, full-text search, audit trail, and soft delete support.
 *
 * IMPORTANT: This is a PERSISTENCE entity with JPA annotations.
 * Business logic resides in Article domain entity.
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

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "excerpt", length = 500)
    private String excerpt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
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

    // Timestamps are inherited from BaseEntity (createdAt, updatedAt, deletedAt)
}
```

**Uwaga**: `ArticleEntity` dziedziczy po `BaseEntity` (już istnieje w projekcie) który zawiera pola:
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)
- `deletedAt` (LocalDateTime)

#### ArticleMapper.java (Mapper Domain ↔ Persistence)

```java
package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;

import java.util.Arrays;
import java.util.List;

/**
 * Mapper between Article domain entity and ArticleEntity persistence entity.
 * Part of the infrastructure layer (Hexagonal Architecture outbound adapter).
 */
@Component
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
                entity.getStatus(),
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
     * NOTE: UserEntity references must be set separately via setUserReferences()
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
        entity.setStatus(domain.getStatus());
        entity.setPublishedAt(domain.getPublishedAt());

        // SEO fields
        entity.setMetaTitle(domain.getMetaTitle());
        entity.setMetaDescription(domain.getMetaDescription());
        entity.setOgImageUrl(domain.getOgImageUrl());
        entity.setCanonicalUrl(domain.getCanonicalUrl());
        entity.setKeywords(listToArray(domain.getKeywords()));

        // Audit fields - timestamps
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());

        // NOTE: UserEntity references (author, createdBy, updatedBy) need to be set
        // separately by the repository using setUserReferences() method

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
        entity.setStatus(domain.getStatus());
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

        // UserEntity references are NOT updated here to preserve relationships
    }

    /**
     * Extracts author ID from ArticleEntity.
     *
     * @param entity JPA entity
     * @return Author ID or null if not set
     */
    private UserId getAuthorId(ArticleEntity entity) {
        return entity != null && entity.getAuthor() != null
                ? UserId.of(entity.getAuthor().getId())
                : null;
    }

    /**
     * Extracts createdBy user ID from ArticleEntity.
     *
     * @param entity JPA entity
     * @return Created by user ID or null if not set
     */
    private UserId getCreatedById(ArticleEntity entity) {
        return entity != null && entity.getCreatedBy() != null
                ? UserId.of(entity.getCreatedBy().getId())
                : null;
    }

    /**
     * Extracts updatedBy user ID from ArticleEntity.
     *
     * @param entity JPA entity
     * @return Updated by user ID or null if not set
     */
    private UserId getUpdatedById(ArticleEntity entity) {
        return entity != null && entity.getUpdatedBy() != null
                ? UserId.of(entity.getUpdatedBy().getId())
                : null;
    }

    /**
     * Sets UserEntity references on ArticleEntity from IDs.
     * This method should be called by repository with loaded UserEntity references.
     *
     * @param entity    JPA entity to update
     * @param author    Author user entity
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
}
```

---

### 4.4. Infrastructure Layer - Web (REST Controller)

#### Request DTOs (Records)
```java
package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record CreateArticleRequest(
    @NotBlank(message = "Tytuł jest wymagany")
    @Size(max = 255, message = "Tytuł nie może przekraczać 255 znaków")
    String title,

    @NotBlank(message = "Treść jest wymagana")
    @Size(min = 50, max = 25000, message = "Treść musi mieć od 50 do 25000 znaków")
    String content,

    @Size(max = 500, message = "Excerpt nie może przekraczać 500 znaków")
    String excerpt,

    @Size(max = 60, message = "Meta title nie może przekraczać 60 znaków")
    String metaTitle,

    @Size(max = 160, message = "Meta description nie może przekraczać 160 znaków")
    String metaDescription,

    @Size(max = 500, message = "OG Image URL nie może przekraczać 500 znaków")
    String ogImageUrl,

    @Size(max = 500, message = "Canonical URL nie może przekraczać 500 znaków")
    String canonicalUrl,

    @Size(max = 10, message = "Maksymalnie 10 keywords")
    List<@Size(max = 50, message = "Keyword nie może przekraczać 50 znaków") String> keywords
) {
    public CreateArticleCommand toCommand(Long userId) {
        return new CreateArticleCommand(
            title,
            content,
            excerpt,
            metaTitle,
            metaDescription,
            ogImageUrl,
            canonicalUrl,
            keywords,
            userId,  // authorId
            userId,  // createdBy
            userId   // updatedBy
        );
    }
}
```

```java
package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record UpdateArticleRequest(
    @NotBlank(message = "Tytuł jest wymagany")
    @Size(max = 255, message = "Tytuł nie może przekraczać 255 znaków")
    String title,

    @NotBlank(message = "Treść jest wymagana")
    @Size(min = 50, max = 25000, message = "Treść musi mieć od 50 do 25000 znaków")
    String content,

    @Size(max = 500, message = "Excerpt nie może przekraczać 500 znaków")
    String excerpt,

    @Size(max = 60, message = "Meta title nie może przekraczać 60 znaków")
    String metaTitle,

    @Size(max = 160, message = "Meta description nie może przekraczać 160 znaków")
    String metaDescription,

    @Size(max = 500, message = "OG Image URL nie może przekraczać 500 znaków")
    String ogImageUrl,

    @Size(max = 500, message = "Canonical URL nie może przekraczać 500 znaków")
    String canonicalUrl,

    @Size(max = 10, message = "Maksymalnie 10 keywords")
    List<@Size(max = 50, message = "Keyword nie może przekraczać 50 znaków") String> keywords
) {
    public UpdateArticleCommand toCommand(Long articleId, Long userId) {
        return new UpdateArticleCommand(
            articleId,
            title,
            content,
            excerpt,
            metaTitle,
            metaDescription,
            ogImageUrl,
            canonicalUrl,
            keywords,
            userId  // updatedBy
        );
    }
}
```

#### Response DTOs (Records)
```java
package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ArticleResponse(
    Long id,
    String title,
    String slug,
    String content,
    String excerpt,
    String status,
    Long authorId,
    String authorName,
    LocalDateTime publishedAt,
    String metaTitle,
    String metaDescription,
    String ogImageUrl,
    String canonicalUrl,
    List<String> keywords,
    Long createdBy,
    String createdByName,
    Long updatedBy,
    String updatedByName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ArticleResponse from(ArticleDetailDto dto) {
        return new ArticleResponse(
            dto.id(),
            dto.title(),
            dto.slug(),
            dto.content(),
            dto.excerpt(),
            dto.status().name(),
            dto.authorId(),
            dto.authorName(),
            dto.publishedAt(),
            dto.metaTitle(),
            dto.metaDescription(),
            dto.ogImageUrl(),
            dto.canonicalUrl(),
            dto.keywords(),
            dto.createdBy(),
            dto.createdByName(),
            dto.updatedBy(),
            dto.updatedByName(),
            dto.createdAt(),
            dto.updatedAt()
        );
    }
}
```

```java
package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import pl.klastbit.lexpage.application.article.dto.*;
import java.util.List;

public record ArticleListResponse(
    List<ArticleListItem> content,
    PageInfo page
) {
    public record ArticleListItem(
        Long id,
        String title,
        String slug,
        String excerpt,
        String status,
        Long authorId,
        String authorName,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        public static ArticleListItem from(ArticleListItemDto dto) {
            return new ArticleListItem(
                dto.id(),
                dto.title(),
                dto.slug(),
                dto.excerpt(),
                dto.status().name(),
                dto.authorId(),
                dto.authorName(),
                dto.publishedAt(),
                dto.createdAt(),
                dto.updatedAt()
            );
        }
    }

    public record PageInfo(
        int number,
        int size,
        long totalElements,
        int totalPages
    ) {}

    public static ArticleListResponse from(PageDto<ArticleListItemDto> pageDto) {
        List<ArticleListItem> items = pageDto.content().stream()
            .map(ArticleListItem::from)
            .toList();

        return new ArticleListResponse(
            items,
            new PageInfo(
                pageDto.page().number(),
                pageDto.page().size(),
                pageDto.page().totalElements(),
                pageDto.page().totalPages()
            )
        );
    }
}
```

#### REST Controller
```java
package pl.klastbit.lexpage.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.klastbit.lexpage.application.article.*;
import pl.klastbit.lexpage.application.article.dto.*;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.infrastructure.web.controller.dto.*;

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
    private final GetArticleUseCase getArticleUseCase;
    private final ListArticlesUseCase listArticlesUseCase;

    @GetMapping
    public ResponseEntity<ArticleListResponse> listArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        log.info("GET /api/articles - page: {}, size: {}, status: {}, authorId: {}, keyword: {}",
            page, size, status, authorId, keyword);

        // Walidacja size
        if (size > 100) {
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }

        // Parsowanie sortowania
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        // Parsowanie status
        ArticleStatus articleStatus = status != null ? ArticleStatus.valueOf(status.toUpperCase()) : null;

        PageDto<ArticleListItemDto> result = listArticlesUseCase.execute(articleStatus, authorId, keyword, pageable);

        return ResponseEntity.ok(ArticleListResponse.from(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long id) {
        log.info("GET /api/articles/{}", id);

        ArticleDetailDto article = getArticleUseCase.execute(id);

        return ResponseEntity.ok(ArticleResponse.from(article));
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @Valid @RequestBody CreateArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("POST /api/articles - title: {}", request.title());

        // Pobranie userId z Spring Security context
        Long userId = getCurrentUserId(userDetails);

        ArticleDetailDto created = createArticleUseCase.execute(request.toCommand(userId));

        return ResponseEntity.status(HttpStatus.CREATED).body(ArticleResponse.from(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("PUT /api/articles/{} - title: {}", id, request.title());

        Long userId = getCurrentUserId(userDetails);

        ArticleDetailDto updated = updateArticleUseCase.execute(request.toCommand(id, userId));

        return ResponseEntity.ok(ArticleResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        log.info("DELETE /api/articles/{}", id);

        deleteArticleUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ArticleResponse> publishArticle(@PathVariable Long id) {
        log.info("PATCH /api/articles/{}/publish", id);

        ArticleDetailDto published = publishArticleUseCase.execute(id);

        return ResponseEntity.ok(ArticleResponse.from(published));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<ArticleResponse> archiveArticle(@PathVariable Long id) {
        log.info("PATCH /api/articles/{}/archive", id);

        ArticleDetailDto archived = archiveArticleUseCase.execute(id);

        return ResponseEntity.ok(ArticleResponse.from(archived));
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        // Implementacja zależna od UserDetails (może wymagać custom UserDetails)
        // Na razie zwracamy hardcoded 1L
        return 1L;
    }
}
```

#### JpaArticleRepository.java (Repository Adapter)
**WAŻNE**: Adapter który używa mappera do konwersji między `Article` (domain) a `ArticleEntity` (JPA)!

```java
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
    public Page<Article> findAllByAuthorIdAndDeletedAtIsNull(Long authorId, Pageable pageable) {
        log.debug("Finding articles by authorId: {} with pageable: {}", authorId, pageable);
        // Note: SpringDataArticleRepository uses UserEntity, so we need to find by author.id
        return springDataRepository.findAllByAuthor_IdAndDeletedAtIsNull(authorId, pageable)
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

    // Private helper methods

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
        return userRepository.findById(userId.value())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId.value()));
    }
}
```

#### SpringDataArticleRepository.java (Spring Data JPA Repository)
**WAŻNE**: Używa `ArticleEntity` (JPA entity), NIE `Article` (domain)!

```java
package pl.klastbit.lexpage.infrastructure.adapters.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity;

import java.util.Optional;

/**
 * Spring Data JPA Repository for ArticleEntity.
 * Works with persistence layer entities, NOT domain entities.
 */
public interface SpringDataArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Optional<ArticleEntity> findByIdAndDeletedAtIsNull(Long id);

    Page<ArticleEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Page<ArticleEntity> findAllByStatusAndDeletedAtIsNull(ArticleStatus status, Pageable pageable);

    // Query by author.id (relationship navigation)
    Page<ArticleEntity> findAllByAuthor_IdAndDeletedAtIsNull(Long authorId, Pageable pageable);

    Page<ArticleEntity> findAllByStatusAndAuthor_IdAndDeletedAtIsNull(
        ArticleStatus status, Long authorId, Pageable pageable
    );

    boolean existsBySlugAndDeletedAtIsNull(String slug);

    // Full-text search using search_vector column (managed by DB trigger)
    @Query(value = """
        SELECT a.* FROM articles a
        WHERE a.deleted_at IS NULL
        AND a.search_vector @@ plainto_tsquery('polish', :keyword)
        ORDER BY ts_rank(a.search_vector, plainto_tsquery('polish', :keyword)) DESC
        """, nativeQuery = true)
    Page<ArticleEntity> searchByKeywordAndDeletedAtIsNull(@Param("keyword") String keyword, Pageable pageable);
}
```

**Uwaga**:
- Full-text search używa kolumny `search_vector` (TSVECTOR) która jest automatycznie aktualizowana przez trigger PostgreSQL
- Wyniki są sortowane po relevance (`ts_rank`)
- Native query bo używamy PostgreSQL-specific funkcji

---

### 4.4. Global Exception Handler

```java
package pl.klastbit.lexpage.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.klastbit.lexpage.domain.article.exception.*;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ArticleNotFoundException.class)
    public ProblemDetail handleArticleNotFound(ArticleNotFoundException ex) {
        log.warn("Article not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );

        problemDetail.setTitle("Article Not Found");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/article-not-found"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(InvalidArticleStatusTransitionException.class)
    public ProblemDetail handleInvalidStatusTransition(InvalidArticleStatusTransitionException ex) {
        log.warn("Invalid article status transition: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );

        problemDetail.setTitle("Invalid Status Transition");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/invalid-status-transition"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            errorMessage
        );

        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/validation-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("fieldErrors", ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new FieldError(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue()
            ))
            .collect(Collectors.toList())
        );

        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );

        problemDetail.setTitle("Invalid Request");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/invalid-request"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Wystąpił nieoczekiwany błąd. Spróbuj ponownie lub skontaktuj się z nami."
        );

        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://klastbit.pl/errors/internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    private record FieldError(String field, String message, Object rejectedValue) {}
}
```

---

## 5. Kroki Implementacji

### Krok 1: Migracja Bazy Danych (Liquibase)
**UWAGA**: Tabela `articles` już istnieje zgodnie z `database-erd.md`. Sprawdź czy struktura się zgadza i czy enumy to VARCHAR!

Plik: `src/main/resources/db/changelog/v1.0/XX-verify-articles-table.xml`

```xml
<changeSet id="verify-articles-table-structure" author="system">
    <comment>Weryfikacja struktury tabeli articles (powinna już istnieć)</comment>

    <preConditions onFail="HALT">
        <tableExists tableName="articles"/>
    </preConditions>

    <!-- Jeśli tabela nie ma poprawnej struktury, dodaj brakujące kolumny -->
    <addColumn tableName="articles">
        <column name="excerpt" type="VARCHAR(500)" />
    </addColumn>

    <!-- Sprawdzenie czy status to VARCHAR, nie ENUM -->
    <sql>
        SELECT data_type FROM information_schema.columns
        WHERE table_name = 'articles' AND column_name = 'status';
        -- Oczekiwany wynik: character varying
    </sql>
</changeSet>
```

Jeśli tabela `articles` jeszcze nie istnieje, użyj changesetu z `database-erd.md`:

```xml
<changeSet id="create-articles-table" author="system">
    <createTable tableName="articles">
        <column name="id" type="BIGSERIAL" autoIncrement="true">
            <constraints primaryKey="true" nullable="false"/>
        </column>
        <column name="title" type="VARCHAR(255)">
            <constraints nullable="false"/>
        </column>
        <column name="slug" type="VARCHAR(255)">
            <constraints nullable="false" unique="true"/>
        </column>
        <column name="content" type="TEXT">
            <constraints nullable="false"/>
        </column>
        <column name="excerpt" type="VARCHAR(500)"/>
        <column name="status" type="VARCHAR(50)" defaultValue="DRAFT">
            <constraints nullable="false"/>
        </column>
        <column name="author_id" type="BIGINT">
            <constraints nullable="false" foreignKeyName="fk_articles_author"
                         referencedTableName="users" referencedColumnNames="id"/>
        </column>
        <column name="published_at" type="TIMESTAMP"/>
        <column name="meta_title" type="VARCHAR(60)"/>
        <column name="meta_description" type="VARCHAR(160)"/>
        <column name="og_image_url" type="VARCHAR(500)"/>
        <column name="canonical_url" type="VARCHAR(500)"/>
        <column name="keywords" type="TEXT[]"/>
        <column name="search_vector" type="TSVECTOR"/>
        <column name="created_by" type="BIGINT">
            <constraints nullable="false" foreignKeyName="fk_articles_created_by"
                         referencedTableName="users" referencedColumnNames="id"/>
        </column>
        <column name="updated_by" type="BIGINT">
            <constraints nullable="false" foreignKeyName="fk_articles_updated_by"
                         referencedTableName="users" referencedColumnNames="id"/>
        </column>
        <column name="created_at" type="TIMESTAMP" defaultValueComputed="NOW()">
            <constraints nullable="false"/>
        </column>
        <column name="updated_at" type="TIMESTAMP" defaultValueComputed="NOW()">
            <constraints nullable="false"/>
        </column>
        <column name="deleted_at" type="TIMESTAMP"/>
    </createTable>

    <!-- Indeksy -->
    <createIndex indexName="idx_articles_slug" tableName="articles">
        <column name="slug"/>
    </createIndex>

    <createIndex indexName="idx_articles_status_published_at" tableName="articles">
        <column name="status"/>
        <column name="published_at" descending="true"/>
    </createIndex>

    <createIndex indexName="idx_articles_search_vector" tableName="articles">
        <column name="search_vector"/>
    </createIndex>

    <createIndex indexName="idx_articles_author_id" tableName="articles">
        <column name="author_id"/>
    </createIndex>

    <createIndex indexName="idx_articles_deleted_at" tableName="articles">
        <column name="deleted_at"/>
    </createIndex>

    <!-- Trigger auto-update search_vector -->
    <sql>
        CREATE OR REPLACE FUNCTION update_article_search_vector()
        RETURNS TRIGGER AS $$
        BEGIN
            NEW.search_vector :=
                setweight(to_tsvector('polish', COALESCE(NEW.title, '')), 'A') ||
                setweight(to_tsvector('polish', COALESCE(NEW.content, '')), 'B');
            RETURN NEW;
        END;
        $$ LANGUAGE plpgsql;

        CREATE TRIGGER update_article_search_vector_trigger
        BEFORE INSERT OR UPDATE ON articles
        FOR EACH ROW EXECUTE FUNCTION update_article_search_vector();
    </sql>

    <!-- Trigger auto-update updated_at -->
    <sql>
        CREATE TRIGGER update_articles_updated_at
        BEFORE UPDATE ON articles
        FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    </sql>
</changeSet>
```

**WAŻNE**: Sprawdź czy funkcja `update_updated_at_column()` już istnieje (powinna być utworzona wcześniej dla innych tabel).

---

### Krok 2: Domain Layer - Entities, Enums, Exceptions

**WAŻNE**: Czysta warstwa domenowa BEZ adnotacji JPA!

1. **Utwórz pakiet**: `pl.klastbit.lexpage.domain.article`
2. **Utwórz enum**: `ArticleStatus.java` (DRAFT, PUBLISHED, ARCHIVED)
3. **Utwórz entity**: `Article.java` - **CZYSTA domena** (bez JPA):
   - Używa `UserId` (Value Object) zamiast `Long` dla user IDs
   - Metody fabrykujące: `createDraft()`, `ofExisting()`
   - Metody biznesowe: `publish()`, `archive()`, `unpublish()`, `updateContent()`, `softDelete()`
   - BEZ adnotacji `@Entity`, `@Column`, etc.
4. **Utwórz pakiet exceptions**: `pl.klastbit.lexpage.domain.article.exception`
5. **Utwórz wyjątki**:
   - `ArticleNotFoundException.java`
   - `InvalidArticleStatusTransitionException.java`
6. **Utwórz port**: `ArticleRepository.java` (interface w domain, zwraca `Article` nie `ArticleEntity`)

**Pliki do utworzenia**:
- `src/main/java/pl/klastbit/lexpage/domain/article/ArticleStatus.java`
- `src/main/java/pl/klastbit/lexpage/domain/article/Article.java` ← **CZYSTA DOMENA**
- `src/main/java/pl/klastbit/lexpage/domain/article/ArticleRepository.java`
- `src/main/java/pl/klastbit/lexpage/domain/article/exception/ArticleNotFoundException.java`
- `src/main/java/pl/klastbit/lexpage/domain/article/exception/InvalidArticleStatusTransitionException.java`

---

### Krok 3: Application Layer - Use Cases, Commands, DTOs

1. **Utwórz pakiet**: `pl.klastbit.lexpage.application.article`
2. **Utwórz pakiet commands**: `pl.klastbit.lexpage.application.article.command`
3. **Utwórz commands**: `CreateArticleCommand.java`, `UpdateArticleCommand.java` (Records)
4. **Utwórz pakiet DTOs**: `pl.klastbit.lexpage.application.article.dto`
5. **Utwórz DTOs**: `ArticleListItemDto.java`, `ArticleDetailDto.java`, `PageDto.java` (Records)
6. **Utwórz use case interfaces**:
   - `CreateArticleUseCase.java`
   - `UpdateArticleUseCase.java`
   - `DeleteArticleUseCase.java`
   - `PublishArticleUseCase.java`
   - `ArchiveArticleUseCase.java`
   - `GetArticleUseCase.java`
   - `ListArticlesUseCase.java`
7. **Utwórz application service**: `ArticleApplicationService.java` (implementuje wszystkie use cases)

**Pliki do utworzenia**:
- `src/main/java/pl/klastbit/lexpage/application/article/command/CreateArticleCommand.java`
- `src/main/java/pl/klastbit/lexpage/application/article/command/UpdateArticleCommand.java`
- `src/main/java/pl/klastbit/lexpage/application/article/dto/ArticleListItemDto.java`
- `src/main/java/pl/klastbit/lexpage/application/article/dto/ArticleDetailDto.java`
- `src/main/java/pl/klastbit/lexpage/application/article/dto/PageDto.java`
- `src/main/java/pl/klastbit/lexpage/application/article/*.java` (7 use case interfaces)
- `src/main/java/pl/klastbit/lexpage/application/article/ArticleApplicationService.java`

---

### Krok 4: Infrastructure Layer - Persistence Entities & Mappers

**WAŻNE**: JPA entities z adnotacjami + Mapper do konwersji domain ↔ persistence!

1. **Utwórz pakiet**: `pl.klastbit.lexpage.infrastructure.adapters.persistence.entity`
2. **Utwórz JPA entity**: `ArticleEntity.java` - **JPA ENTITY** (z adnotacjami):
   - Extends `BaseEntity` (createdAt, updatedAt, deletedAt już są)
   - Używa `@Entity`, `@Table`, `@Column`, etc.
   - Używa `UserEntity` jako relacje (`@ManyToOne`)
   - Używa `@SQLDelete` i `@SQLRestriction` dla soft delete
3. **Utwórz pakiet**: `pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper`
4. **Utwórz mapper**: `ArticleMapper.java` - **MAPPER**:
   - Metoda `toDomain(ArticleEntity)` → `Article`
   - Metoda `toEntity(Article)` → `ArticleEntity`
   - Metoda `updateEntity(ArticleEntity, Article)` - update bez nadpisywania relacji
   - Metoda `setUserReferences(...)` - ustawia relacje UserEntity
   - Helper methods: `getAuthorId()`, `getCreatedById()`, `getUpdatedById()`
   - Helper methods: `listToArray()`, `arrayToList()` dla keywords

**Pliki do utworzenia**:
- `src/main/java/pl/klastbit/lexpage/infrastructure/adapters/persistence/entity/ArticleEntity.java` ← **JPA ENTITY**
- `src/main/java/pl/klastbit/lexpage/infrastructure/adapters/persistence/mapper/ArticleMapper.java` ← **MAPPER**

---

### Krok 5: Infrastructure Layer - Repository Adapters

**WAŻNE**: Adapter używa mappera do konwersji między domain a persistence!

1. **Utwórz pakiet**: `pl.klastbit.lexpage.infrastructure.adapters.persistence.repository`
2. **Utwórz Spring Data JPA Repository**: `SpringDataArticleRepository.java`
   - Interface extends `JpaRepository<ArticleEntity, Long>` ← używa **ArticleEntity**!
   - Query methods: `findByIdAndDeletedAtIsNull()`, etc.
   - Full-text search: native query używający `search_vector`
3. **Utwórz Repository Adapter**: `JpaArticleRepository.java`
   - Implementuje `ArticleRepository` (port z domain)
   - Używa `ArticleMapper` do konwersji
   - Używa `SpringDataUserRepository` do ładowania `UserEntity`
   - Metoda `save()` mapuje domain → entity, ustawia relacje UserEntity, zapisuje, mapuje z powrotem
   - Wszystkie metody find mapują `ArticleEntity` → `Article` przez mapper

**Pliki do utworzenia**:
- `src/main/java/pl/klastbit/lexpage/infrastructure/adapters/persistence/repository/SpringDataArticleRepository.java`
- `src/main/java/pl/klastbit/lexpage/infrastructure/adapters/persistence/repository/JpaArticleRepository.java` ← **używa MAPPERA**

---

### Krok 6: Infrastructure Layer - REST Controller

1. **Utwórz pakiet**: `pl.klastbit.lexpage.infrastructure.web.controller`
2. **Utwórz pakiet DTOs**: `pl.klastbit.lexpage.infrastructure.web.controller.dto`
3. **Utwórz Request DTOs**: `CreateArticleRequest.java`, `UpdateArticleRequest.java` (Records z walidacją)
4. **Utwórz Response DTOs**: `ArticleResponse.java`, `ArticleListResponse.java` (Records)
5. **Utwórz kontroler**: `ArticleController.java` (REST endpoints)

**Pliki do utworzenia**:
- `src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/dto/CreateArticleRequest.java`
- `src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/dto/UpdateArticleRequest.java`
- `src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/dto/ArticleResponse.java`
- `src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/dto/ArticleListResponse.java`
- `src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/ArticleController.java`

---

### Krok 7: Global Exception Handler

1. **Sprawdź czy istnieje**: `GlobalExceptionHandler.java` (może już istnieć z innych features)
2. **Dodaj handlery** dla:
   - `ArticleNotFoundException` → 404
   - `InvalidArticleStatusTransitionException` → 400
   - `MethodArgumentNotValidException` → 400 (walidacja)
   - `IllegalArgumentException` → 400
   - `Exception` → 500

**Plik do edycji/utworzenia**:
- `src/main/java/pl/klastbit/lexpage/infrastructure/web/exception/GlobalExceptionHandler.java`

---

### Krok 8: Testy Jednostkowe

1. **Testy domain layer**:
   - `ArticleTest.java` - testowanie logiki `publish()`, `archive()`, `softDelete()`

2. **Testy application layer**:
   - `ArticleApplicationServiceTest.java` - mockowanie `ArticleRepository`, testowanie use cases

3. **Testy infrastructure layer**:
   - `ArticleControllerTest.java` - testy integracyjne z `@WebMvcTest` lub `@SpringBootTest`

**Pliki do utworzenia**:
- `src/test/java/pl/klastbit/lexpage/domain/article/ArticleTest.java`
- `src/test/java/pl/klastbit/lexpage/application/article/ArticleApplicationServiceTest.java`
- `src/test/java/pl/klastbit/lexpage/infrastructure/web/controller/ArticleControllerTest.java`

**Przykładowy test domain** (czysty, bez JPA):
```java
package pl.klastbit.lexpage.domain.article;

import org.junit.jupiter.api.Test;
import pl.klastbit.lexpage.domain.user.UserId;

import static org.assertj.core.api.Assertions.*;

class ArticleTest {

    @Test
    void shouldPublishArticleWhenStatusIsDraft() {
        // Arrange
        Article article = Article.createDraft(
            "Test Title",
            "test-slug",
            "Test content with enough words to pass validation",
            "Excerpt",
            null, null, null, null, null,
            UserId.of(1L)
        );

        // Act
        article.publish();

        // Assert
        assertThat(article.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
        assertThat(article.getPublishedAt()).isNotNull();
        assertThat(article.isPublished()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenPublishingAlreadyPublishedArticle() {
        // Arrange
        Article article = Article.createDraft(
            "Test Title",
            "test-slug",
            "Test content",
            null, null, null, null, null, null,
            UserId.of(1L)
        );
        article.publish(); // First publish

        // Act & Assert
        assertThatThrownBy(() -> article.publish())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Article is already published");
    }

    @Test
    void shouldArchiveOnlyPublishedArticle() {
        // Arrange
        Article article = Article.createDraft(
            "Test Title",
            "test-slug",
            "Test content",
            null, null, null, null, null, null,
            UserId.of(1L)
        );
        article.publish();

        // Act
        article.archive();

        // Assert
        assertThat(article.getStatus()).isEqualTo(ArticleStatus.ARCHIVED);
    }

    @Test
    void shouldThrowExceptionWhenArchivingNonPublishedArticle() {
        // Arrange
        Article article = Article.createDraft(
            "Test Title",
            "test-slug",
            "Test content",
            null, null, null, null, null, null,
            UserId.of(1L)
        );

        // Act & Assert
        assertThatThrownBy(() -> article.archive())
            .isInstanceOf(InvalidArticleStatusTransitionException.class);
    }

    @Test
    void shouldSoftDeleteArticle() {
        // Arrange
        Article article = Article.createDraft(
            "Test Title",
            "test-slug",
            "Test content",
            null, null, null, null, null, null,
            UserId.of(1L)
        );

        // Act
        article.softDelete();

        // Assert
        assertThat(article.isDeleted()).isTrue();
        assertThat(article.getDeletedAt()).isNotNull();
    }
}
```

**Przykładowy test mappera**:
```java
package pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper;

import org.junit.jupiter.api.Test;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.user.UserId;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ArticleMapperTest {

    private final ArticleMapper mapper = new ArticleMapper();

    @Test
    void shouldMapArticleEntityToDomain() {
        // Arrange
        UserEntity author = createUserEntity(1L, "john");
        ArticleEntity entity = createArticleEntity(1L, "Test Title", author);

        // Act
        Article domain = mapper.toDomain(entity);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getTitle()).isEqualTo("Test Title");
        assertThat(domain.getAuthorId()).isEqualTo(UserId.of(1L));
        assertThat(domain.getStatus()).isEqualTo(ArticleStatus.DRAFT);
    }

    @Test
    void shouldMapArticleDomainToEntity() {
        // Arrange
        Article domain = Article.createDraft(
            "Test Title",
            "test-slug",
            "Test content",
            "Excerpt",
            null, null, null, null,
            List.of("keyword1", "keyword2"),
            UserId.of(1L)
        );

        // Act
        ArticleEntity entity = mapper.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("Test Title");
        assertThat(entity.getSlug()).isEqualTo("test-slug");
        assertThat(entity.getStatus()).isEqualTo(ArticleStatus.DRAFT);
        assertThat(entity.getKeywords()).containsExactly("keyword1", "keyword2");
        // NOTE: UserEntity references are NOT set by toEntity(), must be set via setUserReferences()
    }

    @Test
    void shouldUpdateEntityFromDomain() {
        // Arrange
        UserEntity author = createUserEntity(1L, "john");
        ArticleEntity entity = createArticleEntity(1L, "Old Title", author);

        Article domain = Article.ofExisting(
            1L, "New Title", "new-slug", "New content", "New excerpt",
            ArticleStatus.DRAFT, UserId.of(1L), null,
            null, null, null, null, null,
            UserId.of(1L), UserId.of(1L),
            LocalDateTime.now(), LocalDateTime.now(), null
        );

        // Act
        mapper.updateEntity(entity, domain);

        // Assert
        assertThat(entity.getTitle()).isEqualTo("New Title");
        assertThat(entity.getSlug()).isEqualTo("new-slug");
        assertThat(entity.getContent()).isEqualTo("New content");
        // UserEntity references are preserved
        assertThat(entity.getAuthor()).isEqualTo(author);
    }

    private UserEntity createUserEntity(Long id, String username) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private ArticleEntity createArticleEntity(Long id, String title, UserEntity author) {
        ArticleEntity entity = new ArticleEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setSlug("test-slug");
        entity.setContent("Test content");
        entity.setStatus(ArticleStatus.DRAFT);
        entity.setAuthor(author);
        entity.setCreatedBy(author);
        entity.setUpdatedBy(author);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
```

---

### Krok 9: Integracja z Spring Security

**Zadanie**: Pobranie `userId` z kontekstu zalogowanego użytkownika.

**Opcje**:
1. **Custom UserDetails** - rozszerz `UserDetails` o pole `userId`
2. **@AuthenticationPrincipal** - użyj adnotacji w kontrolerze
3. **SecurityContextHolder** - bezpośredni dostęp do kontekstu

**Przykład Custom UserDetails**:
```java
public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // konstruktor, gettery

    public Long getUserId() {
        return userId;
    }
}
```

**Użycie w kontrolerze**:
```java
@PostMapping
public ResponseEntity<ArticleResponse> createArticle(
        @Valid @RequestBody CreateArticleRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
) {
    Long userId = userDetails.getUserId();
    // ...
}
```

---

### Krok 10: Uruchomienie i Testowanie API

1. **Uruchom aplikację**: `./gradlew bootRun`
2. **Testuj endpointy** przez Postman/curl:

```bash
# Create article
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "Jak napisać pozew o zapłatę?",
    "content": "<h2>Wprowadzenie</h2><p>Pozew o zapłatę...</p>",
    "excerpt": "Krótki opis",
    "keywords": ["pozew", "zapłata"]
  }'

# List articles
curl -X GET "http://localhost:8080/api/articles?page=0&size=10&status=PUBLISHED"

# Get article
curl -X GET http://localhost:8080/api/articles/1

# Publish article
curl -X PATCH http://localhost:8080/api/articles/1/publish \
  -H "Authorization: Bearer YOUR_TOKEN"

# Delete article
curl -X DELETE http://localhost:8080/api/articles/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 6. Uwagi Implementacyjne

### 6.0. Rozdzielenie Domain ↔ Persistence (KRYTYCZNE!)

**ZASADA GŁÓWNA**: Domain i Persistence są CAŁKOWICIE ODDZIELONE!

#### Domain Entity (`Article`)
- **Lokalizacja**: `pl.klastbit.lexpage.domain.article.Article`
- **Charakterystyka**:
  - CZYSTA logika biznesowa (metody: `publish()`, `archive()`, `updateContent()`)
  - BEZ adnotacji JPA (`@Entity`, `@Column`, etc.)
  - Używa `UserId` (Value Object) zamiast `Long` dla user IDs
  - Nie zna o `UserEntity` (relacje JPA)
  - Metody fabrykujące: `createDraft()`, `ofExisting()`

#### Persistence Entity (`ArticleEntity`)
- **Lokalizacja**: `pl.klastbit.lexpage.infrastructure.adapters.persistence.entity.ArticleEntity`
- **Charakterystyka**:
  - JPA entity z pełnymi adnotacjami
  - Używa `UserEntity` jako relacje (`@ManyToOne`)
  - Używa `String[]` dla keywords (PostgreSQL array)
  - Extends `BaseEntity` (timestamps)
  - Soft delete przez `@SQLDelete` i `@SQLRestriction`
  - BEZ logiki biznesowej (tylko gettery/settery)

#### Mapper (`ArticleMapper`)
- **Lokalizacja**: `pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper.ArticleMapper`
- **Odpowiedzialność**:
  - Konwersja `ArticleEntity` → `Article` (toDomain)
  - Konwersja `Article` → `ArticleEntity` (toEntity)
  - Update entity z domain (updateEntity)
  - Ustawianie relacji `UserEntity` (setUserReferences)
  - Konwersja `List<String>` ↔ `String[]` (keywords)
  - Konwersja `UserEntity` ↔ `UserId`

#### Repository Adapter (`JpaArticleRepository`)
- **Odpowiedzialność**:
  - Implementuje `ArticleRepository` (port z domain)
  - Używa `ArticleMapper` do wszystkich konwersji
  - Zarządza relacjami `UserEntity` (ładuje przez `SpringDataUserRepository`)
  - Metoda `save()`:
    1. Mapuje `Article` → `ArticleEntity` (przez mapper)
    2. Ładuje `UserEntity` dla author/createdBy/updatedBy
    3. Ustawia relacje przez `mapper.setUserReferences()`
    4. Zapisuje przez Spring Data
    5. Mapuje z powrotem `ArticleEntity` → `Article`

**KORZYŚCI**:
- ✅ Domain jest niezależny od JPA/Hibernate
- ✅ Łatwiejsze testowanie domeny (bez mockowania DB)
- ✅ Możliwość zmiany persistence layer bez zmiany domeny
- ✅ Czysta architektura heksagonalna

---

### 6.1. Slug Generation
- Używaj biblioteki np. `com.github.slugify:slugify` lub własnej implementacji
- Transliteracja polskich znaków: ą→a, ć→c, ę→e, ł→l, etc.
- Lowercase, usunięcie znaków specjalnych, zamiana spacji na `-`
- Sprawdzenie unikalności i dodanie sufiksu `-1`, `-2` jeśli duplikat

### 6.2. Meta Description Auto-generation
- Jeśli `metaDescription` jest null/blank, wygeneruj z `content`
- Usuń HTML tags: `content.replaceAll("<[^>]+>", "")`
- Obetnij do 160 znaków
- Dodaj `...` na końcu jeśli obcięto

### 6.3. Full-Text Search
- Użyj kolumny `search_vector` typu TSVECTOR
- PostgreSQL trigger auto-update przy INSERT/UPDATE
- Query: `to_tsvector('polish', title) @@ plainto_tsquery('polish', :keyword)`
- GIN index na `search_vector` dla wydajności

### 6.4. Soft Delete Pattern
- Wszystkie queries MUSZĄ filtrować `WHERE deleted_at IS NULL`
- Spring Data JPA: `findAllByDeletedAtIsNull()`
- Możliwość odzyskania przez ustawienie `deleted_at = NULL` (wymaga osobnego endpointu)

### 6.5. Audit Trail
- `created_by`, `updated_by` ustawiane automatycznie z Spring Security context
- `created_at`, `updated_at` ustawiane przez JPA `@PrePersist`, `@PreUpdate` lub DB trigger
- NIE pozwalaj na ręczną zmianę tych pól przez API

### 6.6. Paginacja
- Spring Data Pageable: `PageRequest.of(page, size, sort)`
- Limit max `size=100` (walidacja w kontrolerze)
- Response zawiera `totalElements`, `totalPages` dla frontend pagination UI

### 6.7. Walidacja
- Bean Validation adnotacje: `@NotBlank`, `@Size`, `@Min`, `@Max`
- Walidacja na poziomie request DTO (infrastructure layer)
- Domain validation w metodach agregatu (np. `publish()` sprawdza status)
- Global Exception Handler obsługuje `MethodArgumentNotValidException`

### 6.8. Security
- Wszystkie endpointy modyfikujące (POST, PUT, DELETE, PATCH) wymagają autentykacji
- Endpoint GET może być publiczny (w zależności od wymagań)
- Rate limiting dla zapobiegania abuse (opcjonalne dla MVP)

---

## 7. Potencjalne Rozszerzenia (Poza MVP)

### 7.1. Restore Soft-Deleted Article
**Endpoint**: `PATCH /api/articles/{id}/restore`

### 7.2. Bulk Operations
- `POST /api/articles/bulk-publish` - publikacja wielu artykułów
- `POST /api/articles/bulk-delete` - usunięcie wielu artykułów

### 7.3. Article Versioning
- Historia zmian artykułu
- Możliwość rollback do poprzedniej wersji

### 7.4. Related Articles
- `GET /api/articles/{id}/related` - podobne artykuły (na podstawie keywords)

### 7.5. Article Images Management
- Integracja z tabelą `images` (polymorphic relationship)
- Upload i przypisywanie obrazów do artykułu

### 7.6. Comments System
- Dodanie tabeli `article_comments`
- CRUD dla komentarzy z moderacją

### 7.7. Tags/Categories
- Dodanie tabeli `tags` i `article_tags` (many-to-many)
- Filtrowanie artykułów po tagach

---

## 8. Checklist Implementacji

- [ ] **Krok 1**: Migracja Liquibase - sprawdzenie/utworzenie tabeli `articles`
- [ ] **Krok 2**: Domain layer - `Article` (CZYSTA DOMENA), `ArticleStatus`, `ArticleRepository` (port), exceptions
- [ ] **Krok 3**: Application layer - use cases, commands, DTOs, `ArticleApplicationService`
- [ ] **Krok 4**: Infrastructure persistence entities & mappers - `ArticleEntity` (JPA), `ArticleMapper` (domain ↔ persistence)
- [ ] **Krok 5**: Infrastructure persistence repositories - `SpringDataArticleRepository`, `JpaArticleRepository` (adapter z mapperem)
- [ ] **Krok 6**: Infrastructure web - request/response DTOs, `ArticleController`
- [ ] **Krok 7**: Global Exception Handler - dodanie handlerów dla `ArticleNotFoundException`, etc.
- [ ] **Krok 8**: Testy jednostkowe - domain, application, infrastructure
- [ ] **Krok 9**: Integracja Spring Security - pobranie `userId` z kontekstu
- [ ] **Krok 10**: Uruchomienie i testowanie API - Postman/curl

---

## 9. Dodatkowe Zasoby

### 9.1. Zależności Gradle (build.gradle.kts)

```kotlin
dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql")

    // Liquibase
    implementation("org.liquibase:liquibase-core")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}
```

### 9.2. Application Properties (application.properties)

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/lexpage
spring.datasource.username=postgres
spring.datasource.password=password

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Liquibase
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

# Logging
logging.level.pl.klastbit.lexpage=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

---

## 10. Podsumowanie

Ten plan implementacji pokrywa:
- ✅ **7 endpointów REST API** do zarządzania artykułami
- ✅ **Architektura heksagonalna** (Domain → Application → Infrastructure)
- ✅ **DDD principles** (Aggregate Root, Domain Events, Value Objects)
- ✅ **Zgodność z backend.md** (Lombok, Records, ProblemDetail, no custom ENUMs)
- ✅ **Soft delete pattern** z `deleted_at`
- ✅ **Audit trail** (created_by, updated_by, timestamps)
- ✅ **Paginacja i filtrowanie** listy artykułów
- ✅ **Full-text search** (PostgreSQL tsvector)
- ✅ **Bean Validation** z Global Exception Handler
- ✅ **Kontrakt API** gotowy dla frontendu

Implementacja powinna zająć **2-3 dni** dla doświadczonego backend developera.

---

## 11. Kluczowe Różnice: Domain vs Persistence

| Aspekt | Domain (`Article`) | Persistence (`ArticleEntity`) |
|--------|-------------------|-------------------------------|
| **Lokalizacja** | `domain.article.Article` | `infrastructure.adapters.persistence.entity.ArticleEntity` |
| **Adnotacje JPA** | ❌ NIE (czysta POJO) | ✅ TAK (`@Entity`, `@Column`, etc.) |
| **Logika biznesowa** | ✅ TAK (publish, archive, updateContent) | ❌ NIE (tylko gettery/settery) |
| **User references** | `UserId` (Value Object) | `UserEntity` (relacja `@ManyToOne`) |
| **Keywords** | `List<String>` | `String[]` (PostgreSQL array) |
| **Testowanie** | Proste unit testy bez DB | Wymaga JPA/Hibernate context |
| **Zależności** | Tylko Java stdlib | Spring Data JPA, Hibernate |
| **Konstruktory** | Factory methods (createDraft, ofExisting) | @NoArgsConstructor dla JPA |
| **Walidacja** | Domain validation w metodach | Bean Validation adnotacje |

### Przepływ Danych (Save)

```
Application Service
    ↓
    Article (domain)
    ↓
JpaArticleRepository (adapter)
    ↓
ArticleMapper.toEntity()
    ↓
    ArticleEntity (JPA)
    ↓
Load UserEntity references (author, createdBy, updatedBy)
    ↓
ArticleMapper.setUserReferences()
    ↓
SpringDataArticleRepository.save()
    ↓
    Database (PostgreSQL)
    ↓
ArticleMapper.toDomain()
    ↓
    Article (domain)
    ↓
Application Service → DTO → Controller
```

### Przepływ Danych (Find)

```
Application Service
    ↓
JpaArticleRepository (adapter)
    ↓
SpringDataArticleRepository.findById()
    ↓
    ArticleEntity (JPA) with UserEntity references
    ↓
ArticleMapper.toDomain()
    ↓
    Article (domain) with UserId
    ↓
Application Service → DTO → Controller
```

---

**Status**: Gotowy do implementacji przez agenta AI
**Data utworzenia**: 2026-01-26
**Ostatnia aktualizacja**: 2026-01-26 (dodano mappers i rozdzielenie domain/persistence)
**Autor**: Claude Code
**Wersja**: 2.0