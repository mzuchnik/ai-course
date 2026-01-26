# Blog Management - User Stories & Use Cases

## 1. Przegląd

Blog Management API to backend system do zarządzania artykułami blogowymi dla strony kancelarii prawnej. System umożliwia tworzenie, edycję, publikację i archiwizację artykułów prawniczych, wspierając workflow: Draft → Published → Archived.

**Cel biznesowy:**
- Regularnie publikować treści prawnicze (2 artykuły/miesiąc)
- Budować autorytet prawnika poprzez content marketing
- Generować zapytania kontaktowe od czytelników
- Wspierać SEO i pozycjonowanie strony

**Zakres funkcjonalny:**
- CRUD artykułów blogowych z metadanymi SEO
- Workflow publikacji (DRAFT → PUBLISHED → ARCHIVED)
- Paginacja, filtrowanie i sortowanie listy artykułów
- Soft delete (możliwość odzyskania)
- Wsparcie dla full-text search (przyszłość)
- Audit trail (kto i kiedy utworzył/edytował)

---

## 2. Aktorzy (Actors)

### 2.1. Administrator (Prawnik)
**Rola:** Właściciel kancelarii, główny autor i moderator treści

**Uprawnienia:**
- Tworzenie nowych artykułów
- Edycja wszystkich artykułów (również opublikowanych)
- Publikacja artykułów z Draft do Published
- Archiwizacja przestarzałych artykułów
- Usuwanie artykułów (soft delete)
- Przeglądanie wszystkich artykułów (włącznie z Draft)

**Typowe scenariusze:**
- Tworzy szkic artykułu ręcznie lub z pomocą AI
- Edytuje i poprawia treść przed publikacją
- Publikuje gotowe artykuły
- Aktualizuje artykuły po zmianach w prawie
- Archiwizuje nieaktualne artykuły

### 2.2. System AI (Generator Treści)
**Rola:** Automatyczny generator szkiców artykułów (przyszły moduł)

**Uprawnienia:**
- Tworzenie artykułów w statusie DRAFT
- Generowanie meta description z treści

**Typowe scenariusze:**
- Generuje szkic artykułu na podstawie tematu i keywords
- Wypełnia pola SEO (title, meta description)

### 2.3. Czytelnik (Public User)
**Rola:** Odwiedzający stronę, potencjalny klient

**Uprawnienia:**
- Przeglądanie listy opublikowanych artykułów
- Czytanie pełnej treści opublikowanych artykułów
- Wyszukiwanie artykułów po słowach kluczowych

**Typowe scenariusze:**
- Wchodzi na stronę z Google po wyszukaniu frazy prawniczej
- Przegląda listę artykułów na blogu
- Czyta artykuł i decyduje o kontakcie z kancelarią

---

## 3. User Stories

### US-BLOG-001: Administrator przegląda listę wszystkich artykułów

**Jako** administrator kancelarii
**Chcę** przeglądać paginowaną listę wszystkich artykułów z możliwością filtrowania i sortowania
**Aby** szybko znaleźć artykuł do edycji lub sprawdzić stan publikacji

**Kryteria akceptacji:**
- ✅ Lista wyświetla artykuły z podstawowymi informacjami (tytuł, status, autor, daty)
- ✅ Domyślnie 10 artykułów na stronie, maksymalnie 100
- ✅ Możliwość filtrowania po statusie (DRAFT, PUBLISHED, ARCHIVED)
- ✅ Możliwość filtrowania po autorze (authorId)
- ✅ Możliwość wyszukiwania full-text po słowach kluczowych (title, content)
- ✅ Sortowanie po: createdAt, updatedAt, publishedAt, title (ASC/DESC)
- ✅ Domyślne sortowanie: createdAt DESC (najnowsze pierwsze)
- ✅ Paginacja z informacją o liczbie stron i elementów
- ✅ Nie pokazuje soft-deleted artykułów

**Priorytet:** WYSOKI (MVP)
**Effort:** 3 Story Points
**Zależności:** Brak

---

### US-BLOG-002: Administrator przegląda szczegóły pojedynczego artykułu

**Jako** administrator kancelarii
**Chcę** zobaczyć pełne dane pojedynczego artykułu
**Aby** sprawdzić treść, metadane SEO i historię zmian przed edycją

**Kryteria akceptacji:**
- ✅ Wyświetla pełną treść artykułu (HTML content)
- ✅ Pokazuje wszystkie metadane SEO (metaTitle, metaDescription, keywords, OG image)
- ✅ Wyświetla informacje o autorze (ID i nazwa)
- ✅ Pokazuje audit trail: kto utworzył, kto ostatnio edytował, daty
- ✅ Pokazuje status (DRAFT/PUBLISHED/ARCHIVED) i datę publikacji
- ✅ Zwraca 404 Not Found dla nieistniejącego lub soft-deleted artykułu

**Priorytet:** WYSOKI (MVP)
**Effort:** 2 Story Points
**Zależności:** US-BLOG-001

---

### US-BLOG-003: Administrator tworzy nowy artykuł (draft)

**Jako** administrator kancelarii
**Chcę** utworzyć nowy artykuł w statusie DRAFT
**Aby** zapisać szkic do późniejszej edycji i publikacji

**Kryteria akceptacji:**
- ✅ Artykuł tworzony w statusie DRAFT (nie jest publicznie widoczny)
- ✅ Wymagane pola: title (1-255 znaków), content (50-25000 znaków)
- ✅ Opcjonalne pola: excerpt, metaTitle, metaDescription, ogImageUrl, canonicalUrl, keywords
- ✅ Slug generowany automatycznie z title (transliteracja PL→ASCII, lowercase, `-` zamiast spacji)
- ✅ Unikalność slug: jeśli istnieje, dodawany suffix `-1`, `-2`, etc.
- ✅ Auto-generowanie metaDescription jeśli nie podano (pierwsze 160 znaków content bez HTML tags)
- ✅ authorId, createdBy, updatedBy ustawiane automatycznie z kontekstu zalogowanego użytkownika
- ✅ Walidacja: błędy zwracane w formacie 400 Bad Request z ProblemDetail
- ✅ Zwraca 201 Created z pełnymi danymi utworzonego artykułu

