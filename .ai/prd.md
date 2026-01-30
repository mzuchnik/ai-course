# PRD - MVP Strony Kancelarii Prawnej

## Kluczowe Decyzje Produktowe

1. **Stack technologiczny**: Spring Boot 4.0.1 + Java 25 + Thymeleaf + Material Tailwind HTML + PostgreSQL 16, hostowana na VPS
2. **Architektura**: Monolityczna aplikacja z architekturą heksagonalną opartą na DDD
3. **AI Content Generation**: Workflow AI → weryfikacja prawnika → publikacja. Spring AI 2.0.0-M2 z OpenRouter (proxy dla Anthropic Claude / OpenAI)
4. **Zakres MVP**: Strona informacyjna + blog + formularz kontaktowy + panel admin z generatorem treści artykułów przez AI
5. **Specjalizacja**: Prawo cywilne (niebieski) i karne (bordowy) - subtelne różnicowanie kolorystyczne
6. **Autentykacja**: Spring Security, form login, jedno konto administratora (profil test: admin@lexpage.pl / admin123)
7. **Security**: Whitelist HTML (p, h2, h3, ul, li, strong, em, a), Rate limiting (3 wiadomości/IP/1h), CSRF protection, reCAPTCHA v3 (TODO)
8. **SEO**: Meta tags, Open Graph, sitemap.xml, robots.txt, Schema.org JSON-LD (TODO)
9. **Backup**: pg_dump cron daily, 7 dni retencji (TODO)
10. **Timeline**: 10-12 tygodni (part-time), CI/CD przez GitHub Actions
11. **Zawartość startowa**: Minimum 2 artykuły blogowe, 8-10 opisów usług
12. **Frontend Build**: Gradle Node plugin + Tailwind CSS 3.4.1 + Material Tailwind HTML components
13. **Testing**: TestContainers (PostgreSQL 16-alpine) + JaCoCo coverage reports (HTML + XML)

## Wymagania Funkcjonalne

### Frontend Publiczny
- **Strona główna**: ✅ Hero section, ✅ prezentacja prawnika (mock data), ✅ najnowsze artykuły, ✅ CTA, ✅ 6 kafelków usług (zahardkodowane)
- **Usługi**: 🚧 Podział Cywilne/Karne (kolory w PageController), ❌ szablon szczegółów: nazwa, opis, zakres, przebieg, FAQ, CTA
- **Blog**: ✅ Lista artykułów (paginacja), ✅ pojedynczy artykuł z formatowaniem (HTML), ❌ social sharing buttons
- **Formularz kontaktowy**: ✅ Imię, ✅ email, ✅ telefon (opt.), ✅ kategoria (CIVIL/CRIMINAL), ✅ wiadomość (min 50 znaków), ✅ rate limiting (3/IP/1h), ❌ reCAPTCHA v3
- **Stopka**: ✅ Dane kontaktowe (mock), ❌ Google Maps embed, ❌ klikalny telefon (`tel:`)

### Panel Administracyjny
- **Autentykacja**: ✅ Spring Security form login (admin@lexpage.pl / admin123 w profilu test)
- **Generator AI**: ✅ Modal AI z promptem (max 1000 znaków) → generowanie (timeout 60s, Spring AI) → ✅ TinyMCE 6 WYSIWYG editor → ✅ draft/publikacja
- **Zarządzanie**: ✅ CRUD artykułów (Create, Update, Delete, Publish, Unpublish, Archive), ✅ Draft mode (zapisz jako draft, podgląd, publikuj), ❌ upload obrazów z kompresją (entity istnieje)
- **Ograniczenia**: ❌ Max 20 generowań AI/dzień (brak limitu), ✅ limit 25000 znaków/artykuł (walidacja w TinyMCE)

### Backend & Infrastructure
- **Stack**: Spring Boot 4.0.1, Java 25, Thymeleaf, PostgreSQL 16+, Spring Data JPA, Liquibase migrations
- **Architektura**: Heksagonalna (Domain → Application → Infrastructure/Adapters) z DDD
- **AI Integration**: Spring AI 2.0.0-M2 + OpenRouter API (model configurable: Claude/GPT), CommonMark dla Markdown→HTML
- **Email**: Spring Mail SMTP, async powiadomienia (TODO - not implemented)
- **Security**: CSRF, XSS sanitization, BCrypt, Spring Security form login, Rate limiting (3 msg/IP/1h), HTTPS-only (TODO)
- **Testing**: TestContainers (PostgreSQL 16-alpine), JaCoCo coverage (HTML + XML reports)
- **Frontend Build**: Gradle Node plugin 7.1.0 (Node 20.11.0, NPM 10.2.4), Tailwind CSS 3.4.1, Material Tailwind HTML 2.3.2
- **Hosting**: VPS + Nginx + Let's Encrypt SSL (TODO)
- **CI/CD**: GitHub Actions (Java 25 Oracle, PR comments, 5-day artifacts retention)
- **Monitoring**: Google Analytics 4, UptimeRobot, Spring Boot Actuator (TODO)

