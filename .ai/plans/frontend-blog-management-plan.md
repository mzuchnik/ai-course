# Frontend Blog Management - Plan Implementacji

## 1. Przegląd Funkcjonalności

Implementacja publicznego widoku bloga dla użytkowników (frontend) z następującymi funkcjami:

**Strona Listy Artykułów** (`/blog`):
- Wyświetlenie wszystkich opublikowanych artykułów (status: PUBLISHED)
- Layout: 3x3 grid (9 artykułów na stronie)
- Paginacja na dole strony
- Card layout z excerpt, datą publikacji, obrazem (og:image)
- Mobile-first responsive design

**Strona Szczegółów Artykułu** (`/blog/{slug}`):
- Pełna treść artykułu (HTML content)
- Breadcrumbs nawigacyjne (Blog → Tytuł artykułu)
- Data publikacji
- SEO meta tags (Open Graph, Twitter Cards)
- Related articles (3-4 podobne artykuły na dole strony)
- Responsywny layout z optymalną czytelnością

---

## 2. Routing i Endpointy Spring MVC

### 2.1. GET /blog - Lista Artykułów

**URL Pattern:**
```
/blog
/blog?page=0
/blog?page=1
```

**Controller:** `BlogViewController` (nowy controller w `infrastructure/web/controller/`)

**Metoda:**
- Nazwa: `listArticles()`
- Parametry: `@RequestParam(defaultValue = "0") int page`
- Zwraca: `String` → template `"pages/blog/index"`
- Model attributes:
  - `pageTitle` - "Blog - Lexpage"
  - `pageDescription` - SEO description dla listy blogów
  - `articles` - `PageDto<ArticleListItemDto>` (z application layer)
  - `currentPage` - numer bieżącej strony
  - `totalPages` - łączna liczba stron

**Backend API Call:**
- Endpoint: `GET /api/articles?page={page}&size=9&status=PUBLISHED&sort=publishedAt,desc`
- Sortowanie: najnowsze artykuły na górze (publishedAt desc)
- Filtrowanie: tylko PUBLISHED
- Size: 9 (3x3 grid)

### 2.2. GET /blog/{slug} - Szczegóły Artykułu

**URL Pattern:**
```
/blog/jak-napisac-pozew-o-zaplate
/blog/{slug}
```

**Controller:** `BlogViewController`

**Metoda:**
- Nazwa: `viewArticle()`
- Parametry: `@PathVariable String slug`
- Zwraca: `String` → template `"pages/blog/article"`
- Model attributes:
  - `pageTitle` - `{article.metaTitle}` lub `{article.title} - Lexpage`
  - `pageDescription` - `{article.metaDescription}`
  - `article` - `ArticleDetailDto` (z application layer)
  - `breadcrumbs` - Lista breadcrumb items
  - `relatedArticles` - Lista 3-4 podobnych artykułów
  - `ogImageUrl` - Open Graph image
  - `canonicalUrl` - Canonical URL dla SEO
  - `keywords` - Meta keywords

**Backend API Call:**
- Najpierw: `GET /api/articles?slug={slug}&status=PUBLISHED` (wymaga dodania metody w repository do wyszukiwania po slug)
- Alternatywnie: `GET /api/articles/{id}` (jeśli najpierw mapujemy slug → id)
- Related articles: `GET /api/articles?status=PUBLISHED&size=4&sort=publishedAt,desc` (exclude current article)

**Error Handling:**
- 404 Not Found jeśli artykuł nie istnieje lub nie jest PUBLISHED
- Redirect do /blog z error message

---

## 3. Struktura Katalogów i Plików

```
src/main/resources/templates/
├── pages/
│   └── blog/
│       ├── index.html              # Lista artykułów (/blog)
│       └── article.html            # Szczegóły artykułu (/blog/{slug})
│
└── fragments/
    └── components/
        ├── blog/
        │   ├── article-card.html   # Card artykułu (dla listy)
        │   ├── article-content.html # Treść artykułu z formatowaniem
        │   └── related-articles.html # Sekcja related articles
        └── breadcrumbs.html         # JUŻ ISTNIEJE - wykorzystaj go

src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/
└── BlogViewController.java          # Nowy controller
```

---

## 4. Komponenty Material Tailwind do Wykorzystania

### 4.1. Istniejące Komponenty (z `fragments/components/`)

**DO WYKORZYSTANIA:**
- `breadcrumbs.html` - Nawigacja breadcrumb na stronie artykułu
- `cards.html` - Base card dla article card (jeśli pasuje)
- `pagination.html` - Paginacja na dole listy blogów
- `typography.html` - Headingi, paragrafy, formatowanie tekstu

### 4.2. Nowe Komponenty do Utworzenia

**`fragments/components/blog/article-card.html`:**
- Fragment: `articleCard(article)`
- Parametry:
  - `article` (ArticleListItemDto) - dane artykułu
