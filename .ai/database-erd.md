# Database Schema - Entity Relationship Diagram

## Przegląd

Schemat bazy danych dla MVP strony kancelarii prawnej, zaprojektowany zgodnie z architekturą heksagonalną i zasadami DDD.

**Baza danych**: PostgreSQL 15+
**Zarządzanie migracjami**: Liquibase
**ORM**: Spring Data JPA / Hibernate 6.x

---

## Encje Główne

### 1. users

Tabela użytkowników systemu (Spring Security).

| Kolumna | Typ | Constraints | Opis |
|---------|-----|-------------|------|
| id | BIGSERIAL | PRIMARY KEY | ID użytkownika |
| username | VARCHAR(50) | UNIQUE NOT NULL | Nazwa użytkownika |
| password_hash | VARCHAR(60) | NOT NULL | BCrypt hash (strength 12) |
| email | VARCHAR(255) | UNIQUE NOT NULL | Email użytkownika |
| enabled | BOOLEAN | NOT NULL DEFAULT true | Czy konto aktywne |
| created_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data utworzenia |
| updated_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data ostatniej aktualizacji |

**Indeksy**:
- PRIMARY KEY na `id`
- UNIQUE INDEX na `username`
- UNIQUE INDEX na `email`

**Uwagi**:
- Pojedyncze konto administratora dla MVP
- BCrypt z siłą 12 dla hashowania haseł
- Pole `enabled` pozwala na soft-disable konta w przyszłości

---

### 2. articles

Tabela artykułów blogowych z workflow draft/publikacja.

| Kolumna | Typ | Constraints | Opis |
|---------|-----|-------------|------|
| id | BIGSERIAL | PRIMARY KEY | ID artykułu |
| title | VARCHAR(255) | NOT NULL | Tytuł artykułu |
| slug | VARCHAR(255) | UNIQUE NOT NULL | SEO-friendly URL slug |
| content | TEXT | NOT NULL | Treść artykułu (max 5000 słów) |
| excerpt | VARCHAR(500) | NULL | Krótki opis dla listy |
| status | article_status_enum | NOT NULL DEFAULT 'DRAFT' | Status: DRAFT, PUBLISHED, ARCHIVED |
| author_id | BIGINT | FK → users(id) NOT NULL | Autor artykułu |
| published_at | TIMESTAMP | NULL | Data publikacji |
| meta_title | VARCHAR(60) | NULL | SEO meta title |
| meta_description | VARCHAR(160) | NULL | SEO meta description |
| og_image_url | VARCHAR(500) | NULL | Open Graph image URL |
| canonical_url | VARCHAR(500) | NULL | Canonical URL |
| keywords | TEXT[] | NULL | SEO keywords (PostgreSQL array) |
| search_vector | TSVECTOR | NULL | Full-text search vector |
| created_by | BIGINT | FK → users(id) NOT NULL | Użytkownik który utworzył |
| updated_by | BIGINT | FK → users(id) NOT NULL | Użytkownik który zaktualizował |
| created_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data utworzenia |
| updated_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data ostatniej aktualizacji |
| deleted_at | TIMESTAMP | NULL | Soft delete timestamp |

**Indeksy**:
- PRIMARY KEY na `id`
- UNIQUE INDEX na `slug`
- INDEX na `(status, published_at DESC)` dla listing published articles
- GIN INDEX na `search_vector` dla full-text search
- GIN INDEX na `keywords` dla keyword search
- INDEX na `author_id`
- INDEX na `(deleted_at)` dla filtrowania soft-deleted

**Foreign Keys**:
- `author_id` → `users(id)` ON DELETE RESTRICT
- `created_by` → `users(id)` ON DELETE RESTRICT
- `updated_by` → `users(id)` ON DELETE RESTRICT

**Triggers**:
- Trigger auto-update `search_vector` z `title` + `content` (pl_PL dictionary)
- Trigger auto-update `updated_at` przy UPDATE

**Uwagi**:
- Soft delete z `deleted_at` dla możliwości recovery
- Full-text search z tsvector dla wydajności
- Relacja ManyToOne do User dla audytu i przyszłej skalowalności
- Keywords jako PostgreSQL array dla elastyczności

---

