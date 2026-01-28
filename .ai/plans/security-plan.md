# Plan Implementacji Spring Security

## Cel
Zaimplementowanie Spring Security z uwierzytelnianiem opartym na formularzu logowania dla ścieżek `/admin/**` i `/api/**`. Zabezpieczenie aplikacji zgodnie z architekturą heksagonalną i zasadami DDD.

## Wymagania Funkcjonalne

### Autentykacja
- Login użytkownika: email (jako username)
- Hasło: przechowywane jako BCrypt hash (strength 12)
- Formularz logowania dla użytkowników nieuwierzytelnionych próbujących dostać się do `/admin/**`
- Wykorzystanie istniejącej tabeli `users` z kolumnami: id, username, password_hash, email, enabled

### Autoryzacja
- Ścieżki `/api/**` - wymagają uwierzytelnionego użytkownika
- Ścieżki `/admin/**` - wymagają uwierzytelnionego użytkownika
- Pozostałe ścieżki (publiczne strony) - dostępne bez uwierzytelniania

### Security Context
- W Spring Security Context trzymamy **UserPrincipal** - lekki obiekt implementujący UserDetails
- UserPrincipal zawiera minimalne informacje potrzebne do uwierzytelniania i autoryzacji:
  - UserId (UUID) - identyfikator użytkownika
  - Email - używany jako username
  - Username - nazwa użytkownika do wyświetlania
  - Enabled - czy konto aktywne
  - Authorities - uprawnienia użytkownika (na przyszłość)

**Uzasadnienie:** Nie trzymamy całego User domain object w Security Context, bo:
- Security Context powinien być lekki i serializowalny
- Domain User może zawierać dodatkową logikę biznesową niepotrzebną w kontekście security
- Separation of concerns - UserPrincipal to koncept infrastruktury, User to domena

---

## Architektura - Struktura Pakietów

Implementacja zgodna z **Hexagonal Architecture** (Ports & Adapters) i **DDD**.

### 1. Domain Layer (`domain/user/`)

**ZASADA:** Domain layer nie może mieć zależności od Spring Security! Spring Security to infrastruktura.

**Zmiany w istniejących klasach:**
- **User.java** - rozszerzyć o dodatkowe pola potrzebne do uwierzytelniania:
  - username (String) - nazwa użytkownika do wyświetlania
  - email (String) - email jako login
  - enabled (boolean) - czy konto aktywne
  - Dodać metody fabrykujące do tworzenia User z danych z bazy
  - Zachować niezmienność (final fields, brak setterów)

**Nowe klasy:**
- **Email.java** (Value Object jako Record) - reprezentuje email z walidacją:
  - Walidacja formatu email w compact constructor
  - Metoda `value()` zwracająca String
  - Niezmienne (Record)

**Wyjątki domenowe w `domain/user/exception/`:**
- **UserNotFoundException.java** - użytkownik nie znaleziony po email lub id
- **UserDisabledException.java** - próba logowania przez wyłączone konto
- **InvalidCredentialsException.java** - nieprawidłowe hasło

**UWAGA:** Domain NIE zawiera logiki hashowania haseł - to odpowiedzialność infrastructure layer!

---

### 2. Application Layer (`application/user/`)

**Use Cases (interfejsy w `application/user/`):**

#### AuthenticateUserUseCase
- Interfejs reprezentujący use case uwierzytelniania użytkownika
- Metoda: `AuthenticationResult execute(AuthenticateUserCommand command)`
- Command zawiera: email, rawPassword
- Result zawiera: UserId, email, username, enabled (wszystko co potrzebne do UserPrincipal)

**Commands (`application/user/command/`):**
- **AuthenticateUserCommand.java** (Record):
  - email (String)
  - rawPassword (String)
  - Walidacja w compact constructor (null checks)

**Results (`application/user/result/`):**
- **AuthenticationResult.java** (Record):
  - userId (UserId)
  - email (String)
  - username (String)
  - enabled (boolean)
  - Metoda fabrykująca `from(User user)`