- Layout:
  - Obraz (og:image lub placeholder jeśli brak)
  - Tytuł artykułu (h3)
  - Excerpt (max 2-3 linie, truncated)
  - Data publikacji (formatowana: "26 stycznia 2026")
  - "Czytaj więcej →" link
- Responsywność: full width na mobile, 1/3 width na desktop
- Hover effect: shadow-lg, scale subtle

**`fragments/components/blog/article-content.html`:**
- Fragment: `articleContent(content)`
- Parametry:
  - `content` (String) - HTML content artykułu
- Features:
  - Safe HTML rendering (Thymeleaf `th:utext`)
  - Typography classes dla content (prose, prose-lg)
  - Responsive images w content
  - Code blocks styling (jeśli są w content)
  - Linkowanie zewnętrznych linków z target="_blank"

**`fragments/components/blog/related-articles.html`:**
- Fragment: `relatedArticles(articles)`
- Parametry:
  - `articles` (List<ArticleListItemDto>) - 3-4 podobne artykuły
- Layout:
  - Heading "Podobne artykuły"
  - Grid 3 kolumny na desktop, 1 kolumna na mobile
  - Mniejsze karty artykułów (compact variant)
  - Link do /blog na dole ("Zobacz wszystkie artykuły →")

---

## 5. Page Templates - Szczegółowa Struktura

### 5.1. `pages/blog/index.html` - Lista Artykułów

**Layout:** `layouts/main.html` (navbar + footer)

**Struktura:**
1. **Hero Section** (opcjonalnie, dla lepszego UX):
   - Gradient background (primary color)
   - Heading "Blog" (h1)
   - Subtitle/description
   - Wysokość: 200px na desktop, 150px na mobile

2. **Articles Grid Section**:
   - Container: `max-w-7xl mx-auto px-4 py-12`
   - Grid: `grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6`
   - Iterate przez `articles.content` i renderuj `article-card` dla każdego
   - Empty state: jeśli brak artykułów, wyświetl message "Brak artykułów do wyświetlenia"

3. **Pagination Section**:
   - Component: `~{fragments/components/pagination :: pagination(...)}`
   - Parametry:
     - currentPage: `${currentPage}`
     - totalPages: `${totalPages}`
     - baseUrl: `'/blog'`
     - paramName: `'page'`
     - variant: `'numbered'`
     - showPrevNext: `true`
     - maxVisible: `7`

**SEO:**
- Page title: "Blog - Lexpage"
- Meta description: "Odkryj nasze najnowsze artykuły prawnicze. Porady, analizy i praktyczne wskazówki dla każdego."
- Canonical URL: `https://example.com/blog?page={page}`
- No indexing dla page > 0: `<meta th:if="${currentPage > 0}" name="robots" content="noindex, follow">`

### 5.2. `pages/blog/article.html` - Szczegóły Artykułu

**Layout:** `layouts/main.html` (navbar + footer)

**Struktura:**
1. **Breadcrumbs Section**:
   - Component: `~{fragments/components/breadcrumbs :: breadcrumbs(...)}`
   - Items:
     ```java
     List.of(
       Map.of("text", "Blog", "url", "/blog"),
       Map.of("text", article.title, "url", null) // current page, no link
     )
     ```
   - Container: `max-w-4xl mx-auto px-4 py-4`

2. **Article Header**:
   - Container: `max-w-4xl mx-auto px-4`
   - Heading (h1): article.title - `~{fragments/components/typography :: h1(...)}`
   - Meta info:
     - Published date: formatowana jako "Opublikowano: 26 stycznia 2026"
     - Typography: `text-gray-600 text-sm`
   - Featured image (jeśli ogImageUrl):
     - Full width, rounded, aspect ratio 16:9
     - Alt text: article.title

3. **Article Content Section**:
   - Container: `max-w-4xl mx-auto px-4 py-8`
   - Component: `~{fragments/components/blog/article-content :: articleContent(${article.content})}`
   - Typography: Użyj Tailwind Typography plugin (prose classes):
     - `prose prose-lg max-w-none`
     - `prose-headings:font-bold prose-headings:text-gray-900`
     - `prose-a:text-primary-600 prose-a:no-underline hover:prose-a:underline`
     - `prose-img:rounded-lg`

4. **Related Articles Section**:
   - Container: `max-w-7xl mx-auto px-4 py-12 bg-gray-50`
   - Component: `~{fragments/components/blog/related-articles :: relatedArticles(${relatedArticles})}`
   - Conditional: tylko jeśli `relatedArticles` nie jest puste