**Flow:**
1. Administrator wypełnia formularz z tytułem i treścią artykułu
2. Opcjonalnie dodaje metadane SEO (meta title, description, keywords)
3. Klika "Zapisz jako szkic"
4. System waliduje dane (długość pól, wymagane pola)
5. System generuje unikalny slug z tytułu
6. System generuje meta description jeśli nie podano
7. System zapisuje artykuł w statusie DRAFT
8. System zwraca szczegóły utworzonego artykułu

**Warunki błędów:**
- 400 Bad Request: puste title/content, przekroczone limity długości
- 401 Unauthorized: brak autentykacji
- 500 Internal Server Error: błąd bazy danych

**Priorytet:** WYSOKI (MVP)
**Effort:** 5 Story Points
**Zależności:** Brak

---

### US-BLOG-004: Administrator edytuje istniejący artykuł

**Jako** administrator kancelarii
**Chcę** edytować treść i metadane artykułu w dowolnym statusie
**Aby** poprawić błędy, zaktualizować informacje lub uzupełnić SEO

**Kryteria akceptacji:**
- ✅ Możliwość edycji artykułu w statusie DRAFT, PUBLISHED lub ARCHIVED
- ✅ Status NIE zmienia się podczas edycji (pozostaje bez zmian)
- ✅ Edycja wszystkich pól: title, content, excerpt, metadane SEO
- ✅ Slug regenerowany jeśli zmienił się title (z zachowaniem unikalności)
- ✅ updatedBy i updatedAt aktualizowane automatycznie
- ✅ Walidacja taka sama jak przy tworzeniu
- ✅ Zwraca 200 OK z zaktualizowanymi danymi
- ✅ Zwraca 404 Not Found dla nieistniejącego lub soft-deleted artykułu

**Flow:**
1. Administrator otwiera szczegóły artykułu
2. Klika "Edytuj"
3. Modyfikuje treść, tytuł lub metadane
4. Klika "Zapisz"
5. System waliduje dane
6. System regeneruje slug jeśli zmienił się tytuł
7. System aktualizuje artykuł z zachowaniem statusu
8. System ustawia updatedBy i updatedAt
9. System zwraca zaktualizowane dane artykułu

**Warunki błędów:**
- 400 Bad Request: błędy walidacji
- 404 Not Found: artykuł nie istnieje lub jest soft-deleted
- 401 Unauthorized: brak autentykacji

**Priorytet:** WYSOKI (MVP)
**Effort:** 5 Story Points
**Zależności:** US-BLOG-003

---

### US-BLOG-005: Administrator publikuje artykuł

**Jako** administrator kancelarii
**Chcę** opublikować artykuł ze statusu DRAFT
**Aby** był widoczny publicznie dla czytelników

**Kryteria akceptacji:**
- ✅ Publikacja możliwa tylko dla artykułów w statusie DRAFT
- ✅ Status zmienia się na PUBLISHED
- ✅ publishedAt ustawiane automatycznie na NOW()
- ✅ updatedBy i updatedAt aktualizowane
- ✅ Walidacja wymagań publikacji: title, content, slug nie mogą być puste
- ✅ Zwraca 200 OK z danymi opublikowanego artykułu
- ✅ Zwraca 400 Bad Request jeśli artykuł nie jest w statusie DRAFT
- ✅ Zwraca 404 Not Found dla nieistniejącego artykułu

**Flow:**
1. Administrator przegląda listę artykułów w statusie DRAFT
2. Otwiera szczegóły artykułu
3. Sprawdza treść i metadane
4. Klika "Opublikuj"
5. System waliduje wymagania publikacji (title, content, slug)
6. System zmienia status na PUBLISHED
7. System ustawia publishedAt na NOW()
8. System zwraca zaktualizowane dane artykułu
9. Administrator widzi potwierdzenie publikacji

**Warunki błędów:**
- 400 Bad Request: artykuł nie jest w statusie DRAFT (np. już opublikowany)
- 400 Bad Request: brak wymaganej treści (title, content, slug puste)
- 404 Not Found: artykuł nie istnieje

**Priorytet:** WYSOKI (MVP)
**Effort:** 3 Story Points
**Zależności:** US-BLOG-003, US-BLOG-004

---

### US-BLOG-006: Administrator archiwizuje nieaktualny artykuł

**Jako** administrator kancelarii
**Chcę** zarchiwizować nieaktualny lub przestarzały artykuł
**Aby** ukryć go przed czytelnikami bez usuwania z bazy danych

**Kryteria akceptacji:**
- ✅ Archiwizacja możliwa tylko dla artykułów w statusie PUBLISHED
- ✅ Status zmienia się na ARCHIVED
- ✅ publishedAt pozostaje bez zmian (historia publikacji zachowana)
- ✅ updatedBy i updatedAt aktualizowane
- ✅ Zwraca 200 OK z danymi zarchiwizowanego artykułu
- ✅ Zwraca 400 Bad Request jeśli artykuł nie jest w statusie PUBLISHED
- ✅ Zwraca 404 Not Found dla nieistniejącego artykułu

