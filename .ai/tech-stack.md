# Tech Stack - Lexpage

## Overview

Lexpage wykorzystuje nowoczesny stack technologiczny oparty na ekosystemie Java/Spring z architekturą heksagonalną i zasadami DDD.

## Core Technologies

### Java 25
- **Wersja**: Java 25 (LTS候选)
- **JVM**: Najnowsze funkcje języka i optymalizacje wydajności
- **Kluczowe funkcje**:
  - Virtual Threads (Project Loom) - lekkie wątki dla wysokiej skalowalności
  - Pattern Matching - uproszczona logika biznesowa
  - Records - immutable value objects dla DDD
  - Sealed Classes - modelowanie hierarchii domenowych
  - Text Blocks - czytelne zapytania SQL i szablony

**Best Practices**:
```java
// Value Objects jako Records
public record EmailAddress(String value) {
    public EmailAddress {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}

// Sealed Classes dla hierarchii domenowych
public sealed interface PaymentMethod permits CreditCard, BankTransfer, Cash {
    Money calculateFee();
}

// Virtual Threads dla operacji I/O
@Bean
public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
    return protocolHandler -> {
        protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    };
}
```

### Spring Boot 4.0.1
- **Framework**: Najnowsza wersja Spring Boot z Spring Framework 6.x
- **Kluczowe moduły**:
  - Spring Web MVC - web layer
  - Spring Data JPA - persistence
  - Spring Security - authentication/authorization
  - Spring Validation - input validation
  - Spring Mail - email notifications
  - Spring Actuator - health checks i monitoring

**Best Practices**:
```java
// Application Configuration
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class LexpageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LexpageApplication.class, args);
    }
}

// Profiles dla różnych środowisk
# application-dev.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# application-prod.properties
spring.jpa.show-sql=false
server.compression.enabled=true
```

### PostgreSQL 15+
- **Database**: Nowoczesna relacyjna baza danych
- **Kluczowe funkcje**:
  - JSONB dla elastycznych struktur danych
  - Full-text search dla wyszukiwania artykułów
  - Partitioning dla dużych tabel (artykuły, logi)
  - Row Level Security dla multi-tenancy (przyszłość)
  - pg_trgm dla fuzzy search

**Best Practices**:
```sql
-- Indeksy dla wydajności
CREATE INDEX idx_articles_published_at ON articles(published_at DESC)
  WHERE status = 'PUBLISHED';

CREATE INDEX idx_articles_full_text ON articles
  USING gin(to_tsvector('polish', title || ' ' || content));

-- JSONB dla metadanych
CREATE TABLE articles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_articles_metadata ON articles USING gin(metadata);

-- Constraints na poziomie bazy
ALTER TABLE articles
  ADD CONSTRAINT chk_title_length CHECK (char_length(title) >= 10);
```

**Connection Pool Configuration**:
```properties
# HikariCP - najszybszy connection pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.leak-detection-threshold=60000
```

### Liquibase
- **Purpose**: Database version control i migrations
- **Strategy**: Incremental changesets z rollback support

**Best Practices**:
```yaml
# db/changelog/db.changelog-master.yaml
databaseChangeLog:
  - include:
      file: db/changelog/v1.0/01-initial-schema.yaml
  - include:
      file: db/changelog/v1.0/02-articles-table.yaml
  - include:
      file: db/changelog/v1.0/03-users-table.yaml
  - include:
      file: db/changelog/v1.1/01-add-categories.yaml
```

```yaml
# db/changelog/v1.0/02-articles-table.yaml
databaseChangeLog:
  - changeSet:
      id: create-articles-table
      author: developer
      changes:
        - createTable:
            tableName: articles
            columns:
              - column:
                  name: id
                  type: bigserial
                  constraints:
                    primaryKey: true
              - column:
                  name: title
                  type: varchar(255)
                  constraints:
                    nullable: false
              - column:
                  name: slug
                  type: varchar(255)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: content
                  type: text
                  constraints:
                    nullable: false
              - column:
                  name: status
                  type: varchar(20)
                  defaultValue: DRAFT
              - column:
                  name: published_at
                  type: timestamp
              - column:
                  name: created_at
                  type: timestamp
                  defaultValueComputed: CURRENT_TIMESTAMP
              - column:
                  name: updated_at
                  type: timestamp
      rollback:
        - dropTable:
            tableName: articles
```