**Build Commands:**
```bash
# Build project (includes frontend build, runs tests, generates coverage)
./gradlew build

# Run application (production profile)
./gradlew bootRun

# Run application (test profile with test user: admin@lexpage.pl / admin123)
./gradlew bootRun --args='--spring.profiles.active=test'

# Run tests + generate JaCoCo coverage
./gradlew test

# Run Tailwind CSS in watch mode (development)
./gradlew tailwindWatch

# Clean build artifacts
./gradlew clean build
```

## Kryteria Sukcesu MVP

### Biznesowe
1. Pierwsze zapytanie kontaktowe w pierwszym miesiącu
2. Lighthouse Performance Score >90
3. Uruchomienie w terminie 10-12 tygodni

### Techniczne
1. Uptime >99%
2. Page Load Time <2s (homepage/artykuł)
3. AI Generation Success Rate >95%

### Contentowe
1. Minimum 2 artykuły na start
2. Czas tworzenia artykułu z AI <30 min
3. Częstotliwość publikacji: 2 artykuły/miesiąc

## User Stories

### US1: Klient wypełnia formularz kontaktowy (Priorytet: WYSOKI)
**Jako** odwiedzający stronę kancelarii
**Chcę** wysłać zapytanie przez formularz kontaktowy
**Aby** szybko skontaktować się z prawnikiem bez konieczności dzwonienia

**Akceptacja:**
- Pola: Imię i nazwisko*, Email*, Telefon, Kategoria sprawy* (Cywilne/Karne), Wiadomość* (min 50 znaków)
- Walidacja po stronie klienta (HTML5 + JS) i serwera
- reCAPTCHA v3 w tle (score >0.5)
- Po wysłaniu: komunikat sukcesu + informacja o czasie odpowiedzi (max 24h)
- Prawnik otrzymuje email z powiadomieniem
- Dane zapisane w bazie
- Brak możliwości wysłania >3 wiadomości z tego samego IP w ciągu 1h

**Flow:**
1. Użytkownik klika "Kontakt" w menu
2. Wypełnia formularz (walidacja na bieżąco)
3. Wybiera kategorię sprawy z dropdown (Cywilne/Karne)
4. Klika "Wyślij zapytanie"
5. System: walidacja serwer-side → sprawdzenie reCAPTCHA → zapis do DB → wysłanie emaila
6. Wyświetlenie: "Dziękujemy! Odpowiemy w ciągu 24h."

**Błędy:**
- Puste pola: czerwone obramowanie + komunikat pod polem
- Email niepoprawny: "Podaj prawidłowy adres email"
- Wiadomość <50 znaków: "Opisz swoją sprawę (minimum 50 znaków)"
- reCAPTCHA fail: "Weryfikacja bezpieczeństwa nie powiodła się. Spróbuj ponownie."
- Błąd serwera: "Wystąpił problem. Spróbuj ponownie lub zadzwoń: [telefon]"

---

### US2: Klient szuka pomocy prawnej przez wyszukiwarkę
**Jako** osoba potrzebująca pomocy prawnika
**Chcę** znaleźć specjalistę w Google i szybko zrozumieć jego specjalizację
**Aby** ocenić, czy może mi pomóc

**Flow:**
1. Google search: "prawnik prawo karne [miasto]"
2. Kliknięcie w wynik (meta description jasno wskazuje specjalizację)
3. Landing: strona główna lub usługi
4. Przegląd: hero section z specjalizacją, lista usług, ostatnie artykuły
5. Decyzja: kliknięcie w konkretną usługę lub bezpośrednio "Kontakt"

**Akceptacja:**
- Meta title/description zoptymalizowane pod kluczowe frazy
- Hero section jednoznacznie komunikuje: Kim jest prawnik, Co oferuje (Cywilne/Karne)
- Czas do podjęcia decyzji o kontakcie <2 min

---