### 3. services

Tabela usług prawnych (cywilne/karne).

| Kolumna | Typ | Constraints | Opis |
|---------|-----|-------------|------|
| id | BIGSERIAL | PRIMARY KEY | ID usługi |
| name | VARCHAR(255) | NOT NULL | Nazwa usługi |
| slug | VARCHAR(255) | UNIQUE NOT NULL | SEO-friendly URL slug |
| description | TEXT | NOT NULL | Opis usługi |
| category | service_category_enum | NOT NULL | CIVIL_LAW lub CRIMINAL_LAW |
| scope | TEXT | NULL | Zakres usługi |
| process | TEXT | NULL | Przebieg usługi |
| faq | JSONB | NULL | FAQ jako JSON array |
| display_order | INTEGER | NOT NULL DEFAULT 0 | Kolejność wyświetlania |
| meta_title | VARCHAR(60) | NULL | SEO meta title |
| meta_description | VARCHAR(160) | NULL | SEO meta description |
| og_image_url | VARCHAR(500) | NULL | Open Graph image URL |
| keywords | TEXT[] | NULL | SEO keywords |
| search_vector | TSVECTOR | NULL | Full-text search vector |
| created_by | BIGINT | FK → users(id) NOT NULL | Użytkownik który utworzył |
| updated_by | BIGINT | FK → users(id) NOT NULL | Użytkownik który zaktualizował |
| created_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data utworzenia |
| updated_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data ostatniej aktualizacji |
| deleted_at | TIMESTAMP | NULL | Soft delete timestamp |

**Indeksy**:
- PRIMARY KEY na `id`
- UNIQUE INDEX na `slug`
- INDEX na `(category, display_order)` dla listing
- GIN INDEX na `search_vector` dla full-text search
- GIN INDEX na `faq` dla JSONB queries
- INDEX na `(deleted_at)`

**Foreign Keys**:
- `created_by` → `users(id)` ON DELETE RESTRICT
- `updated_by` → `users(id)` ON DELETE RESTRICT

**Triggers**:
- Trigger auto-update `search_vector` z `name` + `description`
- Trigger auto-update `updated_at` przy UPDATE

**CHECK Constraints**:
- `faq` JSONB validation (struktura: `[{"question": "...", "answer": "..."}]`)

**Uwagi**:
- FAQ jako JSONB dla prostoty MVP (łatwiejsza edycja, mniej JOIN-ów)
- Soft delete dla możliwości archiwizacji usług
- `display_order` dla kontroli kolejności na stronie

---

### 4. contact_messages

Tabela wiadomości z formularza kontaktowego.

| Kolumna | Typ | Constraints | Opis |
|---------|-----|-------------|------|
| id | BIGSERIAL | PRIMARY KEY | ID wiadomości |
| first_name | VARCHAR(100) | NOT NULL | Imię |
| last_name | VARCHAR(100) | NOT NULL | Nazwisko |
| email | VARCHAR(255) | NOT NULL | Email |
| phone | VARCHAR(20) | NULL | Telefon (opcjonalny) |
| category | message_category_enum | NOT NULL | CIVIL_LAW, CRIMINAL_LAW, GENERAL, OTHER |
| message | TEXT | NOT NULL CHECK(LENGTH(message) >= 50) | Wiadomość (min 50 znaków) |
| status | message_status_enum | NOT NULL DEFAULT 'NEW' | NEW, READ, REPLIED, ARCHIVED |
| recaptcha_score | NUMERIC(3,2) | NULL | reCAPTCHA v3 score (0.0-1.0) |
| ip_address | VARCHAR(45) | NULL | IP address (IPv4 lub IPv6) |
| user_agent | VARCHAR(500) | NULL | User agent string |
| created_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data utworzenia |
| updated_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data ostatniej aktualizacji |

**Indeksy**:
- PRIMARY KEY na `id`
- INDEX na `created_at DESC` dla admin panel sorting
- INDEX na `(status, created_at DESC)` dla filtrowania
- INDEX na `email` dla szukania po nadawcy

**Triggers**:
- Trigger auto-update `updated_at` przy UPDATE

**CHECK Constraints**:
- `LENGTH(message) >= 50` - minimum 50 znaków w wiadomości