### Spring Data JPA / Hibernate
- **ORM**: Hibernate 6.x z Jakarta Persistence API
- **Strategy**: Repository pattern z custom queries

**Best Practices**:
```java
// Domain Entity (Rich Model)
@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // Rich domain behavior
    public void publish() {
        if (this.status == ArticleStatus.PUBLISHED) {
            throw new IllegalStateException("Article already published");
        }
        this.status = ArticleStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public boolean isPublished() {
        return status == ArticleStatus.PUBLISHED;
    }
}

// Repository Interface (Port)
public interface ArticleRepository {
    Article save(Article article);
    Optional<Article> findById(Long id);
    Optional<Article> findBySlug(String slug);
    List<Article> findPublishedArticles(Pageable pageable);
}

// JPA Implementation (Adapter)
interface JpaArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findBySlug(String slug);

    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' " +
           "ORDER BY a.publishedAt DESC")
    List<Article> findPublishedArticles(Pageable pageable);
}

@Repository
class ArticleRepositoryAdapter implements ArticleRepository {
    private final JpaArticleRepository jpaRepository;

    // Delegation to JPA repository
}
```

**Performance Optimization**:
```properties
# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.fetch_size=50
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.generate_statistics=false

# Query optimization
spring.jpa.properties.hibernate.query.plan_cache_max_size=2048
spring.jpa.properties.hibernate.query.plan_parameter_metadata_max_size=128
```

### Thymeleaf
- **Template Engine**: Server-side rendering dla SEO
- **Version**: Thymeleaf 3.1+
- **Integration**: Spring Boot auto-configuration

**Best Practices**:
```html
<!-- Layout Pattern - layout.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title layout:title-pattern="$CONTENT_TITLE - $LAYOUT_TITLE">Kancelaria</title>

    <!-- SEO Meta Tags -->
    <meta th:if="${seo}" name="description" th:content="${seo.description}">
    <meta th:if="${seo}" property="og:title" th:content="${seo.ogTitle}">
    <meta th:if="${seo}" property="og:description" th:content="${seo.ogDescription}">

    <!-- Security Headers -->
    <meta http-equiv="X-Content-Type-Options" content="nosniff">
    <meta http-equiv="X-Frame-Options" content="DENY">

    <link rel="stylesheet" th:href="@{/css/main.css}">
</head>
<body>
    <nav th:replace="~{fragments/nav :: nav}"></nav>

    <main layout:fragment="content">
        <!-- Page content -->
    </main>

    <footer th:replace="~{fragments/footer :: footer}"></footer>
</body>
</html>

<!-- Article Page -->
<html layout:decorate="~{layout/layout}">
    <div layout:fragment="content">
        <article>
            <h1 th:text="${article.title}">Article Title</h1>
            <time th:datetime="${article.publishedAt}"
                  th:text="${#temporals.format(article.publishedAt, 'dd MMM yyyy')}">
            </time>
            <!-- Sanitized HTML content -->
            <div th:utext="${article.sanitizedContent}"></div>
        </article>
    </div>
</html>
```

```properties
# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.mode=HTML
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Production settings
# spring.thymeleaf.cache=true
```

## Build & Development Tools

### Gradle 8.x
- **Build Tool**: Declarative builds z Kotlin DSL
- **Key Plugins**:
  - Spring Boot Gradle Plugin
  - Spring Dependency Management
  - JaCoCo (code coverage)
  - SpotBugs (static analysis)

**build.gradle.kts**:
```kotlin
plugins {
    java
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.4"
    id("jacoco")
    id("com.github.spotbugs") version "6.0.0"
}

group = "pl.klastbit"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Thymeleaf Layout
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.3.0")

    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")

    // Utilities
    implementation("org.apache.commons:commons-lang3")
    implementation("org.jsoup:jsoup:1.17.2") // HTML sanitization

    // Development
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

jacoco {
    toolVersion = "0.8.11"
}
```

### Testing Stack