### US3: Klient dzwoni z urządzenia mobilnego
**Jako** użytkownik mobile w pilnej sprawie
**Chcę** szybko zadzwonić do kancelarii
**Aby** natychmiast porozmawiać z prawnikiem

**Flow:**
1. Wejście na stronę z telefonu
2. Scroll lub kliknięcie "Kontakt"
3. Kliknięcie w numer telefonu (klikalny link `tel:`)
4. Automatyczne uruchomienie aplikacji telefonu

**Akceptacja:**
- Numer telefonu widoczny w header (mobile) i stopce (wszystkie urządzenia)
- Link `<a href="tel:+48...">` działa na iOS i Android
- Icon telefonu obok numeru dla czytelności

---

### US4: Prawnik tworzy artykuł blogowy z AI
**Jako** prawnik prowadzący bloga
**Chcę** wygenerować szkic artykułu przez AI i go edytować
**Aby** regularnie publikować treści bez spędzania godzin na pisaniu

**Flow:**
1. Login do panelu admin
2. Sekcja "Blog" → "Nowy artykuł z AI"
3. Formularz: Temat*, Keywords (max 5), Długość (500-5000 słów)
4. Kliknięcie "Generuj" → loading (timeout 60s)
5. AI (Spring AI + OpenRouter) zwraca Markdown → konwersja do HTML (CommonMark)
6. Edycja: TinyMCE WYSIWYG editor z pełnym toolbar, formatowanie, dodanie obrazów, korekta prawnicza
7. Zapis jako Draft lub Publikacja

**Akceptacja:**
- ✅ Czas generowania <60s dla artykułu 2000 słów
- ✅ AI success rate >95% (Spring AI retry logic)
- ✅ Rich text editor: TinyMCE 6 (CDN) z pluginami: lists, link, image, charmap, preview, code, fullscreen, table, wordcount
- ✅ WYSIWYG editor: pełny toolbar (bold, italic, underline, lists, align, colors, links, images, code view)
- ✅ Podgląd artykułu przed publikacją: artykuły można zapisać jako DRAFT i przejrzeć w admin panelu przed publikacją
- ❌ SEO: auto-generowanie meta description - TODO

---

### US5: Czytelnik znajduje artykuł blogowy
**Jako** czytelnik szukający informacji prawnych
**Chcę** znaleźć wartościowy artykuł i móc się skontaktować
**Aby** rozwiązać swój problem prawny

**Flow:**
1. Google search: "jak napisać pozew [temat]"
2. Kliknięcie w artykuł z bloga kancelarii
3. Czytanie artykułu (formatowanie, struktura, FAQ)
4. Przekonanie: "Ten prawnik zna się na rzeczy"
5. Kliknięcie CTA: "Masz podobną sprawę? Skontaktuj się"
6. Wypełnienie formularza kontaktowego

**Akceptacja:**
- Artykuł ładuje się <2s
- Social sharing buttons (Facebook, LinkedIn, Twitter/X)
- Related articles na końcu (3 podobne tematy)
- CTA co 800-1000 słów + na końcu artykułu
- Breadcrumbs: Home > Blog > [Kategoria] > [Tytuł]

---

### US6: Administrator zarządza treścią
**Jako** administrator strony (prawnik lub asystent)
**Chcę** edytować opisy usług i artykuły
**Aby** utrzymywać treść aktualną

**Flow:**
1. Login do panelu
2. Sekcje: Usługi / Blog / Wiadomości kontaktowe
3. Lista elementów z akcjami: Edytuj / Usuń / Duplikuj
4. Edycja w TinyMCE WYSIWYG → Zapisz
5. Publikacja natychmiastowa lub draft mode

**Akceptacja:**
- ✅ CRUD artykułów: Create, Update, Delete, Publish, Unpublish, Archive
- ✅ TinyMCE 6 WYSIWYG editor z rich toolbar
- ✅ Draft mode: możliwość zapisu jako draft, podglądu i późniejszej publikacji
- ✅ Status badges: DRAFT, PUBLISHED, ARCHIVED
- ❌ Upload obrazów: max 5MB, auto-kompresja do WebP - TODO
- ❌ Wersjonowanie treści (możliwość cofnięcia zmian) - TODO
- ❌ Wiadomości kontaktowe: oznaczanie jako przeczytane, archiwizacja - TODO (entity istnieje, brak UI)

---

### US7: Administrator loguje się i dodaje nowy artykuł
**Jako** administrator strony (prawnik)
**Chcę** zalogować się do panelu admina i dodać nowy artykuł na blog
**Aby** publikować treści prawne dla klientów i zwiększać widoczność kancelarii w wyszukiwarkach