**Ports (interfejsy w `application/user/ports/`):**

#### UserRepository (outbound port)
- Interfejs definiujący operacje na użytkownikach
- Metody:
  - `Optional<User> findByEmail(Email email)`
  - `Optional<User> findById(UserId userId)`
  - `User save(User user)`
- UWAGA: To interfejs w warstwie aplikacji, implementacja w infrastructure!

#### PasswordEncoder (outbound port)
- Interfejs abstrakcji nad hashowaniem haseł
- Metody:
  - `String encode(String rawPassword)` - hashowanie hasła
  - `boolean matches(String rawPassword, String encodedPassword)` - weryfikacja hasła
- UWAGA: Implementacja w infrastructure (BCrypt)!

**Application Service (`application/user/service/`):**

#### UserAuthenticationService
- Implementuje `AuthenticateUserUseCase`
- Zależności (przez constructor injection):
  - UserRepository (port)
  - PasswordEncoder (port)
- Logika:
  1. Wyszukanie użytkownika po email przez UserRepository
  2. Jeśli nie znaleziono - rzuć UserNotFoundException
  3. Sprawdzenie czy enabled - jeśli nie, rzuć UserDisabledException
  4. Weryfikacja hasła przez PasswordEncoder
  5. Jeśli hasło nieprawidłowe - rzuć InvalidCredentialsException
  6. Zwrócenie AuthenticationResult z danymi użytkownika
- Adnotacje: `@RequiredArgsConstructor`, `@Slf4j`
- UWAGA: To klasa w warstwie aplikacji, więc może mieć `@Service` (Spring) lub być registrowana w Configuration

---

### 3. Infrastructure Layer

#### 3.1 Adapters - Persistence (`infrastructure/adapters/persistence/`)

**Rozszerzenie UserEntity:**
- UserEntity już istnieje, upewnić się że zawiera wszystkie pola:
  - id (UUID)
  - username (String)
  - passwordHash (String)
  - email (String)
  - enabled (Boolean)
  - createdAt, updatedAt, deletedAt (z BaseEntity)

**Mapper (`infrastructure/adapters/persistence/mapper/`):**

#### UserMapper
- Klasa z metodami statycznymi do konwersji UserEntity ↔ User (domain)
- Metody:
  - `User toDomain(UserEntity entity)` - konwersja z entity do domain
  - `UserEntity toEntity(User domain)` - konwersja z domain do entity
- Mapowanie UserId ↔ UUID
- Mapowanie Email (value object) ↔ String

**Repository Implementation (`infrastructure/adapters/persistence/repository/`):**

#### SpringDataUserRepository (JPA Repository)
- Interfejs rozszerzający `JpaRepository<UserEntity, UUID>`
- Custom query methods:
  - `Optional<UserEntity> findByEmail(String email)`
  - `Optional<UserEntity> findByUsername(String username)`
  - `Optional<UserEntity> findByEmailAndEnabledTrue(String email)` - tylko aktywni użytkownicy

#### JpaUserRepositoryAdapter
- Implementuje `UserRepository` (port z application layer)
- Zależność: SpringDataUserRepository (JPA)
- Używa UserMapper do konwersji Entity ↔ Domain
- Implementuje metody z portu:
  - `findByEmail(Email email)` - deleguje do SpringDataUserRepository, mapuje result
  - `findById(UserId userId)` - analogicznie
  - `save(User user)` - konwersja do entity, zapis, konwersja do domain
- Adnotacje: `@Repository`, `@RequiredArgsConstructor`

---

#### 3.2 Adapters - Security (`infrastructure/adapters/security/`)

**UWAGA:** To nowy pakiet dla adapterów związanych z Spring Security.

**UserPrincipal (implementacja UserDetails):**
- Klasa implementująca `org.springframework.security.core.userdetails.UserDetails`
- Pola (wszystkie final):
  - userId (UserId) - z domeny
  - email (String) - jako username dla Spring Security
  - displayName (String) - username do wyświetlania
  - passwordHash (String) - zahashowane hasło
  - enabled (boolean) - czy konto aktywne
  - authorities (Collection<GrantedAuthority>) - uprawnienia (na razie pusta lista lub ROLE_USER)