**Uwagi**:
- Brak soft delete - wiadomości archiwizowane przez status
- `recaptcha_score` dla analizy spamu
- IP i User Agent dla bezpieczeństwa i diagnostyki

---

### 5. ai_generations

Tabela historii generowań AI z limitami.

| Kolumna | Typ | Constraints | Opis |
|---------|-----|-------------|------|
| id | BIGSERIAL | PRIMARY KEY | ID generowania |
| user_id | BIGINT | FK → users(id) NOT NULL | Użytkownik który generował |
| prompt | TEXT | NOT NULL | Prompt użyty do generowania |
| keywords | TEXT | NULL | Keywords użyte w generowaniu |
| word_count | INTEGER | NULL | Liczba słów w generowanej treści |
| generated_content | TEXT | NOT NULL | Wygenerowana treść |
| model | VARCHAR(50) | NOT NULL | Model AI (np. 'claude-3.5-sonnet') |
| tokens_used | INTEGER | NULL | Liczba tokenów użytych |
| generation_time_ms | INTEGER | NULL | Czas generowania w ms |
| status | generation_status_enum | NOT NULL | SUCCESS, FAILED, TIMEOUT |
| error_message | TEXT | NULL | Komunikat błędu jeśli failed |
| article_id | BIGINT | FK → articles(id) NULL | ID artykułu jeśli użyto |
| created_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data utworzenia |

**Indeksy**:
- PRIMARY KEY na `id`
- INDEX na `(user_id, created_at)` dla daily limit check
- INDEX na `article_id`
- INDEX na `created_at DESC` dla historii

**Foreign Keys**:
- `user_id` → `users(id)` ON DELETE CASCADE
- `article_id` → `articles(id)` ON DELETE SET NULL

**Uwagi**:
- Daily limit (20 generowań/dzień) sprawdzany przez query: `SELECT COUNT(*) WHERE user_id = ? AND DATE(created_at) = CURRENT_DATE`
- Index na `(user_id, created_at)` optymalizuje ten query
- Przechowywanie metadanych dla analizy użycia i kosztów

---

### 6. images

Tabela obrazów z kompresją (polymorphic relationship).

| Kolumna | Typ | Constraints | Opis |
|---------|-----|-------------|------|
| id | BIGSERIAL | PRIMARY KEY | ID obrazu |
| entity_type | VARCHAR(50) | NOT NULL CHECK(entity_type IN ('article', 'service')) | Typ encji |
| entity_id | BIGINT | NOT NULL | ID powiązanej encji |
| file_name | VARCHAR(255) | NOT NULL | Nazwa pliku |
| file_path | VARCHAR(500) | NOT NULL | Ścieżka do pliku |
| file_size | BIGINT | NOT NULL | Rozmiar pliku w bajtach |
| mime_type | VARCHAR(100) | NOT NULL | MIME type (image/jpeg, image/png) |
| width | INTEGER | NULL | Szerokość obrazu w px |
| height | INTEGER | NULL | Wysokość obrazu w px |
| alt_text | VARCHAR(255) | NULL | Alt text dla SEO |
| display_order | INTEGER | NOT NULL DEFAULT 0 | Kolejność wyświetlania |
| created_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data uploadu |

**Indeksy**:
- PRIMARY KEY na `id`
- INDEX na `(entity_type, entity_id)` dla listing obrazów encji
- PARTIAL INDEX na `(entity_id)` WHERE `entity_type = 'article'`
- PARTIAL INDEX na `(entity_id)` WHERE `entity_type = 'service'`

**Foreign Keys**:
- Brak bezpośrednich FK (polymorphic) - integralność sprawdzana przez aplikację

**CHECK Constraints**:
- `entity_type IN ('article', 'service')`
- `file_size > 0`

**Uwagi**:
- Polymorphic relationship dla elastyczności
- Partial indexes dla wydajności queries
- Kompresja obrazów obsługiwana przez aplikację
- Brak CASCADE delete - obrazy usuwane przez aplikację

---

### 7. lawyer_profile

Tabela profilu prawnika (singleton - 1 rekord).