**Flow:**
1. Administrator przegląda listę opublikowanych artykułów
2. Identyfikuje przestarzały artykuł (np. po zmianie przepisów)
3. Otwiera szczegóły artykułu
4. Klika "Archiwizuj"
5. System zmienia status na ARCHIVED
6. System aktualizuje updatedBy i updatedAt
7. System zwraca zaktualizowane dane artykułu
8. Administrator widzi potwierdzenie archiwizacji

**Warunki błędów:**
- 400 Bad Request: artykuł nie jest w statusie PUBLISHED
- 404 Not Found: artykuł nie istnieje

**Uwagi:**
- Zarchiwizowane artykuły są nadal dostępne w panelu admin
- Można je ponownie opublikować poprzez edycję i publikację
- Domyślnie nie pokazują się na liście publicznej

**Priorytet:** ŚREDNI (MVP)
**Effort:** 3 Story Points
**Zależności:** US-BLOG-005

---

### US-BLOG-007: Administrator usuwa artykuł (soft delete)

**Jako** administrator kancelarii
**Chcę** usunąć artykuł (soft delete)
**Aby** ukryć niepożądaną treść z możliwością przywrócenia w przyszłości

**Kryteria akceptacji:**
- ✅ Soft delete: ustawienie deletedAt = NOW()
- ✅ Artykuł przestaje być widoczny w listach (GET /api/articles)
- ✅ Artykuł nie jest dostępny przez GET /api/articles/{id}
- ✅ Możliwość usunięcia artykułu w dowolnym statusie (DRAFT, PUBLISHED, ARCHIVED)
- ✅ Zwraca 204 No Content po sukcesie (brak body)
- ✅ Zwraca 404 Not Found dla nieistniejącego lub już usuniętego artykułu

**Flow:**
1. Administrator przegląda listę artykułów
2. Identyfikuje artykuł do usunięcia (błąd merytoryczny, spam, etc.)
3. Klika "Usuń"
4. System wyświetla dialog potwierdzenia: "Czy na pewno usunąć artykuł?"
5. Administrator potwierdza
6. System ustawia deletedAt na NOW()
7. System zwraca 204 No Content
8. Artykuł znika z listy

**Warunki błędów:**
- 404 Not Found: artykuł nie istnieje lub jest już soft-deleted

**Uwagi:**
- Soft delete umożliwia przywrócenie artykułu w przyszłości (wymaga osobnego endpointu)
- W MVP nie ma endpointu do przywracania (restore)
- Hard delete możliwy tylko bezpośrednio w bazie danych

**Priorytet:** ŚREDNI (MVP)
**Effort:** 2 Story Points
**Zależności:** US-BLOG-003

---

### US-BLOG-008: Czytelnik przegląda opublikowane artykuły na blogu

**Jako** czytelnik strony kancelarii
**Chcę** przeglądać listę opublikowanych artykułów
**Aby** znaleźć interesującą mnie tematykę prawną

**Kryteria akceptacji:**
- ✅ Lista zawiera tylko artykuły w statusie PUBLISHED
- ✅ Artykuły DRAFT i ARCHIVED są niewidoczne dla czytelników
- ✅ Domyślne sortowanie: publishedAt DESC (najnowsze pierwsze)
- ✅ Paginacja: 10 artykułów na stronie
- ✅ Każdy item zawiera: tytuł, excerpt, datę publikacji, autora
- ✅ Możliwość wyszukiwania po słowach kluczowych (keyword)
- ✅ Responsywna lista na urządzeniach mobilnych

**Flow:**
1. Czytelnik wchodzi na stronę `/blog`
2. System pobiera listę artykułów: status=PUBLISHED, sort=publishedAt,desc, page=0, size=10
3. System wyświetla listę z tytułami, excerptami i datami
4. Czytelnik klika "Czytaj więcej" na artykule
5. System przekierowuje na `/blog/{slug}`

**Uwagi:**
- Endpoint publiczny (nie wymaga autentykacji)
- W przyszłości: filtrowanie po kategoriach (prawo cywilne/karne)

**Priorytet:** WYSOKI (MVP)
**Effort:** 3 Story Points
**Zależności:** US-BLOG-005

---

### US-BLOG-009: Czytelnik czyta pełną treść artykułu

**Jako** czytelnik strony kancelarii
**Chcę** przeczytać pełną treść opublikowanego artykułu
**Aby** zdobyć wiedzę prawną i ocenić kompetencje prawnika

**Kryteria akceptacji:**
- ✅ Dostęp tylko do artykułów w statusie PUBLISHED
- ✅ Wyświetla pełną treść HTML (formatowanie: h2, h3, p, ul, li, strong, em, a)
- ✅ Pokazuje metadane: tytuł, autora, datę publikacji
- ✅ Meta tags SEO (title, description, OG image) dla lepszego pozycjonowania
- ✅ Schema.org JSON-LD dla Google Rich Snippets
- ✅ Social sharing buttons (Facebook, LinkedIn, Twitter/X)
- ✅ CTA na końcu artykułu: "Masz podobną sprawę? Skontaktuj się"
- ✅ Zwraca 404 Not Found dla artykułów DRAFT, ARCHIVED lub nieistniejących

**Flow:**
1. Czytelnik wchodzi na stronę artykułu przez link lub wyszukiwarkę
2. System pobiera artykuł po slug: GET /api/articles/{id}
3. System sprawdza status=PUBLISHED
4. System renderuje treść HTML z formatowaniem
5. System wyświetla CTA do kontaktu
6. Czytelnik czyta artykuł i decyduje o kontakcie

**Uwagi:**
- Endpoint publiczny (nie wymaga autentykacji)
- W przyszłości: related articles, komentarze, licznik wyświetleń

**Priorytet:** WYSOKI (MVP)
**Effort:** 5 Story Points
**Zależności:** US-BLOG-008

---

### US-BLOG-010: System generuje unikalny slug dla artykułu