**SEO:**
- Page title: `${article.metaTitle}` lub `${article.title} - Lexpage`
- Meta description: `${article.metaDescription}`
- Meta keywords: `${article.keywords}` (join with comma)
- Canonical URL: `${article.canonicalUrl}` lub `/blog/${article.slug}`
- Open Graph tags:
  - `og:title`: `${article.metaTitle}`
  - `og:description`: `${article.metaDescription}`
  - `og:image`: `${article.ogImageUrl}`
  - `og:type`: "article"
  - `og:url`: canonical URL
  - `article:published_time`: `${article.publishedAt}`
- Twitter Card tags:
  - `twitter:card`: "summary_large_image"
  - `twitter:title`: `${article.metaTitle}`
  - `twitter:description`: `${article.metaDescription}`
  - `twitter:image`: `${article.ogImageUrl}`
- Schema.org structured data (JSON-LD):
  ```json
  {
    "@context": "https://schema.org",
    "@type": "Article",
    "headline": "${article.title}",
    "description": "${article.metaDescription}",
    "image": "${article.ogImageUrl}",
    "datePublished": "${article.publishedAt}",
    "dateModified": "${article.updatedAt}",
    "author": {
      "@type": "Organization",
      "name": "Lexpage"
    }
  }
  ```

---

## 6. Spring MVC Controller - Szczegółowa Implementacja

### 6.1. BlogViewController.java

**Package:** `pl.klastbit.lexpage.infrastructure.web.controller`

**Annotations:**
- `@Controller` (NOT @RestController - zwracamy views, nie JSON)
- `@RequiredArgsConstructor` (Lombok)
- `@Slf4j` (logging)

**Dependencies (Constructor Injection):**
- `ListArticlesUseCase` - do pobierania listy artykułów
- `GetArticleUseCase` - do pobierania pojedynczego artykułu (wymaga dodania metody `findBySlug()`)
- Alternatywnie: użyj istniejących use cases i dodaj mapowanie slug

**Metoda 1: listArticles()**
- Path: `@GetMapping("/blog")`
- Parametry:
  - `@RequestParam(defaultValue = "0") int page`
  - `Model model`
- Logika:
  1. Walidacja: `page` >= 0 (jeśli < 0, redirect do page=0)
  2. Create `Pageable`: `PageRequest.of(page, 9, Sort.by(Sort.Direction.DESC, "publishedAt"))`
  3. Call use case: `listArticlesUseCase.execute(ArticleStatus.PUBLISHED, null, null, pageable)`
  4. Add to model:
     - `pageTitle`: "Blog - Lexpage"
     - `pageDescription`: "Odkryj nasze najnowsze artykuły prawnicze..."
     - `articles`: result from use case
     - `currentPage`: page
     - `totalPages`: `articles.page().totalPages()`
  5. Return: `"pages/blog/index"`
- Error handling: jeśli page > totalPages, redirect do ostatniej strony

**Metoda 2: viewArticle()**
- Path: `@GetMapping("/blog/{slug}")`
- Parametry:
  - `@PathVariable String slug`
  - `Model model`
- Logika:
  1. Call use case: `getArticleUseCase.findBySlug(slug)` (wymaga dodania tej metody)
     - UWAGA: Backend API nie ma jeszcze metody findBySlug, trzeba ją dodać:
       - W `ArticleRepository`: `Optional<Article> findBySlugAndStatusAndDeletedAtIsNull(String slug, ArticleStatus status)`
       - W `GetArticleUseCase`: dodaj metodę `ArticleDetailDto executeBySlug(String slug)`
       - W `ArticleApplicationService`: implementuj logikę
  2. Jeśli nie znaleziono lub status != PUBLISHED:
     - Log warning: `log.warn("Article not found or not published: {}", slug)`
     - Redirect to `/blog` z error message (użyj RedirectAttributes)
     - Return: `"redirect:/blog"`
  3. Get related articles:
     - Call: `listArticlesUseCase.execute(ArticleStatus.PUBLISHED, null, null, PageRequest.of(0, 4))`
     - Filter out current article: `relatedArticles = result.content().stream().filter(a -> !a.slug().equals(slug)).limit(3).toList()`
  4. Create breadcrumbs:
     - Lista map: `[{text: "Blog", url: "/blog"}, {text: article.title(), url: null}]`
  5. Add to model:
     - `pageTitle`: `article.metaTitle()` lub `article.title() + " - Lexpage"`
     - `pageDescription`: `article.metaDescription()`
     - `article`: article
     - `breadcrumbs`: breadcrumbs list
     - `relatedArticles`: relatedArticles
     - `ogImageUrl`: `article.ogImageUrl()`
     - `canonicalUrl`: `article.canonicalUrl()` lub `/blog/ + article.slug()`
     - `keywords`: `String.join(", ", article.keywords())`
  6. Return: `"pages/blog/article"`
- Error handling:
  - `ArticleNotFoundException` → redirect to `/blog` z error
  - `IllegalArgumentException` (invalid slug) → 400 Bad Request