**Flow:**
1. Wejście na stronę logowania `/login`
2. Wprowadzenie email (`admin@lexpage.pl`) i hasła (`admin123` w profilu test)
3. Kliknięcie "Zaloguj się" → Spring Security authentication
4. Przekierowanie do dashboardu admina `/admin/dashboard`
5. Kliknięcie w menu bocznym: "Blog" → przekierowanie do `/admin/blogs`
6. Lista artykułów z przyciskiem "Nowy Artykuł" → przekierowanie do `/admin/blogs/new`
7. Formularz nowego artykułu:
   - **Opcja A - Generowanie z AI**:
     - Kliknięcie "Generuj z AI" (fioletowy przycisk)
     - Modal: wpisanie promptu (np. "Artykuł o odpowiedzialności karnej za jazdę po alkoholu")
     - Kliknięcie "Generuj" → loading (do 30s)
     - Preview wygenerowanej treści (tytuł + pierwsze 200 znaków)
     - Kliknięcie "Użyj tej treści" → treść wstawiona do formularza
   - **Opcja B - Manualne tworzenie**:
     - Bezpośrednie wypełnienie pól
8. Wypełnienie/edycja formularza:
   - Tytuł* (max 255 znaków)
   - Treść* w TinyMCE WYSIWYG (min 50, max 25000 znaków)
   - Zajawka (opcjonalna, max 500 znaków)
9. Wybór akcji:
   - **"Zapisz jako Draft"** → artykuł zapisany ze statusem DRAFT
   - **"Zapisz i Opublikuj"** → artykuł zapisany i opublikowany (status PUBLISHED)
10. Success message + przekierowanie do `/admin/blogs` (lista artykułów)

**Akceptacja:**
- ✅ Strona logowania: `/login` z formularzem (email + password)
- ✅ Spring Security authentication z BCrypt password encoder
- ✅ Przekierowanie po zalogowaniu: `/admin/dashboard`
- ✅ Dashboard z nawigacją do sekcji Blog
- ✅ Lista artykułów z przyciskiem "Nowy Artykuł"
- ✅ Formularz artykułu z walidacją:
  - Tytuł: required, max 255 znaków (live counter)
  - Treść: required, min 50, max 25000 znaków (live counter w TinyMCE)
  - Zajawka: optional, max 500 znaków (live counter)
- ✅ TinyMCE 6 WYSIWYG editor z toolbar:
  - Formatting: bold, italic, underline, strikethrough
  - Colors: forecolor, backcolor
  - Alignment: left, center, right, justify
  - Lists: bullet, numbered, indent, outdent
  - Insert: link, image, media, table
  - Tools: undo, redo, removeformat, code view, fullscreen, help
- ✅ Modal AI Generation:
  - Prompt input (max 1000 znaków)
  - Loading state z animacją i komunikatem
  - Preview: tytuł + pierwsze 200 znaków treści
  - Przyciski: "Generuj", "Użyj tej treści", "Anuluj"
- ✅ Przyciski akcji:
  - "Zapisz jako Draft" (szary) → status: DRAFT
  - "Zapisz i Opublikuj" (niebieski primary) → status: PUBLISHED
  - "Anuluj" → powrót do `/admin/blogs`
- ✅ Success alert: zielony toast z komunikatem "Artykuł został zapisany pomyślnie"
- ✅ Error handling: czerwone komunikaty pod polami formularza
- ✅ Przekierowanie po zapisie: `/admin/blogs` (lista artykułów)
- ✅ CSRF protection (Spring Security)

**Błędy:**
- Błąd logowania (invalid credentials):
  - Czerwony alert: "Nieprawidłowy email lub hasło"
  - UserNotFoundException / InvalidCredentialsException → 401 Unauthorized
- Sesja wygasła:
  - Przekierowanie do `/login` z parametrem `?sessionExpired=true`
  - Komunikat: "Sesja wygasła. Zaloguj się ponownie."
- Walidacja formularza artykułu:
  - Tytuł pusty: "Tytuł jest wymagany"
  - Tytuł > 255 znaków: "Tytuł nie może przekraczać 255 znaków"
  - Treść pusta: "Treść jest wymagana"
  - Treść < 50 znaków: "Treść musi mieć minimum 50 znaków"
  - Treść > 25000 znaków: "Treść nie może przekraczać 25000 znaków"
  - Zajawka > 500 znaków: "Zajawka nie może przekraczać 500 znaków"