**Jako** system
**Chcę** automatycznie generować unikalny slug z tytułu artykułu
**Aby** zapewnić czytelne i SEO-friendly URL-e bez kolizji

**Kryteria akceptacji:**
- ✅ Slug generowany z title podczas tworzenia artykułu
- ✅ Transliteracja polskich znaków: ą→a, ć→c, ę→e, ł→l, ń→n, ó→o, ś→s, ź/ż→z
- ✅ Konwersja do lowercase
- ✅ Usunięcie znaków specjalnych (tylko a-z, 0-9, `-`)
- ✅ Zamiana spacji na `-`
- ✅ Usunięcie wielokrotnych `-` i trimming
- ✅ Sprawdzenie unikalności: jeśli istnieje, dodanie suffixa `-1`, `-2`, etc.
- ✅ Regeneracja slug przy edycji title (z zachowaniem unikalności)

**Przykłady:**
- "Jak napisać pozew o zapłatę?" → `jak-napisac-pozew-o-zaplate`
- "Odszkodowanie za wypadek" → `odszkodowanie-za-wypadek`
- "Odszkodowanie za wypadek" (drugi raz) → `odszkodowanie-za-wypadek-1`

**Priorytet:** WYSOKI (MVP)
**Effort:** 3 Story Points
**Zależności:** US-BLOG-003

---

### US-BLOG-011: System automatycznie generuje meta description

**Jako** system
**Chcę** automatycznie generować meta description z treści artykułu
**Aby** zapewnić podstawowe SEO nawet jeśli administrator nie wypełnił pola

**Kryteria akceptacji:**
- ✅ Jeśli metaDescription jest puste podczas tworzenia/edycji
- ✅ System ekstrakuje pierwsze 160 znaków z content
- ✅ Usuwa wszystkie HTML tags (`<[^>]+>`)
- ✅ Trimuje białe znaki
- ✅ Jeśli tekst dłuższy niż 160 znaków, obcina i dodaje "..."
- ✅ Jeśli metaDescription podano ręcznie, system go nie nadpisuje

**Przykład:**
```
Content: "<h2>Wprowadzenie</h2><p>Pozew o zapłatę to pismo procesowe..."
Generated meta description: "Pozew o zapłatę to pismo procesowe..."
```

**Priorytet:** ŚREDNI (MVP)
**Effort:** 2 Story Points
**Zależności:** US-BLOG-003

---

### US-BLOG-012: System zapisuje audit trail dla artykułów

**Jako** system
**Chcę** zapisywać historię zmian artykułów (kto, kiedy utworzył/edytował)
**Aby** zapewnić transparentność i możliwość audytu

**Kryteria akceptacji:**
- ✅ Przy tworzeniu: createdBy, createdAt ustawiane automatycznie z kontekstu użytkownika
- ✅ Przy tworzeniu: updatedBy, updatedAt ustawiane na te same wartości co createdBy/createdAt
- ✅ Przy edycji: updatedBy, updatedAt aktualizowane automatycznie
- ✅ Przy publikacji/archiwizacji: updatedBy, updatedAt aktualizowane
- ✅ authorId ustawiany na ID użytkownika podczas tworzenia (nie zmienia się)
- ✅ Wszystkie pola audit są immutable (nie można ich nadpisać przez API)

**Priorytet:** WYSOKI (MVP)
**Effort:** 2 Story Points
**Zależności:** US-BLOG-003

---

## 4. Use Cases (Szczegółowe przypadki użycia)

### UC-001: Pobieranie listy artykułów z filtrowaniem

**Aktor:** Administrator, System Frontend

**Warunki wstępne:**
- Brak (endpoint publiczny lub wymagający autentykacji w zależności od konfiguracji)

**Flow główny:**
1. Aktor wysyła żądanie: `GET /api/articles?page=0&size=10&status=PUBLISHED&sort=publishedAt,desc`
2. System waliduje parametry:
   - `page` >= 0
   - `size` <= 100
   - `status` w {DRAFT, PUBLISHED, ARCHIVED} lub null
   - `sort` w {createdAt, updatedAt, publishedAt, title}
3. System buduje query z warunkami:
   - `deletedAt IS NULL`
   - Jeśli `status` podany: `status = :status`
   - Jeśli `authorId` podany: `authorId = :authorId`
   - Jeśli `keyword` podany: full-text search w `title` i `content`
4. System wykonuje query z paginacją i sortowaniem
5. System mapuje wyniki do `ArticleListItemDto`
6. System zwraca 200 OK z JSON:
```json
{
  "content": [{ "id": 1, "title": "...", "status": "PUBLISHED", ... }],
  "page": { "number": 0, "size": 10, "totalElements": 25, "totalPages": 3 }
}
```

**Flow alternatywny - nieprawidłowe parametry:**
1. System wykrywa `size > 100`
2. System zwraca 400 Bad Request z ProblemDetail:
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Page size cannot exceed 100"
}
```

**Warunki końcowe:**
- Lista artykułów zwrócona z paginacją

---

### UC-002: Pobieranie szczegółów pojedynczego artykułu

**Aktor:** Administrator, Czytelnik

**Warunki wstępne:**
- Artykuł o podanym ID istnieje w bazie

**Flow główny:**
1. Aktor wysyła żądanie: `GET /api/articles/1`
2. System wyszukuje artykuł po ID z warunkiem `deletedAt IS NULL`
3. System sprawdza czy artykuł istnieje
4. System ładuje powiązane dane (author, createdBy, updatedBy)
5. System mapuje do `ArticleDetailDto` z pełnymi danymi
6. System zwraca 200 OK z JSON:
```json
{
  "id": 1,
  "title": "Jak napisać pozew o zapłatę?",
  "content": "<h2>Wprowadzenie</h2><p>...",
  "status": "PUBLISHED",
  "authorName": "Jan Kowalski",
  ...
}
```

**Flow alternatywny - artykuł nie istnieje:**
1. System nie znajduje artykułu (nie istnieje lub `deletedAt IS NOT NULL`)
2. System rzuca `ArticleNotFoundException`
3. Global Exception Handler przechwytuje wyjątek
4. System zwraca 404 Not Found z ProblemDetail:
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Article not found with ID: 1"
}
```