**Additional Methods (Helper):**
- `formatPublishedDate(LocalDateTime publishedAt)` - formatuje datę do "26 stycznia 2026"
  - Użyj `DateTimeFormatter` z locale `Locale.forLanguageTag("pl-PL")`
  - Pattern: `"d MMMM yyyy"`

---

## 7. Backend Changes Required

### 7.1. Dodanie Metody findBySlug w Repository

**Lokalizacja:** `domain/article/ArticleRepository.java`

**Nowa metoda:**
```java
Optional<Article> findBySlugAndStatusAndDeletedAtIsNull(String slug, ArticleStatus status);
```

**Implementacja w:** `infrastructure/adapters/persistence/repository/JpaArticleRepository.java`
- Deleguj do `SpringDataArticleRepository`

**Dodaj w:** `infrastructure/adapters/persistence/repository/SpringDataArticleRepository.java`
```java
Optional<ArticleEntity> findBySlugAndStatusAndDeletedAtIsNull(String slug, ArticleStatus status);
```

### 7.2. Dodanie Metody w GetArticleUseCase

**Lokalizacja:** `application/article/GetArticleUseCase.java`

**Nowa metoda:**
```java
ArticleDetailDto executeBySlug(String slug);
```

**Implementacja w:** `application/article/ArticleApplicationService.java`
- Wywołaj `articleRepository.findBySlugAndStatusAndDeletedAtIsNull(slug, ArticleStatus.PUBLISHED)`
- Rzuć `ArticleNotFoundException` jeśli nie znaleziono
- Zwróć `ArticleDetailDto.from(...)`

### 7.3. Date Formatting w DTOs (opcjonalnie)

Jeśli chcesz formatować daty w backend zamiast frontend:
- Dodaj pole `formattedPublishedAt` w `ArticleListItemDto` i `ArticleDetailDto`
- Formatuj w service layer przed zwróceniem DTO

**REKOMENDACJA:** Formatuj daty w kontrolerze (frontend), nie w application layer. Application layer powinien zwracać surowe LocalDateTime.

---

## 8. SEO Implementation - Meta Tags w Templates

### 8.1. Base Layout (`layouts/base.html`) - Zmiany

Dodaj conditional meta tags dla Open Graph i Twitter Cards:

```html
<head>
    <title th:text="${pageTitle ?: 'Lexpage'}">Lexpage</title>
    <meta name="description" th:content="${pageDescription ?: 'Default description'}">
    <meta name="keywords" th:if="${keywords}" th:content="${keywords}">
    <link rel="canonical" th:if="${canonicalUrl}" th:href="${canonicalUrl}">

    <!-- Open Graph -->
    <meta property="og:title" th:if="${pageTitle}" th:content="${pageTitle}">
    <meta property="og:description" th:if="${pageDescription}" th:content="${pageDescription}">
    <meta property="og:image" th:if="${ogImageUrl}" th:content="${ogImageUrl}">
    <meta property="og:url" th:if="${canonicalUrl}" th:content="${canonicalUrl}">
    <meta property="og:type" th:content="${ogType ?: 'website'}">

    <!-- Twitter Card -->
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:title" th:if="${pageTitle}" th:content="${pageTitle}">
    <meta name="twitter:description" th:if="${pageDescription}" th:content="${pageDescription}">
    <meta name="twitter:image" th:if="${ogImageUrl}" th:content="${ogImageUrl}">

    <!-- Article-specific -->
    <meta property="article:published_time" th:if="${publishedAt}" th:content="${publishedAt}">
    <meta property="article:modified_time" th:if="${updatedAt}" th:content="${updatedAt}">

    <!-- ... rest of head ... -->
</head>
```

### 8.2. Article Page - Schema.org Structured Data

W `pages/blog/article.html`, na końcu fragmentu `pageContent`, dodaj:

```html
<script type="application/ld+json" th:inline="javascript">
{
  "@context": "https://schema.org",
  "@type": "Article",
  "headline": /*[[${article.title}]]*/ "",
  "description": /*[[${article.metaDescription}]]*/ "",
  "image": /*[[${article.ogImageUrl}]]*/ "",
  "datePublished": /*[[${article.publishedAt}]]*/ "",
  "dateModified": /*[[${article.updatedAt}]]*/ "",
  "author": {
    "@type": "Organization",
    "name": "Lexpage"
  }
}
</script>
```

---

## 9. Responsywność - Mobile-First Design

### 9.1. Lista Artykułów (`/blog`)

**Breakpoints:**
- **Mobile** (default): 1 kolumna, full width cards
  - `grid-cols-1`
- **Tablet** (md: 768px): 2 kolumny
  - `md:grid-cols-2`
- **Desktop** (lg: 1024px): 3 kolumny
  - `lg:grid-cols-3`

**Article Card:**
- Image aspect ratio: 16:9 (zachowane na wszystkich urządzeniach)
- Tytuł: `text-xl md:text-2xl` (większy na desktop)
- Excerpt: 2 linie truncated: `line-clamp-2`
- Padding: `p-4 md:p-6` (większy na desktop)

