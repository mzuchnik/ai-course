# Lexpage

> Nowoczesna strona internetowa dla kancelarii prawnej z AI-powered content generation

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## O Projekcie

**Lexpage** to kompletna platforma webowa dla kancelarii prawnej, łącząca funkcjonalność strony informacyjnej, bloga prawnego oraz panelu administracyjnego z zaawansowanym generatorem treści wykorzystującym AI.

Projekt zbudowany w oparciu o architekturę heksagonalną (Ports & Adapters) i zasady Domain-Driven Design (DDD), zapewnia wysoką jakość kodu, testowalność oraz łatwość utrzymania.

## Kluczowe Funkcjonalności

### Frontend Publiczny
- **Strona główna**: Hero section, prezentacja prawnika, najnowsze artykuły, call-to-action
- **Katalog usług**: Podział na prawo cywilne i karne z subtelnym różnicowaniem kolorystycznym
- **Blog prawny**: System artykułów z paginacją, formatowaniem HTML, SEO-friendly
- **Formularz kontaktowy**: Walidacja, rate limiting (3 wiadomości/IP/1h), kategoryzacja spraw
- **Responsive Design**: Material Tailwind HTML + Tailwind CSS 3.4.1

### Panel Administracyjny
- **Generator AI**: Spring AI 2.0.0-M2 + OpenRouter (Claude/GPT) do tworzenia szkiców artykułów
- **WYSIWYG Editor**: TinyMCE 6 z pełnym toolbarem formatowania
- **Zarządzanie treścią**: CRUD artykułów ze statusami (DRAFT, PUBLISHED, ARCHIVED)
- **Workflow**: AI generation → edycja → podgląd → publikacja
- **Bezpieczeństwo**: Spring Security, form login, CSRF protection

### Backend & Infrastructure
- **Architektura**: Hexagonal Architecture + DDD
- **Database**: PostgreSQL 16+ z Liquibase migrations
- **AI Integration**: Configurable model (Claude/GPT) via OpenRouter API
- **Testing**: TestContainers (PostgreSQL 16-alpine) + JaCoCo coverage reports
- **CI/CD**: GitHub Actions z automatycznymi testami i raportami

## Stack Technologiczny

### Backend
| Technologia | Wersja | Zastosowanie |
|------------|--------|--------------|
| **Java** | 25 | Virtual Threads, Records, Pattern Matching |
| **Spring Boot** | 4.0.1 | Framework aplikacji |
| **Spring Security** | 6.x | Autentykacja i autoryzacja |
| **Spring Data JPA** | 3.x | Persistence layer |
| **Spring AI** | 2.0.0-M2 | AI content generation |
| **PostgreSQL** | 16+ | Relational database |
| **Liquibase** | - | Database migrations |
| **Hibernate** | 6.x | ORM |

### Frontend
| Technologia | Wersja | Zastosowanie |
|------------|--------|--------------|
| **Thymeleaf** | 3.1+ | Server-Side Rendering |
| **Tailwind CSS** | 3.4.1 | Utility-first CSS framework |
| **Material Tailwind HTML** | 2.3.2 | UI Components |
| **TinyMCE** | 6 | WYSIWYG Editor |

### Build & DevOps
| Technologia | Zastosowanie |
|------------|--------------|
| **Gradle** | 8.x (Kotlin DSL) |
| **Gradle Node Plugin** | 7.1.0 (Node 20.11.0, NPM 10.2.4) |
| **TestContainers** | Integration testing |
| **JaCoCo** | Code coverage |
| **GitHub Actions** | CI/CD pipeline |
| **Docker** | Containerization (TestContainers) |

## Wymagania

- **Java**: 25 (Oracle JDK lub OpenJDK)
- **PostgreSQL**: 16+
- **Docker**: Wymagane dla TestContainers (testy integracyjne)
- **Gradle**: 8.x (wrapper included)
- **Node.js**: 20.11.0 (automatycznie instalowany przez Gradle Node Plugin)

## Quick Start

### 1. Klonowanie repozytorium
```bash
git clone https://github.com/twoje-repo/lexpage.git
cd lexpage
```

### 2. Konfiguracja bazy danych
Utwórz bazę danych PostgreSQL:
```sql
CREATE DATABASE lexpage;
CREATE USER lexpage_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE lexpage TO lexpage_user;
```

Skonfiguruj connection w `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/lexpage
spring.datasource.username=lexpage_user
spring.datasource.password=your_password
```

### 3. Build projektu
```bash
./gradlew build
```
Komenda ta:
- Kompiluje kod Java
- Buduje frontend (Tailwind CSS)
- Uruchamia testy z JaCoCo coverage
- Generuje raporty (HTML + XML)

### 4. Uruchomienie aplikacji

**Profil testowy** (z testowym użytkownikiem):
```bash
./gradlew bootRun --args='--spring.profiles.active=test'
```
Dane testowe:
- Email: `admin@lexpage.pl`
- Hasło: `admin123`