**Warunki końcowe:**
- Szczegóły artykułu zwrócone lub błąd 404

---

### UC-003: Tworzenie nowego artykułu

**Aktor:** Administrator

**Warunki wstępne:**
- Administrator jest zalogowany (Spring Security context)
- Administrator ma uprawnienia do tworzenia artykułów

**Flow główny:**
1. Administrator wysyła żądanie: `POST /api/articles`
```json
{
  "title": "Jak napisać pozew o zapłatę?",
  "content": "<h2>Wprowadzenie</h2><p>Pozew o zapłatę to...",
  "excerpt": "Krótki opis...",
  "metaTitle": "Jak napisać pozew o zapłatę? - Poradnik",
  "keywords": ["pozew", "zapłata", "prawo cywilne"]
}
```
2. System waliduje request (Jakarta Validation):
   - `title` NOT BLANK, max 255 znaków
   - `content` NOT BLANK, 50-25000 znaków
   - `excerpt` max 500 znaków
   - `metaTitle` max 60 znaków
   - `keywords` max 10 elementów
3. System pobiera userId z Spring Security context
4. System generuje slug z `title`: `generateSlug("Jak napisać pozew o zapłatę?")` → `jak-napisac-pozew-o-zaplate`
5. System sprawdza unikalność slug: `existsBySlugAndDeletedAtIsNull("jak-napisac-pozew-o-zaplate")`
6. Jeśli slug istnieje: `makeSlugUnique()` dodaje suffix `-1`, `-2`...
7. Jeśli `metaDescription` puste: system generuje z content (pierwsze 160 znaków bez HTML)
8. System tworzy domain entity: `Article.createDraft(...)`
9. System zapisuje do bazy: `articleRepository.save(article)`
10. System mapuje do `ArticleDetailDto`
11. System zwraca 201 Created z Location header i JSON

**Flow alternatywny - błędy walidacji:**
1. System wykrywa błędy walidacji (np. `title` jest puste)
2. Spring Boot rzuca `MethodArgumentNotValidException`
3. Global Exception Handler przechwytuje wyjątek
4. System zwraca 400 Bad Request z ProblemDetail:
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "errors": {
    "title": "Tytuł jest wymagany",
    "content": "Treść musi mieć od 50 do 25000 znaków"
  }
}
```

**Warunki końcowe:**
- Artykuł utworzony w statusie DRAFT
- Zwrócone ID i pełne dane artykułu

---

### UC-004: Edycja istniejącego artykułu

**Aktor:** Administrator

**Warunki wstępne:**
- Artykuł o podanym ID istnieje
- Administrator jest zalogowany

**Flow główny:**
1. Administrator wysyła żądanie: `PUT /api/articles/1`
```json
{
  "title": "Jak napisać pozew o zapłatę? [ZAKTUALIZOWANE]",
  "content": "<h2>Wprowadzenie</h2><p>Zaktualizowana treść...",
  "excerpt": "Zaktualizowany opis..."
}
```
2. System waliduje request (identycznie jak w UC-003)
3. System pobiera userId z Spring Security context
4. System wyszukuje artykuł: `findByIdAndDeletedAtIsNull(1)`
5. System sprawdza czy artykuł istnieje (jeśli nie: 404)
6. System porównuje nowy `title` z obecnym
7. Jeśli `title` się zmienił:
   - System generuje nowy slug
   - System sprawdza unikalność i dodaje suffix jeśli potrzeba
8. System aktualizuje domain entity: `article.updateContent(...)`
9. System zapisuje: `articleRepository.save(article)`
10. System mapuje do `ArticleDetailDto`
11. System zwraca 200 OK z JSON

**Flow alternatywny - artykuł nie istnieje:**
1. System nie znajduje artykułu
2. System rzuca `ArticleNotFoundException`
3. System zwraca 404 Not Found

**Warunki końcowe:**
- Artykuł zaktualizowany
- Status pozostaje bez zmian
- updatedBy i updatedAt zaktualizowane

---

### UC-005: Publikacja artykułu

**Aktor:** Administrator

**Warunki wstępne:**
- Artykuł o podanym ID istnieje
- Artykuł jest w statusie DRAFT

**Flow główny:**
1. Administrator wysyła żądanie: `PATCH /api/articles/1/publish`
2. System wyszukuje artykuł: `findByIdAndDeletedAtIsNull(1)`
3. System sprawdza czy artykuł istnieje (jeśli nie: 404)
4. System wywołuje: `article.publish()` (domain method)
5. Domain entity waliduje:
   - Status == DRAFT (jeśli nie: rzuca `IllegalStateException`)
   - Title, content, slug nie są puste
6. Domain entity:
   - Ustawia `status = PUBLISHED`
   - Ustawia `publishedAt = NOW()`
   - Ustawia `updatedAt = NOW()`
7. System zapisuje: `articleRepository.save(article)`
8. System mapuje do `ArticleDetailDto`
9. System zwraca 200 OK z JSON

**Flow alternatywny - nieprawidłowy status:**
1. Domain entity wykrywa `status != DRAFT`
2. Domain entity rzuca `IllegalStateException("Article is already published")`
3. Global Exception Handler przechwytuje
4. System zwraca 400 Bad Request:
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Article is already published"
}
```