**Pagination:**
- Mobile: pokazuj tylko 3 numery stron + prev/next
- Desktop: pokazuj 7 numerów stron + prev/next
- Użyj parametru `maxVisible` w komponencie pagination

### 9.2. Strona Artykułu (`/blog/{slug}`)

**Content Width:**
- Max width: `max-w-4xl` (optimal reading width: ~65-75 characters per line)
- Centered: `mx-auto`
- Padding: `px-4 md:px-6 lg:px-8`

**Typography:**
- Heading (h1): `text-3xl md:text-4xl lg:text-5xl`
- Content font size: `prose-base md:prose-lg`
- Line height: `prose` classes handle this automatically

**Related Articles Grid:**
- Mobile: 1 kolumna (`grid-cols-1`)
- Tablet: 2 kolumny (`md:grid-cols-2`)
- Desktop: 3 kolumny (`lg:grid-cols-3`)

**Featured Image:**
- Mobile: full width, aspect 16:9
- Desktop: maintain aspect, max height 500px

---

## 10. Accessibility - WCAG AA Compliance

### 10.1. Semantic HTML

**Lista Artykułów:**
- Użyj `<main>` dla głównej sekcji content
- Każdy article card owinięty w `<article>` tag
- Headingi: `<h1>` dla page title, `<h2>` lub `<h3>` dla article titles

**Strona Artykułu:**
- `<article>` dla głównej treści
- `<header>` dla article header (tytuł, data)
- `<nav>` dla breadcrumbs
- Proper heading hierarchy: h1 → h2 → h3 (w content)

### 10.2. ARIA Labels

**Article Cards:**
- Link "Czytaj więcej" z `aria-label="Czytaj więcej: {article.title}"`

**Pagination:**
- `<nav aria-label="Nawigacja stron">`
- Previous button: `aria-label="Poprzednia strona"`
- Next button: `aria-label="Następna strona"`
- Current page: `aria-current="page"`

**Images:**
- Zawsze `alt` attribute: `alt="${article.title}"`
- Jeśli decorative: `alt=""` (empty, not missing)

### 10.3. Keyboard Navigation

**Article Cards:**
- Cały card powinien być klikalny (link wrapper)
- Focus state: `focus:ring-4 focus:ring-primary-300`

**Links:**
- Visible focus state
- Skip to content link (w navbar, ukryty do focus)

### 10.4. Color Contrast

**Text Readability:**
- Body text: minimum contrast ratio 4.5:1 (WCAG AA)
  - `text-gray-900` on `bg-white` ✓
  - `text-gray-700` on `bg-gray-50` ✓
- Links: `text-primary-600` minimum 4.5:1 contrast

**Buttons/Interactive:**
- Primary button: `bg-primary-600` + `text-white` minimum 3:1 (large text)
- Hover state: darker shade, sufficient contrast

---

## 11. Related Articles Logic

### 11.1. Strategia Doboru Podobnych Artykułów

**Wersja MVP (Prosta):**
1. Pobierz 4 najnowsze artykuły (PUBLISHED, sorted by publishedAt desc)
2. Wyklucz bieżący artykuł (filter by slug)
3. Zwróć 3 artykuły

**Wersja Rozszerzona (Przyszłość):**
- Filtruj po keywords (wspólne keywords z obecnym artykułem)
- Sortuj po liczbie wspólnych keywords
- Fallback do najnowszych jeśli brak wspólnych keywords

### 11.2. Implementacja w Kontrolerze

```java
// Get related articles (simple version)
PageDto<ArticleListItemDto> allArticles = listArticlesUseCase.execute(
    ArticleStatus.PUBLISHED,
    null,
    null,
    PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "publishedAt"))
);

List<ArticleListItemDto> relatedArticles = allArticles.content().stream()
    .filter(a -> !a.slug().equals(slug))  // Exclude current article
    .limit(3)
    .toList();

model.addAttribute("relatedArticles", relatedArticles);
```

### 11.3. Empty State

Jeśli brak related articles (np. to jedyny artykuł):
- Nie pokazuj sekcji "Podobne artykuły"
- Conditional w template: `th:if="${relatedArticles != null && !relatedArticles.isEmpty()}"`

---

## 12. Breadcrumbs Implementation

### 12.1. Struktura Breadcrumbs

**Format:**
```
Blog → Tytuł Artykułu
```

**Data Structure (w kontrolerze):**
```java
List<Map<String, String>> breadcrumbs = List.of(
    Map.of("text", "Blog", "url", "/blog"),
    Map.of("text", article.title(), "url", null)  // current page, no link
);
model.addAttribute("breadcrumbs", breadcrumbs);
```

### 12.2. Użycie Istniejącego Komponentu