| Kolumna | Typ | Constraints | Opis |
|---------|-----|-------------|------|
| id | INTEGER | PRIMARY KEY CHECK(id = 1) | ID (zawsze 1) |
| first_name | VARCHAR(100) | NOT NULL | Imię |
| last_name | VARCHAR(100) | NOT NULL | Nazwisko |
| title | VARCHAR(100) | NULL | Tytuł (np. 'Radca Prawny') |
| bio | TEXT | NOT NULL | Biografia |
| specializations | TEXT[] | NULL | Specjalizacje (array) |
| photo_url | VARCHAR(500) | NULL | URL do zdjęcia |
| phone | VARCHAR(20) | NULL | Telefon kontaktowy |
| email | VARCHAR(255) | NOT NULL | Email kontaktowy |
| office_address | TEXT | NULL | Adres kancelarii |
| google_maps_url | VARCHAR(500) | NULL | Google Maps embed URL |
| linkedin_url | VARCHAR(255) | NULL | LinkedIn profile |
| bar_association_number | VARCHAR(50) | NULL | Numer w izbie adwokackiej |
| created_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data utworzenia |
| updated_at | TIMESTAMP | NOT NULL DEFAULT NOW() | Data ostatniej aktualizacji |

**Indeksy**:
- PRIMARY KEY na `id`

**CHECK Constraints**:
- `id = 1` - tylko jeden rekord w tabeli

**Triggers**:
- Trigger auto-update `updated_at` przy UPDATE
- Trigger BEFORE INSERT blokujący wstawienie jeśli rekord już istnieje

**Uwagi**:
- Singleton pattern - tylko 1 rekord w tabeli
- Używany na stronie głównej i w stopce
- Trigger zapobiega dodaniu więcej niż 1 rekordu

---

## Typy ENUM

### article_status_enum
```sql
CREATE TYPE article_status_enum AS ENUM ('DRAFT', 'PUBLISHED', 'ARCHIVED');
```

### service_category_enum
```sql
CREATE TYPE service_category_enum AS ENUM ('CIVIL_LAW', 'CRIMINAL_LAW');
```

### message_category_enum
```sql
CREATE TYPE message_category_enum AS ENUM ('CIVIL_LAW', 'CRIMINAL_LAW', 'GENERAL', 'OTHER');
```

### message_status_enum
```sql
CREATE TYPE message_status_enum AS ENUM ('NEW', 'READ', 'REPLIED', 'ARCHIVED');
```

### generation_status_enum
```sql
CREATE TYPE generation_status_enum AS ENUM ('SUCCESS', 'FAILED', 'TIMEOUT');
```

---

## Relacje

### Diagram Relacji

```
users (1) ----< (N) articles [author_id]
users (1) ----< (N) articles [created_by]
users (1) ----< (N) articles [updated_by]
users (1) ----< (N) services [created_by]
users (1) ----< (N) services [updated_by]
users (1) ----< (N) ai_generations [user_id]

articles (1) ----< (N) images [entity_type='article', entity_id]
services (1) ----< (N) images [entity_type='service', entity_id]

articles (1) ----< (1) ai_generations [article_id] (optional)

lawyer_profile - singleton (no relations)
contact_messages - standalone (no relations)
```

### Szczegóły Relacji

1. **users → articles**:
   - Author relationship (ManyToOne): `articles.author_id → users.id`
   - Audit relationships: `created_by`, `updated_by`
   - ON DELETE RESTRICT - nie można usunąć user jeśli ma artykuły

2. **users → services**:
   - Audit relationships: `created_by`, `updated_by`
   - ON DELETE RESTRICT

3. **users → ai_generations**:
   - ManyToOne: `ai_generations.user_id → users.id`
   - ON DELETE CASCADE - przy usunięciu user usuwamy historię generowań

4. **articles → images**:
   - Polymorphic OneToMany (przez `entity_type='article'` + `entity_id`)
   - Brak FK - integralność sprawdzana przez aplikację

5. **services → images**:
   - Polymorphic OneToMany (przez `entity_type='service'` + `entity_id`)
   - Brak FK - integralność sprawdzana przez aplikację

6. **articles → ai_generations**:
   - Optional OneToOne: `ai_generations.article_id → articles.id`
   - ON DELETE SET NULL - przy usunięciu artykułu historia generowania pozostaje