**Warunki końcowe:**
- Artykuł opublikowany (status = PUBLISHED)
- publishedAt ustawiony
- Artykuł widoczny publicznie

---

### UC-006: Archiwizacja artykułu

**Aktor:** Administrator

**Warunki wstępne:**
- Artykuł o podanym ID istnieje
- Artykuł jest w statusie PUBLISHED

**Flow główny:**
1. Administrator wysyła żądanie: `PATCH /api/articles/1/archive`
2. System wyszukuje artykuł: `findByIdAndDeletedAtIsNull(1)`
3. System sprawdza czy artykuł istnieje (jeśli nie: 404)
4. System wywołuje: `article.archive()` (domain method)
5. Domain entity waliduje:
   - Status == PUBLISHED (jeśli nie: rzuca `InvalidArticleStatusTransitionException`)
6. Domain entity:
   - Ustawia `status = ARCHIVED`
   - Ustawia `updatedAt = NOW()`
   - publishedAt pozostaje bez zmian
7. System zapisuje: `articleRepository.save(article)`
8. System mapuje do `ArticleDetailDto`
9. System zwraca 200 OK z JSON

**Flow alternatywny - nieprawidłowy status:**
1. Domain entity wykrywa `status != PUBLISHED`
2. Domain entity rzuca `InvalidArticleStatusTransitionException`
3. System zwraca 400 Bad Request

**Warunki końcowe:**
- Artykuł zarchiwizowany (status = ARCHIVED)
- Artykuł ukryty przed czytelnikami
- Historia publikacji zachowana (publishedAt nie zmieniony)

---

### UC-007: Soft delete artykułu

**Aktor:** Administrator

**Warunki wstępne:**
- Artykuł o podanym ID istnieje
- Artykuł nie jest już soft-deleted

**Flow główny:**
1. Administrator wysyła żądanie: `DELETE /api/articles/1`
2. System wyszukuje artykuł: `findByIdAndDeletedAtIsNull(1)`
3. System sprawdza czy artykuł istnieje (jeśli nie: 404)
4. System wywołuje: `article.softDelete()` (domain method)
5. Domain entity:
   - Ustawia `deletedAt = NOW()`
   - Ustawia `updatedAt = NOW()`
6. System zapisuje: `articleRepository.save(article)`
7. System zwraca 204 No Content (brak body)

**Flow alternatywny - artykuł nie istnieje:**
1. System nie znajduje artykułu (nie istnieje lub już soft-deleted)
2. System rzuca `ArticleNotFoundException`
3. System zwraca 404 Not Found

**Warunki końcowe:**
- Artykuł soft-deleted (deletedAt ustawiony)
- Artykuł niewidoczny w listach
- Możliwość przywrócenia w przyszłości

---

## 5. Diagramy przepływu (Flow Diagrams)

### 5.1. Workflow publikacji artykułu

```
┌──────────────┐
│  New Draft   │
│ (CREATE API) │
└──────┬───────┘
       │
       v
┌──────────────┐      PATCH /publish      ┌────────────────┐
│    DRAFT     │─────────────────────────>│   PUBLISHED    │
│              │                           │                │
│ - Editable   │<────────┐                │ - Public       │
│ - Not public │         │                │ - Editable     │
└──────────────┘         │                └────────┬───────┘
                         │                         │
                         │                         │
                    (Unpublish)            PATCH /archive
                    Not in MVP                     │
                         │                         v
                         │                ┌────────────────┐
                         └────────────────│   ARCHIVED     │
                                          │                │
                                          │ - Not public   │
                                          │ - Editable     │
                                          └────────────────┘

Notes:
- Wszystkie statusy: Editable (PUT /api/articles/{id})
- Wszystkie statusy: Soft deletable (DELETE /api/articles/{id})
- Soft delete ukrywa artykuł niezależnie od statusu
```

### 5.2. Proces tworzenia artykułu przez administratora

```
[Administrator] → [Form: Title + Content] → [Click "Save as Draft"]
                                                      │
                                                      v
                                          ┌─────────────────────┐
                                          │ Validation          │
                                          │ - Title: 1-255 char │
                                          │ - Content: 50-25K   │
                                          └──────────┬──────────┘
                                                     │
                                    ┌────────────────┴────────────────┐
                                    │                                 │
                                   FAIL                              OK
                                    │                                 │
                                    v                                 v
                          ┌─────────────────┐           ┌──────────────────────┐
                          │ 400 Bad Request │           │ Generate Slug        │
                          │ Show Errors     │           │ Check Uniqueness     │
                          └─────────────────┘           │ Generate Meta Desc   │
                                                        └──────────┬───────────┘
                                                                   │
                                                                   v
                                                        ┌──────────────────────┐
                                                        │ Save to DB           │
                                                        │ status = DRAFT       │
                                                        │ authorId = currentId │
                                                        └──────────┬───────────┘
                                                                   │
                                                                   v
                                                        ┌──────────────────────┐
                                                        │ 201 Created          │
                                                        │ Return Article JSON  │
                                                        └──────────────────────┘
```

### 5.3. Proces czytania artykułu przez czytelnika

```
[Google Search] → [Click Result] → [Blog Article Page]
                                           │
                                           v
                                ┌──────────────────────┐
                                │ GET /api/articles/1  │
                                └──────────┬───────────┘
                                           │
                        ┌──────────────────┴──────────────────┐
                        │                                     │
                   Article Found                        Article Not Found
                   status=PUBLISHED                     OR status=DRAFT/ARCHIVED
                        │                                     │
                        v                                     v
             ┌──────────────────────┐              ┌─────────────────┐
             │ 200 OK               │              │ 404 Not Found   │
             │ Render HTML          │              │ "Article not    │
             │ - Title              │              │  found"         │
             │ - Content            │              └─────────────────┘
             │ - Author             │
             │ - Published Date     │
             │ - CTA Button         │
             └──────────┬───────────┘
                        │
                        v
               [Reader reads article]
                        │
                        v
              [Clicks CTA: "Contact"]
                        │
                        v
            [Fills Contact Form → US1 from PRD]
```

