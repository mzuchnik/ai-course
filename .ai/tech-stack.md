# Tech Stack - Lexpage

## Overview

Lexpage wykorzystuje nowoczesny stack technologiczny oparty na ekosystemie Java/Spring z architekturą heksagonalną i zasadami DDD.

## Core Technologies

### Java 25
- **Wersja**: Java 25
- **Kluczowe funkcje**: Virtual Threads, Pattern Matching, Records, Sealed Classes, Text Blocks
- **Value Objects**: Używaj Records dla immutable value objects w warstwie domenowej

### Spring Boot 4.0.1
- **Framework**: Spring Boot 4.0.1 + Spring Framework 6.x
- **Kluczowe moduły**:
  - Spring Web MVC
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring Mail
  - Spring Actuator
- **Profiles**: dev, test, prod

### PostgreSQL 15+
- **Wersja**: PostgreSQL 15 lub nowsza
- **Funkcje**: JSONB, Full-text search, Partitioning
- **Connection Pool**: HikariCP (domyślny w Spring Boot)

### Liquibase
- **Wersja**: Wbudowana w Spring Boot
- **Purpose**: Database migrations i version control
- **Lokalizacja**: `src/main/resources/db/changelog/`

### Spring Data JPA / Hibernate
- **ORM**: Hibernate 6.x + Jakarta Persistence API
- **Pattern**: Repository Pattern z architekturą heksagonalną

**Przykład encji**:
```java
@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    // Rich domain behavior
    public void publish() {
        if (this.status == ArticleStatus.PUBLISHED) {
            throw new IllegalStateException("Already published");
        }
        this.status = ArticleStatus.PUBLISHED;
    }
}
```

### Thymeleaf 3.1+
- **Purpose**: Server-side rendering dla SEO
- **Integration**: Spring Boot auto-configuration
- **Layout**: Thymeleaf Layout Dialect 3.3.0
- **Lokalizacja**: `src/main/resources/templates/`

## Build & Development Tools

### Gradle 8.x
- **Build Tool**: Gradle z Kotlin DSL
- **Kluczowe pluginy**:
  - Spring Boot Gradle Plugin 4.0.1
  - Spring Dependency Management 1.1.4
  - JaCoCo (code coverage)
  - SpotBugs (static analysis)

### Kluczowe Dependencies
- **Spring Boot Starters**: web, data-jpa, security, thymeleaf, validation, mail, actuator
- **Database**: PostgreSQL driver, Liquibase
- **Utilities**: Apache Commons Lang3, Jsoup (HTML sanitization)
- **Development**: Spring Boot DevTools
- **Testing**: JUnit 5, Spring Boot Test, Testcontainers, ArchUnit

## Security & Validation

### Spring Security
- **Authentication**: Form-based login
- **Password Encoding**: BCrypt (strength 12)
- **CSRF Protection**: Enabled
- **Security Headers**: CSP, X-Frame-Options, XSS Protection

### Input Validation
- **Bean Validation**: JSR-380 annotations (@NotBlank, @Size, @Pattern)
- **HTML Sanitization**: Jsoup z whitelistą tagów (p, h2, h3, ul, li, strong, em, a)

## Monitoring & Performance

### Spring Boot Actuator
- **Endpoints**: health, info, metrics, prometheus
- **Health Checks**: Database, disk space

### Logging
- **Framework**: Logback
- **Levels**: DEBUG (dev), INFO (prod)
- **Output**: Console (dev), rotating files (prod)

### Performance
- **Caching**: Spring Cache z ConcurrentMapCacheManager
- **Async Processing**: @Async z ThreadPoolTaskExecutor
- **Virtual Threads**: Enabled dla operacji I/O

## Development & Deployment

### Development Workflow
- **Hot Reload**: Spring Boot DevTools
- **Profiles**: dev (debug), test (testcontainers), prod (optimized)
- **Local Development**: `./gradlew bootRun`

### CI/CD
- **Platform**: GitHub Actions
- **Pipeline**: Build → Test → Coverage Report
- **Deployment**: Automatyczny deployment na VPS

---

**Ostatnia aktualizacja**: 2025-01-19
**Stack Version**: Java 25, Spring Boot 4.0.1, PostgreSQL 15+