- Implementacja metod UserDetails:
  - `getUsername()` - zwraca email
  - `getPassword()` - zwraca passwordHash
  - `isEnabled()` - zwraca enabled
  - `isAccountNonExpired()` - true (na razie bez expiration)
  - `isAccountNonLocked()` - true (na razie bez lockowania)
  - `isCredentialsNonExpired()` - true (na razie bez expiration)
  - `getAuthorities()` - zwraca authorities
- Metoda fabrykująca: `static UserPrincipal from(User user, String passwordHash)`
- Adnotacje: `@Getter` (tylko dla pól niestandardowych), reszta to implementacja interfejsu

**DomainUserDetailsService (implementacja UserDetailsService):**
- Implementuje `org.springframework.security.core.userdetails.UserDetailsService`
- Zależności:
  - UserRepository (port z application layer)
- Implementacja metody `loadUserByUsername(String email)`:
  1. Wyszukanie User przez UserRepository.findByEmail(Email.of(email))
  2. Jeśli nie znaleziono - rzuć `UsernameNotFoundException` (Spring Security exception)
  3. Konwersja User → UserPrincipal (przekazanie passwordHash z entity - trzeba go pobrać)
  4. Zwrócenie UserPrincipal
- Adnotacje: `@RequiredArgsConstructor`, `@Slf4j`
- UWAGA: Potrzebny dostęp do passwordHash z bazy - można rozszerzyć User domain o metodę `getPasswordHash()` lub stworzyć dodatkowy port

**BCryptPasswordEncoderAdapter (implementacja PasswordEncoder port):**
- Implementuje `PasswordEncoder` (port z application layer)
- Używa wewnętrznie `org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder` (strength 12)
- Implementacja metod:
  - `encode(String rawPassword)` - deleguje do BCrypt
  - `matches(String rawPassword, String encodedPassword)` - deleguje do BCrypt
- Adnotacje: `@RequiredArgsConstructor`
- Bean BCryptPasswordEncoder wstrzykiwany przez Configuration

---

#### 3.3 Configuration (`infrastructure/config/`)

**SecurityConfiguration:**
- Klasa konfiguracyjna Spring Security
- Adnotacje: `@Configuration`, `@EnableWebSecurity`
- Beany:

  **1. BCryptPasswordEncoder Bean:**
  - Zwraca `BCryptPasswordEncoder` z strength = 12
  - Używany przez BCryptPasswordEncoderAdapter

  **2. SecurityFilterChain Bean:**
  - Konfiguracja HttpSecurity:
    - **Autoryzacja (authorizeHttpRequests):**
      - `/admin/**` - wymaga uwierzytelnienia (`.authenticated()`)
      - `/api/**` - wymaga uwierzytelnienia (`.authenticated()`)
      - `/login`, `/css/**`, `/js/**`, `/images/**`, `/`, `/blog/**`, `/uslugi/**` - publiczne (`.permitAll()`)
      - Pozostałe ścieżki - publiczne (`.permitAll()`)

    - **Form Login (formLogin):**
      - Custom strona logowania: `/login` (GET)
      - Login processing URL: `/login` (POST)
      - Username parameter: `email` (zamiast domyślnego "username")
      - Password parameter: `password`
      - Default success URL: `/admin` (przekierowanie po udanym logowaniu)
      - Failure URL: `/login?error=true`
      - Permit all dla login page

    - **Logout (logout):**
      - Logout URL: `/logout` (POST)
      - Logout success URL: `/` (strona główna)
      - Invalidate HTTP session: true
      - Delete cookies: `JSESSIONID`
      - Permit all

    - **CSRF Protection:**
      - Enabled (domyślnie) - ważne dla formularzy
      - Token dla Thymeleaf: automatycznie wstrzykiwany przez Spring Security

    - **Session Management:**
      - Session creation policy: IF_REQUIRED (domyślna)
      - Max sessions per user: 1 (opcjonalnie)
      - Session fixation protection: migrateSession (domyślna)

    - **Security Headers:**
      - X-Frame-Options: DENY
      - X-Content-Type-Options: nosniff
      - X-XSS-Protection: 1; mode=block
      - Cache-Control dla zabezpieczonych stron

  **3. AuthenticationManager Bean (opcjonalnie):**
  - Jeśli potrzebny do ręcznego uwierzytelniania (np. w kontrolerach API)
  - Konfiguracja z DomainUserDetailsService