---

## 6. Scenariusze testowe (Test Scenarios)

### 6.1. Happy Path - Pełny cykl życia artykułu

**Scenario:** Administrator tworzy, publikuje i archiwizuje artykuł

**Steps:**
1. **Create Draft**
   - POST /api/articles
   - Body: { "title": "Test Article", "content": "<p>Content...</p>" }
   - Expected: 201 Created, status=DRAFT, slug=test-article

2. **View Draft**
   - GET /api/articles/1
   - Expected: 200 OK, status=DRAFT

3. **Edit Draft**
   - PUT /api/articles/1
   - Body: { "title": "Test Article Updated", "content": "..." }
   - Expected: 200 OK, slug=test-article-updated

4. **Publish**
   - PATCH /api/articles/1/publish
   - Expected: 200 OK, status=PUBLISHED, publishedAt set

5. **View Published (as public user)**
   - GET /api/articles?status=PUBLISHED
   - Expected: 200 OK, content contains article

6. **Archive**
   - PATCH /api/articles/1/archive
   - Expected: 200 OK, status=ARCHIVED

7. **Verify not public**
   - GET /api/articles?status=PUBLISHED
   - Expected: 200 OK, content does NOT contain article

8. **Soft Delete**
   - DELETE /api/articles/1
   - Expected: 204 No Content

9. **Verify deleted**
   - GET /api/articles/1
   - Expected: 404 Not Found

**Expected Result:** Artykuł przeszedł pełny lifecycle bez błędów

---

### 6.2. Edge Case - Slug Uniqueness

**Scenario:** Tworzenie dwóch artykułów z tym samym tytułem

**Steps:**
1. **Create First Article**
   - POST /api/articles
   - Body: { "title": "Odszkodowanie", "content": "..." }
   - Expected: 201 Created, slug=odszkodowanie

2. **Create Second Article with Same Title**
   - POST /api/articles
   - Body: { "title": "Odszkodowanie", "content": "..." }
   - Expected: 201 Created, slug=odszkodowanie-1

3. **Create Third Article**
   - POST /api/articles
   - Body: { "title": "Odszkodowanie", "content": "..." }
   - Expected: 201 Created, slug=odszkodowanie-2

**Expected Result:** Każdy artykuł ma unikalny slug

---

### 6.3. Error Case - Invalid Status Transition

**Scenario:** Próba publikacji już opublikowanego artykułu

**Steps:**
1. **Create and Publish**
   - POST /api/articles → 201 Created, status=DRAFT
   - PATCH /api/articles/1/publish → 200 OK, status=PUBLISHED

2. **Try to Publish Again**
   - PATCH /api/articles/1/publish
   - Expected: 400 Bad Request
   - Body: { "detail": "Article is already published" }

**Expected Result:** System zapobiega nieprawidłowej zmianie statusu

---

### 6.4. Error Case - Validation Failures

**Scenario:** Próba utworzenia artykułu z nieprawidłowymi danymi

**Steps:**
1. **Empty Title**
   - POST /api/articles
   - Body: { "title": "", "content": "..." }
   - Expected: 400 Bad Request, errors: { "title": "Tytuł jest wymagany" }

2. **Content Too Short**
   - POST /api/articles
   - Body: { "title": "Test", "content": "Short" }
   - Expected: 400 Bad Request, errors: { "content": "Treść musi mieć od 50 do 25000 znaków" }

3. **Too Many Keywords**
   - POST /api/articles
   - Body: { "title": "Test", "content": "...", "keywords": [11 keywords] }
   - Expected: 400 Bad Request, errors: { "keywords": "Maksymalnie 10 keywords" }

**Expected Result:** Walidacja działa poprawnie

---

### 6.5. Performance Test - Large List Pagination

**Scenario:** Pobranie dużej listy artykułów z paginacją

**Steps:**
1. **Create 250 Articles** (via script)
2. **Fetch Page 1**
   - GET /api/articles?page=0&size=100
   - Expected: 200 OK, content.length=100, totalElements=250, totalPages=3
3. **Fetch Page 2**
   - GET /api/articles?page=1&size=100
   - Expected: 200 OK, content.length=100
4. **Fetch Page 3**
   - GET /api/articles?page=2&size=100
   - Expected: 200 OK, content.length=50

**Expected Result:** Paginacja działa poprawnie dla dużych zbiorów

---

## 7. Metryki sukcesu (Success Metrics)

### 7.1. Funkcjonalne

| Metryka | Cel MVP | Pomiar |
|---------|---------|--------|
| Czas tworzenia artykułu | < 30 min (z AI) | Audit trail: createdAt → publishedAt |
| Publikacja artykułów | 2 artykuły/miesiąc | Count artykułów PUBLISHED per month |
| Uptime API | > 99% | UptimeRobot monitoring |
| Response Time (GET list) | < 500ms | Spring Boot Actuator metrics |
| Response Time (GET single) | < 300ms | Spring Boot Actuator metrics |

### 7.2. Techniczne

| Metryka | Cel MVP | Pomiar |
|---------|---------|--------|
| Code Coverage | > 80% | Gradle test report |
| API Success Rate | > 99% | Logs analysis (ERROR level count) |
| Database Query Performance | < 100ms avg | PostgreSQL slow query log |
| Slug Generation Success | 100% | Unit tests |
| Meta Description Auto-gen | 100% | Unit tests |

### 7.3. Biznesowe (z PRD)

