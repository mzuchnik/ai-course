# Plan Implementacji Panelu Administratora - Zarządzanie Blogami

## Przegląd

Ten plan opisuje implementację panelu administratora do zarządzania artykułami blogowymi. Panel będzie dostępny pod ścieżką `/admin/blogs` i umożliwi:
- Przeglądanie wszystkich artykułów z filtrowaniem po statusie
- Tworzenie nowych artykułów
- Edycję istniejących artykułów
- Usuwanie artykułów (soft delete)
- Zmianę statusu artykułów (publikacja, archiwizacja)

**Uwaga:** Na tym etapie pomijamy uwierzytelnianie i autoryzację - zostaną one dodane później.

---

## Wymagania Funkcjonalne

### 1. Lista Artykułów (`/admin/blogs`)
- **Wyświetlanie:** Tabela ze wszystkimi artykułami (DRAFT, PUBLISHED, ARCHIVED)
- **Kolumny:** Tytuł, Status, Data utworzenia, Data aktualizacji, Akcje
- **Filtrowanie:** Dropdown lub tabs do filtrowania po statusie (Wszystkie/Draft/Published/Archived)
- **Paginacja:** Maksymalnie 20 artykułów na stronę
- **Przycisk:** "Dodaj Nowy Artykuł" nad tabelą
- **Akcje na liście:**
  - **Edytuj:** Przejście do `/admin/blogs/{id}/edit`
  - **Usuń:** Potwierdzenie w modalu + soft delete przez API
  - **Publikuj:** Dostępne dla DRAFT - zmienia status na PUBLISHED
  - **Archiwizuj:** Dostępne dla PUBLISHED - zmienia status na ARCHIVED
  - **Cofnij publikację:** Dostępne dla PUBLISHED - zmienia status na DRAFT

### 2. Formularz Dodawania Artykułu (`/admin/blogs/new`)
- **Pola:**
  - `title` (wymagane, input text, max 255 znaków)
  - `content` (wymagane, rich textarea z formatowaniem, 50-25000 znaków)
  - `excerpt` (opcjonalne, textarea, max 500 znaków, auto-resize)
- **Akcje:**
  - **Zapisz jako Draft:** POST do `/api/articles` ze statusem DRAFT
  - **Zapisz i Opublikuj:** POST do `/api/articles` + PATCH do `/api/articles/{id}/publish`
  - **Anuluj:** Powrót do `/admin/blogs` bez zapisywania

### 3. Formularz Edycji Artykułu (`/admin/blogs/{id}/edit`)
- **Ładowanie:** GET `/api/articles/{id}` przy załadowaniu strony
- **Pola:** Identyczne jak w formularzu dodawania (title, content, excerpt)
- **Informacje dodatkowe:** Wyświetlanie statusu, daty utworzenia, autora (read-only)
- **Akcje:**
  - **Zapisz zmiany:** PUT do `/api/articles/{id}`
  - **Publikuj:** PUT + PATCH `/api/articles/{id}/publish` (jeśli DRAFT)
  - **Archiwizuj:** PUT + PATCH `/api/articles/{id}/archive` (jeśli PUBLISHED)
  - **Anuluj:** Powrót do `/admin/blogs` bez zapisywania

### 4. Walidacja po Stronie Klienta
- **title:** Nie może być pusty, max 255 znaków
- **content:** Nie może być pusty, min 50 znaków, max 25000 znaków
- **excerpt:** Max 500 znaków
- Walidacja w czasie rzeczywistym z wyświetlaniem błędów pod polami
- Blokada przycisku submit jeśli formularz nieprawidłowy

---

## Backend - Wymagane Zmiany

### ✅ API Endpoints - GOTOWE

**Dobre wieści:** Backend ma już wszystkie potrzebne endpointy REST API!

- ✅ `GET /api/articles` - Lista z filtrowaniem po statusie (`?status=DRAFT`)
- ✅ `GET /api/articles/{id}` - Pobieranie pojedynczego artykułu
- ✅ `POST /api/articles` - Tworzenie nowego artykułu (zwraca DRAFT)
- ✅ `PUT /api/articles/{id}` - Aktualizacja artykułu
- ✅ `DELETE /api/articles/{id}` - Soft delete artykułu
- ✅ `PATCH /api/articles/{id}/publish` - Publikacja artykułu
- ✅ `PATCH /api/articles/{id}/archive` - Archiwizacja artykułu