**PasswordEncoderConfiguration:**
- Osobna klasa konfiguracyjna dla PasswordEncoder (separacja odpowiedzialności)
- Bean `passwordEncoderAdapter()`:
  - Zwraca BCryptPasswordEncoderAdapter
  - Wstrzykuje BCryptPasswordEncoder bean
- Adnotacje: `@Configuration`

---

#### 3.4 Web - Controllers (`infrastructure/web/controller/`)

**LoginController:**
- Kontroler do wyświetlania formularza logowania i obsługi logout
- Mapowania:
  - `GET /login` - wyświetlenie formularza logowania (Thymeleaf template)
    - Parametr `error` (optional) - jeśli true, wyświetl komunikat o błędzie
    - Parametr `logout` (optional) - jeśli true, wyświetl komunikat o wylogowaniu
    - Model attributes:
      - `errorMessage` - jeśli error=true: "Nieprawidłowy email lub hasło"
      - `logoutMessage` - jeśli logout=true: "Zostałeś pomyślnie wylogowany"
    - Return: `pages/auth/login` (Thymeleaf template)

  - `GET /admin` - strona główna panelu administracyjnego (po zalogowaniu)
    - Wymaga uwierzytelnienia (zabezpieczone przez Security)
    - Pobiera aktualnie zalogowanego użytkownika z Security Context
    - Model attributes:
      - `username` - nazwa zalogowanego użytkownika
    - Return: `pages/admin/dashboard` (Thymeleaf template)

- Adnotacje: `@Controller` (nie RestController!), `@RequiredArgsConstructor`, `@Slf4j`
- Logowanie informacji o próbach logowania

**Rozszerzenie GlobalExceptionApiHandler:**
- Dodać handlery dla Spring Security exceptions (dla API endpoints):
  - `AuthenticationException` - 401 Unauthorized
  - `AccessDeniedException` - 403 Forbidden
  - `UsernameNotFoundException` - 401 Unauthorized (nie ujawniać szczegółów)
- Używać ProblemDetail (RFC 7807) zgodnie z istniejącą konwencją

---

#### 3.5 Web - Templates (`src/main/resources/templates/`)

**Login Page Template (`pages/auth/login.html`):**
- Formularz logowania Thymeleaf
- Layout: użycie istniejącego layout system (sprawdzić `layouts/default.html` lub stworzyć `layouts/auth.html`)
- Struktura formularza:
  - Action: `th:action="@{/login}"` (POST)
  - Method: POST
  - CSRF token: automatycznie dodawany przez Thymeleaf Spring Security integration
  - Pola:
    - Email input: `name="email"`, `type="email"`, `required`, `autofocus`
    - Password input: `name="password"`, `type="password"`, `required`
    - Submit button: "Zaloguj się"
  - Wyświetlanie komunikatów:
    - Błąd logowania: `th:if="${errorMessage}"` - czerwony komunikat
    - Sukces wylogowania: `th:if="${logoutMessage}"` - zielony komunikat
- Stylowanie:
  - Zgodnie z Material Tailwind (sprawdzić `.ai/rules/frontend.md`)
  - Responsive design (mobile-first)
  - Accessibility (labels, aria-labels)
  - Walidacja HTML5

**Admin Dashboard Template (`pages/admin/dashboard.html`):**
- Prosta strona powitalna po zalogowaniu
- Wyświetlenie nazwy zalogowanego użytkownika: `th:text="${username}"`
- Link do wylogowania:
  - Formularz POST do `/logout` (CSRF protected)
  - Button "Wyloguj się"