- AI Generation error:
  - Timeout (>60s): "Przekroczono limit czasu generowania. Spróbuj ponownie."
  - AI API error: "Nie udało się wygenerować artykułu. Spróbuj ponownie lub napisz artykuł ręcznie."
  - Empty prompt: "Prompt jest wymagany"
  - Prompt > 1000 znaków: "Prompt nie może przekraczać 1000 znaków"

**Notatki techniczne:**
- Test user (profil `test`): `admin@lexpage.pl` / `admin123` (BCrypt hashed)
- Authentication: Spring Security `DomainUserDetailsService` + `BCryptPasswordEncoderAdapter`
- Session management: Spring Session (default)
- CSRF token: automatycznie dodawany przez Thymeleaf (`th:action`)
- TinyMCE: CDN (jsdelivr), wersja 6, licencja open source
- AI endpoint: `POST /api/articles/ai/generate` (Spring AI + OpenRouter)
- Article CRUD endpoints:
  - `POST /api/articles` → CreateArticleUseCase (status: DRAFT)
  - `PATCH /api/articles/{id}/publish` → PublishArticleUseCase (DRAFT → PUBLISHED)

## Kwestie Otwarte (Wymagają Decyzji)

### Przed Rozpoczęciem (Tydzień 0-1)
1. ✅ **AI API Provider**: ~~Anthropic Claude vs OpenAI~~ → **Spring AI + OpenRouter** (configurable model)
2. ❓ **Nazwa domeny**: Wybór i rejestracja - TODO
3. ❓ **Branding**: Logo, kolory brandowe (hex codes), ui.md - TODO

### Przed Deploymentem (Tydzień 7-8)
4. ❓ **VPS Provider**: DigitalOcean/Linode/OVH, specyfikacja zasobów - TODO
5. ❓ **Email SMTP**: Gmail/SendGrid/własny, konfiguracja SPF/DKIM - TODO (Spring Mail nie skonfigurowane)
6. ❓ **Google Services**: Analytics 4, Maps API key, reCAPTCHA keys - TODO
7. ❓ **Treść**: 2 artykuły początkowe, 8-10 opisów usług, bio prawnika - TODO (brak seed data)

### Przed Produkcją (KRYTYCZNE - Tydzień 9-10)
8. ❗ **Dokumenty prawne**: Polityka Prywatności, klauzule RODO (BLOCKER) - TODO
9. ❓ **Backup storage**: Lokalizacja i strategia - TODO (pg_dump cron nie skonfigurowany)
10. ❓ **Beta testing**: Rekrutacja 5-10 testerów - TODO

### Techniczne (Do Rozstrzygnięcia)
11. ✅ **WYSIWYG Editor**: ~~TinyMCE vs Quill vs CKEditor~~ → **TinyMCE 6** (CDN, zaimplementowane)
12. ❓ **Image Storage**: Lokalny filesystem vs S3/Cloudflare R2 vs CDN - TODO
13. ❓ **Email Templates**: Inline CSS vs Thymeleaf templates - TODO

---

## Status Implementacji

### ✅ Zaimplementowane

**Backend (Domain + Application Layer):**
- ✅ Domain model: Article, ContactMessage, Service, User, AIGeneration
- ✅ Value Objects: Email, UserId, ArticleStatus, MessageCategory, MessageStatus, GenerationStatus, ServiceCategory
- ✅ Use Cases (Article): Create, Update, Delete, Publish, Unpublish, Archive, Get, List, GenerateWithAI
- ✅ Use Cases (Contact): SubmitContactForm z rate limiting (3 msg/IP/1h)
- ✅ Use Cases (User): AuthenticateUser
- ✅ Exception handling: Global exception handler z ProblemDetail (RFC 7807)
- ✅ Domain exceptions: ArticleNotFoundException, InvalidArticleStatusTransitionException, RateLimitExceededException, UserNotFoundException, UserDisabledException, InvalidCredentialsException

**Infrastructure Layer:**
- ✅ Persistence: JPA entities, repositories (Article, ContactMessage, User, AIGeneration, Service, LawyerProfile, Image)
- ✅ Mappers: ArticleMapper, ContactMessageMapper, ServiceMapper, UserMapper, AIGenerationMapper
- ✅ Database: PostgreSQL + Liquibase migrations (v1.0)
- ✅ AI Adapter: Spring AI 2.0.0-M2 + OpenRouter integration, CommonMark Markdown→HTML converter
- ✅ Security: Spring Security, BCrypt password encoder, form login, test user initializer
- ✅ Web Controllers: PageController (homepage), BlogViewController, AdminBlogController, ContactFormController, LoginController, ArticleController, ArticleAIController