**JUnit 5 + Spring Boot Test**:
```java
// Unit Tests - Domain Layer
@Test
void shouldPublishArticle() {
    var article = new Article("Title", "slug", "content");
    article.publish();

    assertThat(article.isPublished()).isTrue();
    assertThat(article.getPublishedAt()).isNotNull();
}

// Integration Tests - Application Layer
@SpringBootTest
@Transactional
class ArticleServiceIntegrationTest {
    @Autowired
    private ArticleService articleService;

    @Test
    void shouldCreateAndPublishArticle() {
        var command = new CreateArticleCommand("Title", "content");
        var articleId = articleService.createArticle(command);

        articleService.publishArticle(articleId);

        var article = articleService.findById(articleId);
        assertThat(article.status()).isEqualTo(ArticleStatus.PUBLISHED);
    }
}

// Testcontainers - Infrastructure Tests
@SpringBootTest
@Testcontainers
class ArticleRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldSaveAndRetrieveArticle() {
        // Test implementation
    }
}

// ArchUnit - Architecture Tests
@AnalyzeClasses(packages = "pl.klastbit.lexpage")
class ArchitectureTest {
    @ArchTest
    static final ArchRule domainShouldNotDependOnInfrastructure =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule servicesShouldBeAnnotated =
        classes()
            .that().resideInAPackage("..application..")
            .and().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(Service.class);
}
```

## Security

### Spring Security
- **Authentication**: Form-based login z session management
- **Password Encoding**: BCrypt (strength 12)
- **CSRF Protection**: Enabled dla form submissions
- **Security Headers**: Comprehensive security headers

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/articles/**", "/services/**",
                                "/contact", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin/dashboard")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'")
                )
                .frameOptions().deny()
                .xssProtection().and()
                .contentTypeOptions().and()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

### Input Validation & Sanitization

```java
// Bean Validation
public record CreateArticleCommand(
    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 255, message = "Title must be between 10 and 255 characters")
    String title,

    @NotBlank(message = "Content is required")
    @Size(min = 100, max = 50000, message = "Content must be between 100 and 50000 characters")
    String content,

    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers and hyphens")
    String slug
) {}

// HTML Sanitization
@Component
public class HtmlSanitizer {
    private static final Safelist WHITELIST = Safelist.relaxed()
        .addTags("h2", "h3")
        .removeTags("img") // Images handled separately
        .addAttributes("a", "target", "rel")
        .addProtocols("a", "href", "http", "https");

    public String sanitize(String html) {
        return Jsoup.clean(html, WHITELIST);
    }
}
```

## Monitoring & Observability

### Spring Boot Actuator
```properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.metrics.tags.application=${spring.application.name}

# Health checks
management.health.db.enabled=true
management.health.diskspace.enabled=true
```

### Logging
```xml
<!-- logback-spring.xml -->
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <springProfile name="dev">
        <logger name="pl.klastbit.lexpage" level="DEBUG"/>
        <logger name="org.hibernate.SQL" level="DEBUG"/>
    </springProfile>

    <springProfile name="prod">
        <logger name="pl.klastbit.lexpage" level="INFO"/>
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/application.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
        </appender>
    </springProfile>
</configuration>
```

## Performance Best Practices

### Caching
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        var cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Arrays.asList("articles", "services"));
        return cacheManager;
    }
}

@Service
public class ArticleService {
    @Cacheable(value = "articles", key = "#slug")
    public Article findBySlug(String slug) {
        return articleRepository.findBySlug(slug)
            .orElseThrow(() -> new ArticleNotFoundException(slug));
    }

    @CacheEvict(value = "articles", key = "#article.slug")
    public void updateArticle(Article article) {
        articleRepository.save(article);
    }
}
```

### Async Processing
```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}

@Service
public class EmailService {
    @Async
    public CompletableFuture<Void> sendContactNotification(ContactInquiry inquiry) {
        // Send email asynchronously
        return CompletableFuture.completedFuture(null);
    }
}
```

## Development Workflow

### Hot Reload
```properties
# Spring Boot DevTools
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
spring.thymeleaf.cache=false
```

### Profiles
- **dev**: Development z debug logging
- **test**: Testing z H2/Testcontainers
- **prod**: Production z optymalizacjami

```properties
# application-dev.properties
spring.jpa.show-sql=true
logging.level.pl.klastbit.lexpage=DEBUG

# application-prod.properties
spring.jpa.show-sql=false
logging.level.pl.klastbit.lexpage=INFO
server.compression.enabled=true
server.http2.enabled=true
```

## CI/CD Integration

### GitHub Actions
```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 25
        uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'

      - name: Build with Gradle
        run: ./gradlew build

      - name: Run tests
        run: ./gradlew test

      - name: Generate coverage report
        run: ./gradlew jacocoTestReport

      - name: Upload coverage
        uses: codecov/codecov-action@v3
```

---

**Ostatnia aktualizacja**: 2025-01-19
**Stack Version**: Java 25, Spring Boot 4.0.1, PostgreSQL 15+