**Nie trzeba modyfikować API!** Wszystkie operacje są już obsługiwane przez `ArticleController`.

---

### 📝 Zadanie Backend 1: Utworzenie AdminBlogController (MVC)

**Cel:** Serwowanie widoków Thymeleaf dla panelu admina.

**Plik do utworzenia:**
```
src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/AdminBlogController.java
```

**Wymagania:**
- Kontroler Spring MVC z adnotacją `@Controller`
- Base mapping: `@RequestMapping("/admin/blogs")`
- Trzy endpointy GET:
  1. `/admin/blogs` - zwraca `pages/admin/blogs/index` z atrybutem `pageTitle: "Zarządzanie Blogami"`
  2. `/admin/blogs/new` - zwraca `pages/admin/blogs/form` z atrybutami:
     - `pageTitle: "Nowy Artykuł"`
     - `mode: "create"`
  3. `/admin/blogs/{id}/edit` - zwraca `pages/admin/blogs/form` z atrybutami:
     - `pageTitle: "Edytuj Artykuł"`
     - `mode: "edit"`
     - `articleId: {id}`

**Uwaga:** Controller tylko serwuje widoki. Wszystkie operacje na danych obsługuje JavaScript przez REST API (`ArticleController`).

---

### 📝 Zadanie Backend 2: Dodanie endpoint do cofania publikacji (opcjonalne)

**Uwaga:** Backend nie ma obecnie endpointu do cofania publikacji (PUBLISHED → DRAFT).

**Opcja A: Dodaj nowy endpoint (REKOMENDOWANE)**

1. **Utworzenie Use Case:**
   - Plik: `src/main/java/pl/klastbit/lexpage/application/article/UnpublishArticleUseCase.java`
   - Command record: `UnpublishArticleCommand(Long articleId)`
   - Logika: Pobierz artykuł z repozytorium, wywołaj `article.unpublish()`, zapisz
   - Obsługa błędów: `IllegalArgumentException` gdy artykuł nie istnieje

2. **Dodanie endpointu w ArticleController:**
   - Endpoint: `PATCH /api/articles/{id}/unpublish`
   - Zwraca: `ArticleResponse` (200 OK)
   - Logika: Utworzenie use case, wykonanie, mapowanie do response

**Opcja B: Pomiń cofanie publikacji**
- Jeśli pominiemy, usuń przycisk "Cofnij publikację" z UI frontendu

---

## Frontend - Wymagane Zmiany

### 📝 Zadanie Frontend 1: Utworzenie Layout dla Panelu Admina

**Cel:** Dedykowany layout dla stron administracyjnych z nawigacją boczną.

**Plik do utworzenia:**
```
src/main/resources/templates/layouts/admin.html
```

**Struktura layoutu:**

1. **Topbar (navbar fixed top):**
   - Logo: "Lexpage Admin" (link do /admin)
   - Prawy róg: Link "Powrót do strony" (href="/")
   - Tło: `bg-white border-b border-gray-200`
   - Wysokość: standardowa (py-3)

2. **Sidebar (fixed left):**
   - Szerokość: `w-64`
   - Menu items:
     - Dashboard (icon: dashboard, href="/admin")
     - Artykuły (icon: article, href="/admin/blogs")
       - Aktywny stan: `bg-primary-100 text-primary-600` gdy URI startsWith `/admin/blogs`
   - Ikony: Material Icons
   - Hover effect: `hover:bg-gray-100`

3. **Main Content Area:**
   - Margin left: `ml-64` (szerokość sidebaru)
   - Padding: `p-4 md:p-8` (responsywny)
   - Placeholder: `<div th:replace="${content}"></div>`

4. **Dziedziczenie:**
   - `<head>` z `layouts/base :: head(pageTitle=${pageTitle})`
   - Scripts z `layouts/base :: scripts`