---

## Strategie Indeksowania

### Full-Text Search Indexes
```sql
-- Articles
CREATE INDEX idx_articles_search_vector ON articles USING GIN(search_vector);

-- Services
CREATE INDEX idx_services_search_vector ON services USING GIN(search_vector);
```

### SEO i Performance Indexes
```sql
-- Articles listing (published)
CREATE INDEX idx_articles_status_published_at ON articles(status, published_at DESC)
WHERE deleted_at IS NULL;

-- Services listing
CREATE INDEX idx_services_category_order ON services(category, display_order)
WHERE deleted_at IS NULL;

-- Keywords search
CREATE INDEX idx_articles_keywords ON articles USING GIN(keywords);
CREATE INDEX idx_services_keywords ON services USING GIN(keywords);
```

### Daily Limit Check
```sql
-- AI Generations daily count
CREATE INDEX idx_ai_generations_daily_limit ON ai_generations(user_id, created_at);
```

### Admin Panel Sorting
```sql
-- Contact messages admin list
CREATE INDEX idx_contact_messages_created_at ON contact_messages(created_at DESC);
CREATE INDEX idx_contact_messages_status_created_at ON contact_messages(status, created_at DESC);
```

### Polymorphic Relationships
```sql
-- Images by entity (partial indexes dla wydajności)
CREATE INDEX idx_images_article ON images(entity_id) WHERE entity_type = 'article';
CREATE INDEX idx_images_service ON images(entity_id) WHERE entity_type = 'service';
```

---

## Triggers Auto-Update

### Updated At Trigger
```sql
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Zastosowanie na wszystkich tabelach z updated_at
CREATE TRIGGER update_articles_updated_at BEFORE UPDATE ON articles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_services_updated_at BEFORE UPDATE ON services
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- itd.
```

### Search Vector Auto-Update Triggers
```sql
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

-- Analogiczny trigger dla services (name + description)
```

### Singleton Enforcement Trigger
```sql
CREATE OR REPLACE FUNCTION enforce_lawyer_profile_singleton()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT COUNT(*) FROM lawyer_profile) >= 1 THEN
        RAISE EXCEPTION 'Only one lawyer profile record is allowed';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_lawyer_profile_singleton_trigger
BEFORE INSERT ON lawyer_profile
    FOR EACH ROW EXECUTE FUNCTION enforce_lawyer_profile_singleton();
```

---

## Uwagi Implementacyjne

### Soft Delete Pattern
- Tabele `articles` i `services` używają soft delete (`deleted_at`)
- Wszystkie queries muszą filtrować `WHERE deleted_at IS NULL`
- Partial indexes uwzględniają `WHERE deleted_at IS NULL` dla wydajności

### Audit Trail Pattern
- Wszystkie encje biznesowe mają `created_by`, `updated_by`, `created_at`, `updated_at`
- Triggers auto-update `updated_at` przy każdej zmianie
- Integralność referencyjna z `users(id)` przez FK z ON DELETE RESTRICT

### PostgreSQL Specific Features
- **ENUM types** dla typowanych wartości słownikowych
- **TEXT[] arrays** dla keywords i specializations
- **JSONB** dla FAQ (indexed dla queries)
- **TSVECTOR** dla full-text search z Polish dictionary
- **GIN indexes** dla JSONB, arrays, i tsvector
- **Partial indexes** dla polymorphic relationships i soft delete

### Daily Limit Implementation
Query sprawdzający daily limit AI generations:
```sql
SELECT COUNT(*)
FROM ai_generations
WHERE user_id = ?
  AND DATE(created_at) = CURRENT_DATE;
```
Index `idx_ai_generations_daily_limit` optymalizuje ten query.

---

## Następne Kroki

1. Implementacja migracji Liquibase w kolejnych changesetach
2. Stworzenie JPA entities w `infrastructure/adapters/persistence`
3. Stworzenie domain entities w `domain/` z rich behavior
4. Implementacja Repository interfaces (ports) w `application/`
5. Implementacja Repository implementations (adapters) w `infrastructure/adapters/persistence`

---

**Data utworzenia**: 2025-01-19
**Autor**: Claude Code
**Status**: Gotowy do implementacji