**W `pages/blog/article.html`:**
```html
<nav th:replace="~{fragments/components/breadcrumbs :: breadcrumbs(${breadcrumbs}, 'chevron_right', '')}"></nav>
```

**Parametry:**
- `items`: `${breadcrumbs}`
- `separator`: `'chevron_right'` (Material Icon)
- `customClass`: `''` (brak dodatkowych klas)

### 12.3. SEO Benefits

- Schema.org BreadcrumbList (dodaj structured data jeśli component nie ma)
- Lepsze user navigation
- Search engines mogą wyświetlić breadcrumbs w wynikach

---

## 13. Date Formatting

### 13.1. Formatowanie w Kontrolerze (Rekomendowane)

**Helper Method:**
```java
private String formatPublishedDate(LocalDateTime publishedAt) {
    if (publishedAt == null) return null;

    DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("d MMMM yyyy", Locale.forLanguageTag("pl-PL"));

    return publishedAt.format(formatter);
}
```

**Użycie:**
```java
String formattedDate = formatPublishedDate(article.publishedAt());
model.addAttribute("formattedPublishedDate", formattedDate);
```

### 13.2. Formatowanie w Template (Alternatywa)

Jeśli wolisz formatować w Thymeleaf:
```html
<time th:text="${#temporals.format(article.publishedAt, 'd MMMM yyyy', new java.util.Locale('pl', 'PL'))}">
    26 stycznia 2026
</time>
```

**REKOMENDACJA:** Formatuj w kontrolerze dla lepszej testability i separation of concerns.

---

## 14. Error Handling i Edge Cases

### 14.1. Article Not Found (404)

**Scenario:** Użytkownik wchodzi na `/blog/nieistniejacy-slug`

**Handling:**
1. `ArticleNotFoundException` rzucony przez use case
2. Catch w kontrolerze lub global exception handler
3. Redirect do `/blog` z flash message
4. Alternatywnie: custom 404 page dla blogów

**Flash Message:**
```java
catch (ArticleNotFoundException e) {
    redirectAttributes.addFlashAttribute("error", "Artykuł nie został znaleziony.");
    return "redirect:/blog";
}
```

### 14.2. Empty Blog List

**Scenario:** Brak artykułów PUBLISHED w bazie

**Handling:**
1. Sprawdź `articles.content().isEmpty()` w template
2. Wyświetl message: "Brak artykułów do wyświetlenia. Wróć wkrótce!"
3. Opcjonalnie: CTA button do homepage

### 14.3. Invalid Page Number

**Scenario:** Użytkownik wchodzi na `/blog?page=999` (nie istnieje)

**Handling:**
1. Sprawdź `page >= totalPages` w kontrolerze
2. Redirect do ostatniej strony: `redirect:/blog?page={totalPages - 1}`

### 14.4. Article Not Published Yet

**Scenario:** Artykuł istnieje ale status != PUBLISHED (DRAFT lub ARCHIVED)

**Handling:**
1. Filtruj w query: `status=PUBLISHED`
2. Jeśli bezpośredni dostęp przez slug, rzuć `ArticleNotFoundException`
3. Ten sam handling jak 404

---

## 15. Testing Checklist

### 15.1. Manual Testing

**Lista Artykułów (`/blog`):**
- [ ] Wyświetla 9 artykułów na stronie
- [ ] Pagination działa (przejście między stronami)
- [ ] Article cards są kliklane i prowadzą do `/blog/{slug}`
- [ ] Responsive na mobile/tablet/desktop
- [ ] Empty state gdy brak artykułów
- [ ] SEO meta tags są poprawne (View Source)

**Strona Artykułu (`/blog/{slug}`):**
- [ ] Wyświetla pełną treść artykułu
- [ ] Breadcrumbs działa (link do /blog)
- [ ] Data publikacji jest sformatowana
- [ ] Related articles wyświetlają się (3 artykuły)
- [ ] Featured image wyświetla się (jeśli jest)
- [ ] Open Graph tags są poprawne (Facebook Debugger)
- [ ] Twitter Card działa (Twitter Card Validator)
- [ ] Schema.org structured data jest valid (Google Rich Results Test)
- [ ] 404 handling działa dla nieistniejącego slug

**Accessibility:**
- [ ] Keyboard navigation działa (Tab, Enter)
- [ ] Screen reader friendly (test z NVDA/JAWS)
- [ ] Color contrast WCAG AA (użyj WebAIM Contrast Checker)
- [ ] Focus states są widoczne

**Performance:**
- [ ] Images są lazy loaded
- [ ] Tailwind CSS jest minified (production)
- [ ] No console errors w browser DevTools

### 15.2. Automated Testing (Opcjonalnie)

**Unit Tests (Spring MVC):**
- Test `BlogViewController.listArticles()` - sprawdź model attributes
- Test `BlogViewController.viewArticle()` - sprawdź mapping slug → article
- Test error handling (ArticleNotFoundException)