**Użycie:** Strony używają `th:replace="~{layouts/admin :: layout(~{::content})}"` jak w `layouts/main.html`

---

### 📝 Zadanie Frontend 2: Strona Listy Artykułów

**Cel:** Wyświetlanie wszystkich artykułów w tabeli z filtrowaniem i akcjami.

**Plik do utworzenia:**
```
src/main/resources/templates/pages/admin/blogs/index.html
```

**Struktura HTML:**

1. **Header sekcja:**
   - H1: "Zarządzanie Artykułami"
   - Opis: "Przeglądaj, edytuj i zarządzaj artykułami blogowymi"
   - Przycisk: "Dodaj Nowy Artykuł" (href="/admin/blogs/new", icon "add")

2. **Filtry (białe card z shadow):**
   - Dropdown `statusFilter`: Wszystkie, DRAFT, PUBLISHED, ARCHIVED
   - Input `searchInput`: Wyszukiwanie po tytule (placeholder: "Szukaj po tytule...")

3. **Stany UI (każdy hidden domyślnie):**
   - `loadingState`: Spinner + tekst "Ładowanie artykułów..."
   - `errorState`: Red alert z `errorMessage`
   - `emptyState`: Ikona article + "Brak artykułów" + link do dodawania
   - `articlesTable`: Główna tabela

4. **Tabela artykułów:**
   - Kolumny: Tytuł (+ slug), Status, Data utworzenia, Data aktualizacji, Akcje
   - Tbody ID: `articlesTableBody` (wypełniane przez JS)

5. **Paginacja:**
   - Container ID: `paginationContainer`
   - Buttony: Poprzednia, numery stron, Następna

6. **Modal usuwania:**
   - ID: `deleteModal`
   - Ikona warning + tytuł + message
   - Przyciski: Anuluj, Usuń (red)

**JavaScript - Kluczowe funkcje:**

- **Stan:** `currentPage`, `currentStatus`, `currentKeyword`, `articleToDelete`
- **loadArticles():**
  - Fetch `GET /api/articles?page={page}&size=20&sort=createdAt,desc&status={status}&keyword={keyword}`
  - Obsługa response: `data.content` i `data.page`
- **renderArticles(articles):**
  - Template każdego row z: title, slug, status badge, daty, akcje
  - Akcje zależne od statusu:
    - DRAFT: Edytuj, Publikuj, Usuń
    - PUBLISHED: Edytuj, Archiwizuj, Cofnij publikację, Usuń
    - ARCHIVED: Edytuj, Cofnij do Draft, Usuń
- **renderStatusBadge(status):** Kolorowe badge (gray, green, yellow)
- **renderPagination(pageInfo):** Generowanie przycisków paginacji
- **publishArticle(id):** `PATCH /api/articles/{id}/publish`
- **archiveArticle(id):** `PATCH /api/articles/{id}/archive`
- **unpublishArticle(id):** `PATCH /api/articles/{id}/unpublish`
- **confirmDelete():** `DELETE /api/articles/{id}`
- **Debounce dla searchInput:** 500ms delay
- **Utilities:** `formatDate()` (pl-PL locale), `escapeHtml()`, state management

---

### 📝 Zadanie Frontend 3: Formularz Dodawania/Edycji Artykułu

**Cel:** Uniwersalny formularz do tworzenia i edycji artykułów.

**Plik do utworzenia:**
```
src/main/resources/templates/pages/admin/blogs/form.html
```

**Struktura HTML (PODSUMOWANIE - nie trzeba pisać całego kodu tutaj):**

<!-- Zamiast całego HTML, oto krótka specyfikacja:

1. **Header:**
   - Link "Powrót do listy" (href="/admin/blogs")
   - H1: "Nowy Artykuł" lub "Edytuj Artykuł" (zależnie od `mode`)

2. **Loading State (dla edit mode):**
   - ID: `loadingState`
   - Hidden w create mode: `th:classappend="${mode == 'create' ? 'hidden' : ''}"`
   - Spinner + "Ładowanie artykułu..."

3. **Error State:**
   - ID: `errorState`, hidden domyślnie
   - Red alert z `errorMessage`