| Metryka | Cel MVP | Pomiar |
|---------|---------|--------|
| Pierwsze zapytanie kontaktowe | W 1. miesiącu | Contact form submissions |
| Blog Traffic | > 100 czytelników/miesiąc | Google Analytics 4 |
| Avg Time on Article Page | > 2 min | Google Analytics 4 |
| Bounce Rate | < 60% | Google Analytics 4 |

---

## 8. Przyszłe rozszerzenia (Out of Scope for MVP)

### 8.1. Funkcje zaplanowane na Phase 2

1. **Restore soft-deleted articles**
   - Endpoint: POST /api/articles/{id}/restore
   - Ustawia deletedAt = NULL

2. **Article versioning**
   - Historia zmian artykułu
   - Możliwość przywrócenia poprzedniej wersji
   - Tabela: article_versions

3. **Categories/Tags**
   - Tabela: categories, article_categories
   - Filtrowanie po kategorii: ?category=prawo-cywilne

4. **Full-text search z PostgreSQL**
   - Użycie kolumny search_vector (tsvector)
   - Endpoint: GET /api/articles/search?q=pozew

5. **Related articles**
   - Endpoint: GET /api/articles/{id}/related
   - Algorytm podobieństwa na podstawie keywords

6. **Article analytics**
   - Licznik wyświetleń (views_count)
   - Średni czas czytania (avg_read_time)
   - Engagement metrics

7. **Comments system**
   - Tabela: article_comments
   - Moderacja komentarzy przez admin

8. **Scheduled publishing**
   - Pole: scheduled_publish_at
   - Cron job: automatyczna publikacja o określonej godzinie

9. **AI Content Generator Integration**
   - Endpoint: POST /api/articles/generate
   - Body: { "topic": "...", "keywords": [...], "length": 2000 }
   - Integracja z Anthropic Claude API

10. **Image upload & management**
    - Endpoint: POST /api/articles/{id}/images
    - Upload do S3/Minio
    - Auto-kompresja do WebP

### 8.2. Integracje planowane

- **Email notifications:** Powiadomienie admina o nowym komentarzu
- **Social media auto-posting:** Automatyczne posty na Facebook/LinkedIn po publikacji
- **Newsletter integration:** Wysyłka nowych artykułów do subskrybentów
- **RSS feed:** Endpoint /api/articles/rss.xml

---

## 9. Założenia i ograniczenia (Assumptions & Constraints)

### 9.1. Założenia

1. **Single admin user:** W MVP tylko jeden administrator (prawnik)
2. **No concurrent editing:** Brak obsługi konfliktów przy równoczesnej edycji
3. **No RBAC:** Wszystkie endpointy admin wymagają autentykacji, ale brak ról
4. **Polish language only:** Slug generation i transliteracja tylko dla polskiego
5. **HTML content trusted:** Brak sanityzacji HTML w MVP (whitelist na froncie)
6. **No content moderation:** Administrator odpowiada za jakość treści

### 9.2. Ograniczenia techniczne

1. **Max article size:** 25000 znaków (~5000 słów)
2. **Max keywords:** 10 per artykuł
3. **Pagination limit:** Max 100 artykułów na stronę
4. **Slug length:** Max 255 znaków
5. **No real-time updates:** Brak WebSockets, tylko REST API

### 9.3. Ograniczenia MVP

1. **No image upload:** Obrazy przez zewnętrzne URL-e (ogImageUrl)
2. **No draft autosave:** Brak automatycznego zapisywania co X sekund
3. **No preview mode:** Brak podglądu przed publikacją (trzeba opublikować i sprawdzić)
4. **No A/B testing:** Brak testowania różnych wersji tytułu/meta description

---

## 10. Glosariusz

| Termin | Definicja |
|--------|-----------|
| **Article** | Artykuł blogowy zawierający treść prawniczą (post) |
| **Slug** | SEO-friendly URL identifier generowany z tytułu (np. `jak-napisac-pozew`) |
| **Draft** | Status artykułu - szkic niewidoczny publicznie |
| **Published** | Status artykułu - opublikowany i widoczny publicznie |
| **Archived** | Status artykułu - zarchiwizowany, niewidoczny publicznie |
| **Soft Delete** | Logiczne usunięcie (ustawienie deletedAt) bez fizycznego usunięcia z bazy |
| **Audit Trail** | Historia zmian: kto i kiedy utworzył/edytował artykuł |
| **Meta Description** | Krótki opis artykułu wyświetlany w wynikach wyszukiwania (max 160 znaków) |
| **OG Image** | Open Graph image - miniatura artykułu wyświetlana przy udostępnieniu w social media |
| **Canonical URL** | Preferowany URL artykułu dla wyszukiwarek (zapobiega duplicate content) |
| **Excerpt** | Krótki fragment artykułu wyświetlany na liście (teaser) |
| **Pagination** | Podział listy artykułów na strony (page, size, totalElements, totalPages) |
| **Full-text Search** | Wyszukiwanie po treści artykułu (title + content) |
| **Transliteracja** | Konwersja polskich znaków na ASCII (ą→a, ł→l, etc.) |

---

## 11. Referencje

- **PRD:** `.ai/prd.md` - Product Requirements Document dla MVP strony kancelarii
- **Implementation Plan:** `.ai/plans/blog-management-plan.md` - Szczegółowy plan techniczny
- **Backend Rules:** `.ai/rules/backend.md` - Coding standards i best practices
- **Hexagonal Architecture:** Domain-Driven Design + Ports & Adapters pattern
- **RFC 7807:** Problem Details for HTTP APIs (ProblemDetail format)

---

**Dokument wersja:** 1.0
**Data utworzenia:** 2026-01-26
**Autor:** Generated based on PRD and Implementation Plan
**Status:** Ready for Implementation