- Lista linków do funkcji administracyjnych (blog, użytkownicy, etc.)

**Fragmenty (`fragments/`):**
- **Admin Navigation Fragment (`fragments/admin/nav.html`):**
  - Nawigacja dla panelu admin
  - Wyświetlenie zalogowanego użytkownika
  - Link do logout
  - Menu: Dashboard, Blog, Użytkownicy, Ustawienia (placeholdery)

---

### 4. Database - Liquibase Migration

**UWAGA:** Tabela `users` już istnieje z odpowiednimi kolumnami. Należy tylko upewnić się, że schemat jest zgodny z wymaganiami.

**Weryfikacja istniejącej migracji (`02-create-users-table.xml`):**
- Sprawdzić czy kolumny są zgodne:
  - `id` - UUID (primary key)
  - `username` - VARCHAR(50), NOT NULL, UNIQUE
  - `password_hash` - VARCHAR(60), NOT NULL (BCrypt potrzebuje 60 znaków)
  - `email` - VARCHAR(255), NOT NULL, UNIQUE
  - `enabled` - BOOLEAN, NOT NULL, DEFAULT true
  - `created_at`, `updated_at`, `deleted_at` - timestamps

**Nowa migracja - Insert Test User (`13-insert-test-admin-user.xml`):**
- Wstawienie testowego użytkownika administracyjnego do tabeli `users`:
  - username: "admin"
  - email: "admin@example.com"
  - password_hash: BCrypt hash dla hasła "admin123" (strength 12)
  - enabled: true
- UWAGA: Hash BCrypt dla "admin123" to (przykład): `$2a$12$[generated-hash]`
- To tylko dla celów deweloperskich! W produkcji usunąć lub zmienić hasło!
- Rollback: usunięcie użytkownika

**Changelog master (`db.changelog-master.xml`):**
- Dodać include dla nowej migracji po migracji 12

---

## Kolejność Implementacji (Step-by-Step)

### Faza 1: Domain Layer
1. Rozszerzyć `User.java` o pola: username, email, enabled
2. Stworzyć `Email.java` (Value Object jako Record) z walidacją
3. Stworzyć wyjątki domenowe w `domain/user/exception/`:
   - UserNotFoundException
   - UserDisabledException
   - InvalidCredentialsException

### Faza 2: Application Layer
4. Stworzyć `PasswordEncoder.java` (port/interfejs) w `application/user/ports/`
5. Stworzyć `UserRepository.java` (port/interfejs) w `application/user/ports/`
6. Stworzyć `AuthenticateUserCommand.java` (Record) w `application/user/command/`
7. Stworzyć `AuthenticationResult.java` (Record) w `application/user/result/`
8. Stworzyć `AuthenticateUserUseCase.java` (interfejs) w `application/user/`
9. Stworzyć `UserAuthenticationService.java` (implementation) w `application/user/service/`

### Faza 3: Infrastructure - Persistence
10. Rozszerzyć `UserEntity.java` (jeśli potrzeba) w `infrastructure/adapters/persistence/entity/`
11. Stworzyć `UserMapper.java` w `infrastructure/adapters/persistence/mapper/`
12. Rozszerzyć `SpringDataUserRepository.java` o query methods
13. Stworzyć `JpaUserRepositoryAdapter.java` (implementacja UserRepository port)

### Faza 4: Infrastructure - Security Adapters
14. Stworzyć pakiet `infrastructure/adapters/security/`
15. Stworzyć `UserPrincipal.java` (implementacja UserDetails)
16. Stworzyć `DomainUserDetailsService.java` (implementacja UserDetailsService)
17. Stworzyć `BCryptPasswordEncoderAdapter.java` (implementacja PasswordEncoder port)

### Faza 5: Infrastructure - Configuration
18. Stworzyć `SecurityConfiguration.java` w `infrastructure/config/`
    - Bean BCryptPasswordEncoder (strength 12)
    - Bean SecurityFilterChain (konfiguracja HTTP security)
    - Konfiguracja form login, logout, CSRF, session management