**Frontend:**
- ✅ Material Tailwind HTML 2.3.2 components library
- ✅ Tailwind CSS 3.4.1 + plugins (@tailwindcss/forms, @tailwindcss/typography)
- ✅ Build system: Gradle Node plugin z Tailwind watch mode
- ✅ Layouts: base.html, main.html, admin.html
- ✅ Pages: index.html, contact.html, blog/index.html, blog/article.html, admin/dashboard.html, admin/blogs/*.html, auth/login.html
- ✅ Components: navbar, footer, alerts, cards, buttons, inputs, textarea, accordion, breadcrumbs, pagination, blog components (article-card, article-content, related-articles)
- ✅ TinyMCE 6 WYSIWYG Editor: CDN integration, rich text toolbar, plugins (lists, link, image, charmap, preview, code, fullscreen, table, wordcount)
- ✅ AI Modal: prompt input (max 1000 chars), loading state, preview generated content, "Użyj tej treści" button

**Testing & CI/CD:**
- ✅ TestContainers integration (PostgreSQL 16-alpine)
- ✅ JaCoCo coverage reports (HTML + XML)
- ✅ GitHub Actions workflow: test + build + PR comments
- ✅ Artifacts: test reports, coverage reports, JAR (5-day retention)
- ✅ Test profile z automatycznym tworzeniem użytkownika (admin@lexpage.pl / admin123)

**Configuration:**
- ✅ application.properties (database, JPA, Liquibase, Thymeleaf, AI, static resources)
- ✅ application-test.properties (TestContainers, test user, DevTools)
- ✅ SecurityConfiguration, MapperConfiguration, TestUserProperties

### 🚧 W Trakcie / Częściowo Zaimplementowane

**Backend:**
- 🚧 Service (usługi prawne) - domain model istnieje, brak pełnego CRUD (zahardkodowane w PageController)
- 🚧 LawyerProfile - entity istnieje, brak use cases i controllerów
- 🚧 Image upload - entity istnieje, brak implementacji uploadu z kompresją

**Frontend:**
- 🚧 Strona usług - brak dedykowanego widoku dla szczegółów usługi
- 🚧 Profil prawnika - brak widoku

### ❌ Do Zrobienia (TODO)

**Security:**
- ❌ reCAPTCHA v3 integration (backend + frontend)
- ❌ HTTPS-only configuration

**Email:**
- ❌ Spring Mail SMTP configuration
- ❌ Async email notifications dla formularza kontaktowego
- ❌ Email templates (Thymeleaf)

**SEO:**
- ❌ Meta tags generation (dynamiczne dla artykułów, usług)
- ❌ Open Graph tags
- ❌ sitemap.xml generation
- ❌ robots.txt
- ❌ Schema.org JSON-LD markup

**Images:**
- ❌ Image upload endpoint
- ❌ Image compression (WebP conversion)
- ❌ Image storage strategy

**Content Management:**
- ❌ Service CRUD (use cases + controllers + views)
- ❌ LawyerProfile CRUD (use cases + controllers + views)

**Frontend Public:**
- ❌ Strona szczegółów usługi (template + controller)
- ❌ Social sharing buttons (Facebook, LinkedIn, X/Twitter)
- ❌ Related articles algorithm (teraz mock data)
- ❌ Google Maps embed w stopce
- ❌ Klikalny telefon (`tel:` links)

**Infrastructure:**
- ❌ Backup strategy (pg_dump cron, retention)
- ❌ VPS deployment configuration
- ❌ Nginx reverse proxy configuration
- ❌ Let's Encrypt SSL setup
- ❌ Spring Boot Actuator endpoints
- ❌ Monitoring (Google Analytics 4, UptimeRobot)

**AI Features:**
- ❌ AI generation daily limit (20 generowań/dzień) - obecnie brak limitu
- ❌ Prompt template optimization dla języka prawniczego
- ❌ AI generation history tracking (obecnie tylko podstawowe entity)

**Content:**
- ❌ 2 artykuły początkowe (seed data lub manualne utworzenie)
- ❌ 8-10 opisów usług (seed data)
- ❌ Bio prawnika (LawyerProfile seed data)

---

**Status**: W trakcie implementacji (MVP ~60% gotowe)
**Timeline**: 10-12 tygodni part-time (rozpoczęto ~2025-01-18)
**Ostatnia aktualizacja**: 2026-01-30