4. **Formularz (ID: `articleForm`):**

   a) **Article Info (tylko edit mode, hidden domyślnie):**
      - ID: `articleInfo`
      - Grid 3 kolumny: Status, Data utworzenia, Autor

   b) **Pole Title:**
      - Input text, maxlength="255", required
      - Error message: `titleError`
      - Character counter: `titleCount`/255

   c) **Pole Content (Rich Editor):**
      - Toolbar z przyciskami formatowania:
        - Bold, Italic, Underline
        - Lista punktowana, Lista numerowana
        - Nagłówek, Link
      - ContentEditable div: ID `contentEditor`, min-height 400px
      - Error message: `contentError`
      - Character counter: min 50, max 25000

   d) **Pole Excerpt:**
      - Textarea, rows="3", maxlength="500"
      - Error message: `excerptError`
      - Character counter: `excerptCount`/500

   e) **Przyciski akcji:**
      - `saveDraftBtn`: "Zapisz jako Draft" (gray button)
      - `savePublishBtn`: "Zapisz i Opublikuj" / "Zapisz zmiany" (primary button)
      - Edit mode dodatkowo:
        - `publishBtn`: "Opublikuj" (green, hidden domyślnie, pokazuj dla DRAFT)
        - `archiveBtn`: "Archiwizuj" (yellow, hidden domyślnie, pokazuj dla PUBLISHED)
      - Link Anuluj: href="/admin/blogs"
      - Loading spinners w buttonach (`.button-spinner` hidden domyślnie)

5. **Success Alert (fixed top-right):**
   - ID: `successAlert`, hidden domyślnie
   - Green alert z `successMessage`

---

**JavaScript - Kluczowe funkcje (IMPLEMENTUJ NA PODSTAWIE OPISU):**

```javascript
// Thymeleaf variables
const mode = /*[[${mode}]]*/ 'create';
const articleId = /*[[${articleId}]]*/ null;

// Funkcje do implementacji:

// 1. loadArticle() - tylko dla edit mode
//    - Fetch GET /api/articles/{articleId}
//    - Wypełnij pola formularza: titleInput.value, contentEditor.innerHTML, excerptInput.value
//    - Zaktualizuj liczniki znaków
//    - Pokaż articleInfo z danymi: status, createdAt, authorName
//    - Pokaż odpowiednie przyciski akcji (publishBtn dla DRAFT, archiveBtn dla PUBLISHED)

// 2. saveArticle(shouldPublish)
//    - Walidacja formularza: validateForm()
//    - Przygotuj dane: {title, content (innerHTML), excerpt}
//    - Mode create: POST /api/articles
//    - Mode edit: PUT /api/articles/{articleId}
//    - Jeśli shouldPublish === true && status === DRAFT: PATCH /api/articles/{id}/publish
//    - Success: showSuccess(), redirect do /admin/blogs po 1.5s

// 3. publishArticle() - tylko dla edit mode
//    - Confirm dialog
//    - PATCH /api/articles/{articleId}/publish
//    - Success: showSuccess(), redirect

// 4. archiveArticle() - tylko dla edit mode
//    - Confirm dialog
//    - PATCH /api/articles/{articleId}/archive
//    - Success: showSuccess(), redirect

// 5. Walidacja:
//    - validateForm(): sprawdza wszystkie pola, zwraca boolean
//    - validateField(fieldName): walidacja pojedynczego pola
//      - title: required, max 255
//      - content: required, min 50, max 25000
//      - excerpt: max 500
//    - showFieldError(fieldName, message): pokazuje błąd pod polem
//    - hideFieldError(fieldName): ukrywa błąd

// 6. Rich text editor:
//    - formatText(command): document.execCommand(command) - bold, italic, underline, lists
//    - insertHeading(): document.execCommand('formatBlock', false, '<h2>')
//    - insertLink(): prompt dla URL + document.execCommand('createLink')

// 7. Event listeners:
//    - titleInput, contentEditor, excerptInput: aktualizacja liczników znaków + walidacja
//    - saveDraftBtn: saveArticle(false)
//    - savePublishBtn: saveArticle(true)
//    - publishBtn, archiveBtn: odpowiednie funkcje

// 8. Utilities:
//    - setButtonLoading(button, loading): toggle spinner w buttonie
//    - showError(message): pokazuje errorState
//    - showSuccess(message): pokazuje successAlert na 3s
//    - formatDate(dateString): formatowanie daty pl-PL locale
```