19. Stworzyć `PasswordEncoderConfiguration.java` (opcjonalnie - separacja)

### Faza 6: Infrastructure - Web Controllers
20. Stworzyć `LoginController.java` w `infrastructure/web/controller/`
    - GET /login - wyświetlenie formularza
    - GET /admin - dashboard po zalogowaniu
21. Rozszerzyć `GlobalExceptionApiHandler.java`:
    - Handler dla AuthenticationException (401)
    - Handler dla AccessDeniedException (403)

### Faza 7: Infrastructure - Templates (Thymeleaf)
22. Stworzyć `pages/auth/login.html` - formularz logowania
23. Stworzyć `pages/admin/dashboard.html` - strona główna admin
24. Stworzyć `fragments/admin/nav.html` - nawigacja admin
25. Stworzyć lub rozszerzyć `layouts/admin.html` - layout dla panelu admin

### Faza 8: Database - Liquibase
26. Zweryfikować migrację `02-create-users-table.xml`
27. Stworzyć migrację `13-insert-test-admin-user.xml` (testowy użytkownik)
28. Zaktualizować `db.changelog-master.xml`

### Faza 9: Testowanie
29. Testy jednostkowe:
    - `UserAuthenticationServiceTest` - testowanie use case z mockami
    - `DomainUserDetailsServiceTest` - testowanie UserDetailsService
    - `BCryptPasswordEncoderAdapterTest` - testowanie encodera
30. Testy integracyjne:
    - `SecurityConfigurationIntegrationTest` - testowanie konfiguracji security
    - `LoginFlowIntegrationTest` - testowanie całego flow logowania
31. Manualne testowanie:
    - Próba dostępu do /admin bez logowania → przekierowanie na /login
    - Logowanie z poprawnymi credentials → przekierowanie na /admin
    - Logowanie z błędnymi credentials → komunikat błędu
    - Wylogowanie → przekierowanie na /
    - Próba dostępu do /api/** bez uwierzytelnienia → 401 Unauthorized

---

## Szczegóły Techniczne

### BCrypt Configuration
- Strength: 12 (zgodnie z tech-stack.md)
- Format hasła w bazie: `$2a$12$[hash]` (60 znaków)
- Przykład użycia do wygenerowania test password:

### CSRF Protection
- Enabled dla wszystkich POST/PUT/DELETE/PATCH requestów
- Token automatycznie dodawany do formularzy Thymeleaf przez Spring Security
- W formularzach Thymeleaf: automatyczne dodanie input hidden z CSRF token
- Dla API endpoints: można rozważyć wyłączenie CSRF dla /api/** (jeśli używane stateless JWT w przyszłości)

### Session Management
- Session creation: IF_REQUIRED
- Session fixation protection: migrateSession
- Max sessions per user: 1 (opcjonalnie - zapobiega równoczesnym sesjom)
- Timeout: domyślny Spring Boot (30 minut) - konfigurowalny w application.properties

### Authorities/Roles (Przyszłość)
- Na razie: każdy zalogowany użytkownik ma dostęp do /admin i /api
- W przyszłości: dodać tabelę `roles` i `user_roles` (many-to-many)
- Dodać enum `UserRole` (ADMIN, USER, EDITOR, etc.)
- Rozszerzyć UserPrincipal o authorities bazujące na rolach
- Użyć `.hasRole("ADMIN")` w SecurityFilterChain dla /admin/**

### Error Handling dla Security
- **401 Unauthorized** - brak uwierzytelnienia (dla API)
- **403 Forbidden** - brak uprawnień (dla API)
- **Redirect to /login** - dla web endpoints (/admin/**)
- Nie ujawniać szczegółów czy użytkownik istnieje (security best practice)

---

## Checklist Implementacji

- [ ] Faza 1: Domain Layer (User, Email, Exceptions)
- [ ] Faza 2: Application Layer (Use Cases, Ports, Services)
- [ ] Faza 3: Infrastructure - Persistence (Repositories, Mappers)
- [ ] Faza 4: Infrastructure - Security Adapters (UserPrincipal, UserDetailsService, PasswordEncoder)
- [ ] Faza 5: Infrastructure - Configuration (SecurityConfiguration)
- [ ] Faza 6: Infrastructure - Web Controllers (LoginController)
- [ ] Faza 7: Infrastructure - Templates (Login page, Admin dashboard)
- [ ] Faza 8: Database - Liquibase (Test user migration)
- [ ] Faza 9: Testowanie (Unit tests, Integration tests, Manual testing)

---

## Najlepsze Praktyki i Konwencje

### Zgodność z Zasadami Projektu
1. **Hexagonal Architecture:**
   - Domain layer bez zależności od Spring Security
   - Porty (interfejsy) definiowane w application/domain
   - Adaptery (implementacje) w infrastructure
   - Kierunek zależności: Infrastructure → Application → Domain

2. **DDD Principles:**
   - Email jako Value Object (Record)
   - User jako Aggregate Root
   - Wyjątki domenowe w domain/user/exception/
   - Ubiquitous Language (User, Email, Authentication, etc.)

3. **Lombok Usage:**
   - `@RequiredArgsConstructor` dla constructor injection
   - `@Slf4j` dla logowania
   - `@Getter` dla getterów (tylko gdzie potrzeba)
   - NIE używać `@Value` - zamiast tego Java Records

4. **Java Records:**
   - ZAWSZE dla Commands (AuthenticateUserCommand)
   - ZAWSZE dla Results (AuthenticationResult)
   - ZAWSZE dla Value Objects (Email)
   - ZAWSZE dla DTOs

5. **Exception Handling:**
   - Global Exception Handler (@RestControllerAdvice) dla API
   - ProblemDetail (RFC 7807) dla błędów API
   - Propagacja wyjątków bez try-catch w kontrolerach
   - Wyjątki domenowe w domain/*/exception/