**Integration Tests:**
- Test `/blog` endpoint - status 200, content type text/html
- Test `/blog/{slug}` endpoint - status 200 dla valid slug
- Test `/blog/{invalid}` endpoint - redirect lub 404

---

## 16. Tasks Implementation Order

### Faza 1: Backend Preparation
1. **Dodaj metodę findBySlug w backend:**
   - `ArticleRepository.findBySlugAndStatusAndDeletedAtIsNull()`
   - `GetArticleUseCase.executeBySlug()`
   - Implementacja w `ArticleApplicationService`
   - Unit tests

### Faza 2: Spring MVC Controller
2. **Utwórz `BlogViewController`:**
   - Class structure, dependencies injection
   - `listArticles()` method - lista blogów
   - `viewArticle()` method - szczegóły artykułu
   - Date formatting helper
   - Error handling

### Faza 3: Components Creation
3. **Utwórz komponenty blog-specific:**
   - `fragments/components/blog/article-card.html` - karta artykułu
   - `fragments/components/blog/article-content.html` - formatowanie treści
   - `fragments/components/blog/related-articles.html` - sekcja podobnych
   - Test każdego komponentu z mockowanymi danymi

### Faza 4: Page Templates
4. **Utwórz `pages/blog/index.html`:**
   - Hero section (opcjonalnie)
   - Articles grid z article-card
   - Pagination
   - SEO meta tags
   - Responsive breakpoints

5. **Utwórz `pages/blog/article.html`:**
   - Breadcrumbs
   - Article header (title, date, featured image)
   - Article content (z article-content component)
   - Related articles section
   - SEO meta tags (Open Graph, Twitter, Schema.org)
   - Responsive layout

### Faza 5: SEO Enhancements
6. **Dodaj SEO do base layout:**
   - Update `layouts/base.html` z Open Graph tags
   - Add Twitter Card meta tags
   - Add conditional canonical URL
   - Add Schema.org script placeholder

### Faza 6: Testing & Refinement
7. **Manual testing:**
   - Test all pages na różnych urządzeniach
   - Test accessibility (keyboard, screen reader)
   - Test SEO tags (Facebook Debugger, Google Rich Results)
   - Fix bugs

8. **Performance optimization:**
   - Lazy load images
   - Minify CSS (production build)
   - Enable Thymeleaf template cache (production)

### Faza 7: Documentation
9. **Update dokumentacji:**
   - Add `/blog` routes do API docs (jeśli masz)
   - Update README z nowymi endpointami
   - Document SEO implementation

---

## 17. Future Enhancements (Out of Scope)

Funkcje do dodania w przyszłości:
- **Search i Filtering:** Wyszukiwanie full-text, filtrowanie po kategoriach/tagach
- **Categories/Tags:** Taksonomia artykułów
- **Comments System:** Komentarze użytkowników (Disqus lub własny system)
- **Social Sharing:** Liczniki share'ów, share buttons
- **Reading Time:** Obliczanie czasu czytania (~5 min read)
- **Newsletter Subscription:** CTA do zapisu na newsletter
- **Advanced Related Articles:** ML-based recommendations lub keyword matching
- **Table of Contents:** Auto-generated TOC dla długich artykułów
- **Print Stylesheet:** Optymalizacja dla druku
- **RSS Feed:** `/blog/feed.xml` dla subskrybentów

---

## 18. Dependencies i Konfiguracja

### 18.1. Tailwind Typography Plugin (dla article content)

**Problem:** Content artykułów (HTML) potrzebuje stylowania (headingi, paragrafy, listy, kod).

**Rozwiązanie:** Dodaj Tailwind Typography plugin.

**Installation:**
```bash
npm install -D @tailwindcss/typography
```

**Configuration w `tailwind.config.js`:**
```javascript
module.exports = {
  // ... existing config
  plugins: [
    require('@tailwindcss/typography'),
    // ... other plugins
  ],
}
```

**Usage w article-content component:**
```html
<div class="prose prose-lg max-w-none">
  <!-- article content HTML here -->
</div>
```

**Customization:**
- `prose` - base typography styles
- `prose-lg` - larger font size (readable)
- `max-w-none` - no max width (control w parent container)
- Możesz dostosować kolory: `prose-headings:text-gray-900`, `prose-a:text-primary-600`, etc.

### 18.2. DateTimeFormatter Locale

Upewnij się, że aplikacja ma polskie locale dla dat.

**W `application.properties`:**
```properties
spring.web.locale=pl_PL
spring.web.locale-resolver=fixed
```

**Lub w kontrolerze:**
```java
DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("pl-PL"))
```

---

## 19. Przykładowe Dane do Testowania

Aby przetestować frontend, potrzebujesz artykułów PUBLISHED w bazie. Użyj REST API:

**Utwórz artykuły testowe:**
```bash
# Artykuł 1
POST /api/articles
{
  "title": "Jak napisać pozew o zapłatę?",
  "content": "<h2>Wprowadzenie</h2><p>Pozew o zapłatę to...</p>",
  "excerpt": "Krótki opis artykułu...",
  "metaTitle": "Jak napisać pozew o zapłatę? - Poradnik prawny",
  "metaDescription": "Kompleksowy przewodnik...",
  "ogImageUrl": "https://via.placeholder.com/1200x630",
  "keywords": ["pozew", "zapłata"]
}

# Opublikuj artykuł
PATCH /api/articles/1/publish
```

**Użyj Postman Collection lub curl do szybkiego utworzenia 10-15 artykułów testowych.**

**Placeholder images:**
- OG Image: `https://via.placeholder.com/1200x630/0ea5e9/ffffff?text=Article+Title`
- Featured image: użyj unsplash.com lub placeholder

---

## 20. Code Review Checklist dla Implementacji

Przed submitem pull requesta, sprawdź:

- [ ] **Backend Changes:**
  - [ ] Metoda `findBySlug()` dodana i działa
  - [ ] Unit tests pokrywają nową funkcjonalność
  - [ ] Logowanie (SLF4J) dodane w kontrolerze

- [ ] **Frontend Components:**
  - [ ] Wszystkie `th:replace` używają `<th:block>`, nie `<button>/<div>` (zapobiega infinite loops)
  - [ ] Wszystkie `<script>` tagi są WEWNĄTRZ fragmentu content
  - [ ] Komponenty są reusable i dobrze sparametryzowane
  - [ ] Responsive breakpoints (mobile/tablet/desktop)

- [ ] **Templates:**
  - [ ] SEO meta tags kompletne (title, description, OG, Twitter)
  - [ ] Canonical URLs ustawione
  - [ ] Schema.org structured data (JSON-LD)
  - [ ] Accessibility (ARIA labels, semantic HTML, alt text)
  - [ ] No console errors (browser DevTools)

- [ ] **Styling:**
  - [ ] Tailwind classes użyte poprawnie
  - [ ] Typography plugin skonfigurowany
  - [ ] Color contrast WCAG AA
  - [ ] Focus states widoczne

- [ ] **Testing:**
  - [ ] Manual testing na mobile/tablet/desktop
  - [ ] Keyboard navigation działa
  - [ ] Edge cases obsłużone (404, empty state)
  - [ ] SEO validators: Facebook Debugger, Twitter Card, Google Rich Results

- [ ] **Performance:**
  - [ ] Images lazy loaded (gdzie możliwe)
  - [ ] CSS minified (production build)
  - [ ] No unnecessary API calls

---

## 21. Kontakt z Backendem - Podsumowanie Endpointów

### Istniejące Endpointy (Backend API):
- `GET /api/articles?page={page}&size={size}&status={status}&sort={field,direction}` - lista artykułów z filtrowaniem
- `GET /api/articles/{id}` - szczegóły artykułu po ID

### Wymagane Nowe Endpointy (do dodania w backend):
- **DODAJ:** `GET /api/articles?slug={slug}&status=PUBLISHED` - wyszukiwanie po slug
  - Alternatywnie: dodaj metodę w repository i service layer do bezpośredniego pobierania po slug

### Controller → Backend Communication:
- **BlogViewController** NIE wywołuje REST API przez HTTP
- **BlogViewController** używa `@Autowired` use cases bezpośrednio (dependency injection)
- To jest Server-Side Rendering, nie API-first approach
- Backend logic (use cases) są dostępne w tym samym procesie JVM

---

## 22. Podsumowanie Kluczowych Decyzji

| Aspekt | Decyzja | Uzasadnienie |
|--------|---------|--------------|
| **Routing** | `/blog` i `/blog/{slug}` | SEO-friendly URLs, slug jako identyfikator |
| **Articles per page** | 9 (3x3 grid) | Balans między UX a performance |
| **Pagination** | Numbered pagination | Lepsze dla SEO niż infinite scroll |
| **Search/Filtering** | Nie w MVP | Uproszczenie, do dodania później |
| **Related Articles** | 3-4 najnowsze | Prosty algorytm, do ulepszenia (keywords matching) |
| **Author Display** | Tylko data publikacji | Bez informacji o autorze w MVP |
| **Social Sharing** | Nie w MVP | Do dodania później |
| **Breadcrumbs** | Tak | Lepsze UX i SEO |
| **Date Format** | "26 stycznia 2026" | Polskie locale, user-friendly |
| **SEO** | Full implementation | Open Graph, Twitter Cards, Schema.org |
| **Typography** | Tailwind Typography plugin | Automatyczne stylowanie HTML content |

---

**Ostatnia aktualizacja:** 2026-01-27
**Wersja:** 1.0
**Status:** Ready for Implementation
**Maintainer:** Claude AI (Sonnet 4.5)