**Profil produkcyjny**:
```bash
./gradlew bootRun
```

### 5. Dostęp do aplikacji
- **Strona główna**: http://localhost:8080
- **Panel admin**: http://localhost:8080/admin/dashboard
- **Login**: http://localhost:8080/login

## Testing

### Uruchomienie testów
```bash
./gradlew test
```

### Raport pokrycia kodu (JaCoCo)
Automatycznie generowany po testach:
- **HTML**: `build/reports/jacoco/test/html/index.html`
- **XML**: `build/reports/jacoco/test/jacocoTestReport.xml`

### Testy integracyjne
Projekt wykorzystuje **TestContainers** do testów integracyjnych z prawdziwą bazą PostgreSQL:
- Automatyczne uruchomienie PostgreSQL 16-alpine w kontenerze Docker
- Izolacja testów
- Automatyczna migracja Liquibase

### CI/CD
GitHub Actions automatycznie:
- Uruchamia wszystkie testy
- Generuje raporty JaCoCo
- Buduje artefakty (JAR, raporty)
- Komentuje Pull Requesty ze statusem
- Przechowuje artefakty przez 5 dni

## Architektura

Projekt zbudowany w oparciu o **Hexagonal Architecture** (Ports & Adapters) i **Domain-Driven Design**:

```
pl.klastbit.lexpage/
├── domain/               # Warstwa domenowa (niezależna od frameworków)
│   ├── model/           # Encje, Value Objects, Aggregates
│   ├── service/         # Domain Services
│   └── event/           # Domain Events
├── application/          # Warstwa aplikacji
│   ├── usecase/         # Use Cases (Application Services)
│   └── port/            # Port Interfaces (in/out)
└── infrastructure/       # Warstwa infrastruktury
    ├── web/             # REST/MVC Controllers (Inbound Adapters)
    ├── adapters/        # Database, External APIs (Outbound Adapters)
    └── config/          # Spring Configuration
```

### Kluczowe zasady:
- **Domain Layer**: Biznesowa logika, encje z zachowaniami, value objects
- **Application Layer**: Orkiestracja use cases, porty (interfejsy)
- **Infrastructure Layer**: Adaptery implementujące porty (JPA, REST, AI)
- **Dependency Rule**: Infrastructure → Application → Domain

## Development Workflow

### Tailwind CSS (Watch Mode)
```bash
./gradlew tailwindWatch
```
Automatyczne przebudowanie CSS przy zmianach w plikach źródłowych.

### Hot Reload
Spring Boot DevTools włączony w profilu `test`:
- Automatyczne przeładowanie zmian w kodzie
- LiveReload dla przeglądarki

### Clean Build
```bash
./gradlew clean build
```

## Status Projektu

**MVP: ~60% gotowe** (ostatnia aktualizacja: 2026-01-30)

### Zaimplementowane
- Domain model (Article, ContactMessage, Service, User, AIGeneration)
- Use Cases (CRUD artykułów, generator AI, formularz kontaktowy)
- Spring Security z form login
- TinyMCE 6 WYSIWYG Editor
- AI Modal z Spring AI + OpenRouter
- Rate limiting (3 msg/IP/1h)
- TestContainers + JaCoCo
- GitHub Actions CI/CD
- Material Tailwind HTML components

### W trakcie
- Service CRUD (entity istnieje, brak pełnego UI)
- LawyerProfile management
- Image upload z kompresją

### TODO
- reCAPTCHA v3 integration
- Email notifications (Spring Mail)
- SEO optimization (meta tags, sitemap, schema.org)
- Social sharing buttons
- Google Maps embed
- AI daily limit (20 generowań/dzień)
- Backup strategy (pg_dump cron)
- VPS deployment + Nginx + SSL

## Roadmap

### Phase 1: Core MVP
- [x] Domain model + Use Cases
- [x] Spring Security authentication
- [x] AI content generation
- [x] WYSIWYG Editor
- [x] Blog frontend
- [x] Contact form with rate limiting
- [ ] reCAPTCHA v3
- [ ] Email notifications

### Phase 2: Content Management
- [ ] Service CRUD (pełny flow)
- [ ] LawyerProfile management
- [ ] Image upload + compression (WebP)
- [ ] SEO optimization

### Phase 3: Production Ready
- [ ] VPS deployment
- [ ] Nginx reverse proxy
- [ ] Let's Encrypt SSL
- [ ] Backup automation
- [ ] Monitoring (Analytics, UptimeRobot)
- [ ] Performance optimization

## Contributing

Projekt rozwijany w ramach portfolio. Sugestie i feedback mile widziane.

## Licencja

MIT License - see [LICENSE](LICENSE) file for details.

---

**Autor**: [Mateusz Zuchnik](https://github.com/mzuchnik)
**Technologie**: Java 25 | Spring Boot 4.0.1 | PostgreSQL 16 | Spring AI 2.0.0-M2
**Timeline**: 10-12 tygodni (part-time)