**Kluczowe API calle:**
- `GET /api/articles/{id}` - ładowanie artykułu (edit mode)
- `POST /api/articles` - tworzenie nowego artykułu (create mode)
- `PUT /api/articles/{id}` - aktualizacja artykułu (edit mode)
- `PATCH /api/articles/{id}/publish` - publikacja artykułu
- `PATCH /api/articles/{id}/archive` - archiwizacja artykułu

---

## Checklist Implementacji

### Backend

- [ ] **Zadanie 1:** Utworzenie `AdminBlogController.java` z endpointami MVC
  - [ ] GET `/admin/blogs` - Lista artykułów
  - [ ] GET `/admin/blogs/new` - Formularz nowego artykułu
  - [ ] GET `/admin/blogs/{id}/edit` - Formularz edycji artykułu
  - [ ] Testy jednostkowe kontrolera

- [ ] **Zadanie 2 (Opcjonalne):** Dodanie endpointu `/api/articles/{id}/unpublish`
  - [ ] Utworzenie `UnpublishArticleUseCase.java`
  - [ ] Dodanie endpointu w `ArticleController.java`
  - [ ] Testy jednostkowe use case i kontrolera
  - [ ] **UWAGA:** Jeśli zostanie pominięte, usuń przycisk "Cofnij publikację" z UI

### Frontend

- [ ] **Zadanie 1:** Utworzenie layoutu admina (`layouts/admin.html`)
  - [ ] Navbar z logo i linkami
  - [ ] Sidebar z menu nawigacyjnym
  - [ ] Główny obszar contentu
  - [ ] Responsywność (mobile, tablet, desktop)

- [ ] **Zadanie 2:** Strona listy artykułów (`pages/admin/blogs/index.html`)
  - [ ] Tabela z artykułami (tytuł, status, daty, akcje)
  - [ ] Filtrowanie po statusie (dropdown)
  - [ ] Wyszukiwanie po tytule (input z debounce)
  - [ ] Paginacja (20 artykułów na stronę)
  - [ ] Przycisk "Dodaj Nowy Artykuł"
  - [ ] Akcje: Edytuj, Usuń, Publikuj/Archiwizuj/Cofnij publikację
  - [ ] Modal potwierdzenia usunięcia
  - [ ] Stany: Loading, Empty, Error, Success
  - [ ] Integracja z API (`GET /api/articles`, `DELETE`, `PATCH publish/archive/unpublish`)

- [ ] **Zadanie 3:** Formularz dodawania/edycji (`pages/admin/blogs/form.html`)
  - [ ] Pola: title, content (rich editor), excerpt
  - [ ] Walidacja po stronie klienta (required, min/max length)
  - [ ] Liczniki znaków dla wszystkich pól
  - [ ] Rich text editor (toolbar z formatowaniem)
  - [ ] Przyciski: Zapisz jako Draft, Zapisz i Opublikuj, Anuluj
  - [ ] W trybie edycji: wyświetlanie info o artykule (status, data, autor)
  - [ ] W trybie edycji: dodatkowe akcje (Publikuj, Archiwizuj) w zależności od statusu
  - [ ] Loading states dla przycisków
  - [ ] Integracja z API (`POST /api/articles`, `PUT /api/articles/{id}`, `PATCH publish/archive`)
  - [ ] Przekierowanie do `/admin/blogs` po sukcesie

### Testowanie

- [ ] **Test E2E:** Pełny flow tworzenia artykułu
  1. Wejście na `/admin/blogs`
  2. Kliknięcie "Dodaj Nowy Artykuł"
  3. Wypełnienie formularza
  4. Zapisanie jako Draft
  5. Weryfikacja pojawienia się artykułu na liście ze statusem DRAFT
  6. Edycja artykułu
  7. Publikacja artykułu
  8. Weryfikacja zmiany statusu na PUBLISHED
  9. Archiwizacja artykułu
  10. Usunięcie artykułu
  11. Weryfikacja braku artykułu na liście