6. **Logging:**
   - DEBUG: wejście/wyjście z metod uwierzytelniania
   - INFO: udane logowanie/wylogowanie użytkownika
   - WARN: nieudane próby logowania, wyłączone konta
   - ERROR: nieoczekiwane błędy w security filter chain

7. **Testing:**
   - Testy jednostkowe dla domain services (>90% coverage)
   - Testy jednostkowe dla application services z mockami (>80% coverage)
   - Testy integracyjne dla security configuration
   - Testy E2E dla login flow (opcjonalnie)

8. **Thymeleaf:**
   - Zgodnie z `.ai/rules/frontend.md`
   - Używać `<th:block th:replace>` zamiast `<div th:replace>` (unikanie infinite loops)
   - Material Tailwind dla stylowania
   - Mobile-first responsive design
   - Accessibility (ARIA labels, semantic HTML)

9. **Security Best Practices:**
   - Nie ujawniać czy użytkownik istnieje (ten sam komunikat błędu)
   - BCrypt z strength >= 12
   - CSRF protection enabled
   - Secure headers (X-Frame-Options, XSS-Protection)
   - Session fixation protection
   - Password nie logować (nawet w debug mode!)

---

## Potencjalne Wyzwania i Rozwiązania

### Problem 1: Dostęp do passwordHash w DomainUserDetailsService
**Wyzwanie:** Domain User może nie zawierać passwordHash (to szczegół infrastruktury).

**Rozwiązanie:**
- Opcja A: Rozszerzyć UserRepository port o metodę `Optional<String> findPasswordHashByEmail(Email email)`
- Opcja B: Dodać do User pole passwordHash (ale to łamie czystość domeny)
- Opcja C: UserMapper zwraca zarówno User jak i passwordHash w DTO
- **Rekomendacja:** Opcja A - port z dodatkową metodą dla passwordHash

### Problem 2: Integracja Spring Security z architekturą heksagonalną
**Wyzwanie:** Spring Security działa na poziomie infrastructure, ale potrzebuje dostępu do domeny.