- [ ] **Test Walidacji:** Sprawdzenie walidacji formularza
  - Pusty tytuł (błąd)
  - Pusta treść (błąd)
  - Treść poniżej 50 znaków (błąd)
  - Treść powyżej 25000 znaków (błąd)
  - Excerpt powyżej 500 znaków (błąd)

- [ ] **Test Filtrowania:** Sprawdzenie filtrowania na liście
  - Filtrowanie po statusie (All, Draft, Published, Archived)
  - Wyszukiwanie po tytule

- [ ] **Test Responsywności:** Sprawdzenie UI na różnych urządzeniach
  - Mobile (< 768px)
  - Tablet (768px - 1024px)
  - Desktop (> 1024px)

---

## Uwagi Implementacyjne

### Bezpieczeństwo (TODO)
**UWAGA:** Na tym etapie pomijamy uwierzytelnianie i autoryzację. W przyszłości należy:
1. Dodać Spring Security z uwierzytelnianiem
2. Zabezpieczyć endpointy `/admin/*` przed nieautoryzowanym dostępem
3. Implementować role (ROLE_ADMIN)
4. Dodać CSRF protection dla formularzy
5. Logować akcje administracyjne

### Pola SEO (TODO)
Zgodnie z decyzją użytkownika, na tym etapie pomijamy pola SEO (metaTitle, metaDescription, ogImageUrl, canonicalUrl, keywords). Można je dodać w przyszłości jako:
- Osobną sekcję "SEO & Metadata" w formularzu (collapsible)
- Osobną zakładkę w formularzu (tabs: "Podstawowe" | "SEO")
- Osobny modal "Ustawienia SEO"

### Podgląd na żywo (TODO)
Na tym etapie pomijamy funkcję podglądu na żywo. Można ją dodać w przyszłości jako:
- Przycisk "Podgląd" otwierający artykuł w nowej karcie (wymaga tymczasowego zapisania jako DRAFT)
- Split view z podglądem obok formularza (wymaga dodatkowejlogiki renderowania)

### Upload obrazów (TODO)
Rich text editor na tym etapie nie obsługuje uploadu obrazów. W przyszłości można dodać:
- Integrację z zewnętrznym serwisem (np. Cloudinary, AWS S3)
- Lokalny upload z endpointem `/api/upload/image`
- Drag & drop dla obrazów w edytorze

### Ulepszenia UX (TODO)
Możliwe przyszłe ulepszenia:
- Auto-save (zapisywanie wersji roboczej co X sekund)
- Historia wersji artykułów
- Bulk actions (masowe usuwanie, zmiana statusu)
- Sortowanie tabeli po kolumnach
- Export artykułów do CSV/PDF
- Duplicate article (kopiowanie artykułu)
- Preview mode (podgląd przed publikacją)

---

## Podsumowanie

Ten plan obejmuje kompletną implementację panelu administratora do zarządzania blogami, włączając:

**Backend:**
- MVC Controller dla widoków admina (`AdminBlogController`)
- Opcjonalnie: endpoint do cofania publikacji (`UnpublishArticleUseCase`)
- Wykorzystanie istniejących endpointów REST API (bez zmian)

**Frontend:**
- Layout dla panelu admina z nawigacją boczną
- Strona listy artykułów z filtrowaniem, wyszukiwaniem i paginacją
- Uniwersalny formularz do tworzenia i edycji artykułów
- Walidacja po stronie klienta
- Rich text editor z podstawowym formatowaniem
- Integracja z API przez fetch (async/await)
- Responsywny design (mobile-first)

**Charakterystyka:**
- Server-Side Rendering (SSR) z Thymeleaf
- JavaScript vanilla (bez frameworków)
- Material Tailwind dla stylingów
- Zgodność z Hexagonal Architecture i DDD principles
- Zgodność z regułami `.ai/rules/frontend.md` i `.ai/rules/backend.md`

Plan jest gotowy do implementacji przez innego agenta AI bez potrzeby dodatkowych wyjaśnień.