**Rozwiązanie:**
- UserPrincipal w infrastructure/adapters/security/ - implementuje Spring Security UserDetails
- DomainUserDetailsService używa UserRepository port (dependency inversion)
- Security Configuration w infrastructure/config/ - wstrzykuje beany
- Domain pozostaje czysty - nie wie o Spring Security

### Problem 3: Formularz logowania a Thymeleaf layout system
**Wyzwanie:** Formularz logowania może potrzebować innego layoutu niż reszta strony.

**Rozwiązanie:**
- Stworzyć `layouts/auth.html` - minimalistyczny layout dla auth pages
- Login page używa tego layoutu zamiast default layout
- Zachować spójność stylowania (Material Tailwind)

### Problem 4: Test user password hash
**Wyzwanie:** Jak wygenerować BCrypt hash dla test usera w migracji?

**Rozwiązanie:**
- Użyć online BCrypt generator (z strength 12)
- Lub napisać prosty test w Java, który wygeneruje hash i wypisze do konsoli
- Umieścić hash w migracji jako hardcoded value
- Dodać komentarz w migracji z oryginalnym hasłem (dla dev purposes)

---

## Dokumentacja dla Przyszłych Rozszerzeń

### Role-Based Access Control (RBAC)
Gdy w przyszłości będzie potrzeba ról:
1. Dodać tabelę `roles` (id, name)
2. Dodać tabelę `user_roles` (user_id, role_id) - many-to-many
3. Dodać enum `UserRole` w domain
4. Rozszerzyć User o `Set<UserRole> roles`
5. Rozszerzyć UserPrincipal o mapowanie ról na GrantedAuthority
6. Użyć `.hasRole("ADMIN")` w SecurityFilterChain

### JWT dla API
Gdy w przyszłości API będzie potrzebowało JWT:
1. Dodać dependency `spring-boot-starter-oauth2-resource-server`
2. Stworzyć JwtAuthenticationFilter w infrastructure/adapters/security/
3. Skonfigurować SecurityFilterChain z osobną konfiguracją dla /api/**:
   - Form login dla /admin/**
   - JWT dla /api/**
4. Stworzyć endpoint /api/auth/login do generowania JWT
5. Dodać JWT token do AuthenticationResult

### Remember Me Functionality
Gdy będzie potrzeba "Zapamiętaj mnie":
1. Dodać tabelę `persistent_logins` (Liquibase migration)
2. Skonfigurować `.rememberMe()` w SecurityFilterChain:
   - Token validity (np. 14 dni)
   - Persistent token repository (database-backed)
3. Dodać checkbox "Zapamiętaj mnie" w formularzu logowania
4. Parameter name: `remember-me`

### Account Lockout po wielu nieudanych próbach
Gdy będzie potrzeba blokowania kont:
1. Dodać do UserEntity kolumny: `failed_attempts`, `locked_until`
2. Stworzyć AuthenticationFailureHandler w infrastructure/adapters/security/
3. Handler zwiększa failed_attempts przy każdej nieudanej próbie
4. Po N próbach: ustawić `locked_until` na teraz + X minut
5. W DomainUserDetailsService sprawdzać `locked_until`

---

## Podsumowanie

Plan implementacji Spring Security został przygotowany zgodnie z:
- **Hexagonal Architecture** (Ports & Adapters)
- **Domain-Driven Design** (Aggregates, Value Objects, Domain Services)
- **Zasadami projektu** z `.ai/rules/backend.md`
- **Tech stack** z `.ai/tech-stack.md`

Implementacja zapewni:
- ✅ Uwierzytelnianie oparte na email + hasło (BCrypt)
- ✅ Zabezpieczenie ścieżek `/admin/**` i `/api/**`
- ✅ Formularz logowania dla nieuwierzytelnionych użytkowników
- ✅ Lekki UserPrincipal w Security Context
- ✅ Czysty Domain Layer bez zależności od Spring Security
- ✅ Testowalne komponenty (porty, adaptery)
- ✅ Zgodność z najlepszymi praktykami security

Agent implementujący ten plan powinien postępować fazami (1-9) i zachować spójność z istniejącą architekturą projektu.
