# CSRF Configuration - Lexpage

## Problem

Domyślnie Spring Security włącza CSRF (Cross-Site Request Forgery) protection dla **wszystkich** endpointów, w tym dla REST API. To powoduje błędy typu:

```
Invalid CSRF token found for http://localhost:8080/api/articles
```

## Rozwiązanie

CSRF protection został selektywnie skonfigurowany:

### ✅ CSRF włączone dla:
- `/login` - formularz logowania
- `/logout` - wylogowanie
- `/admin/**` - panel administracyjny (formularze Thymeleaf)

### ❌ CSRF wyłączone dla:
- `/api/**` - REST API endpoints

## Implementacja

Plik: `infrastructure/config/SecurityConfiguration.java`

```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers("/api/**")
)
```

## Dlaczego?

### REST API nie potrzebuje CSRF:
- **Stateless** - każdy request zawiera pełne credentials (session cookie)
- **Brak formularzy** - API nie używa HTML forms
- **Różne źródła** - API może być wywoływane z różnych źródeł (mobile apps, SPA)
- **Token w każdym requeście** - autentykacja przez session cookie w każdym zapytaniu

### Formularze webowe wymagają CSRF:
- **Stateful** - przeglądarki automatycznie wysyłają cookies
- **Podatne na ataki** - złośliwa strona może wykonać request w imieniu użytkownika
- **Protection** - CSRF token weryfikuje, że request pochodzi z legitnej strony

## Testowanie

### ✅ REST API bez CSRF:

**Request:**
```bash
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=xyz" \
  -d '{"title": "Test"}'
```

**Response:** ✅ Działa bez CSRF tokenu

### ✅ Formularze z CSRF:

**Login form (Thymeleaf):**
```html
<form th:action="@{/login}" method="post">
    <!-- CSRF token automatycznie dodawany przez Spring Security + Thymeleaf -->
    <input type="email" name="email" />
    <input type="password" name="password" />
    <button type="submit">Zaloguj</button>
</form>
```

**Response:** ✅ CSRF token automatycznie weryfikowany

## Best Practices

### ✅ DOBRZE:
- REST API (`/api/**`) - bez CSRF
- Formularze Thymeleaf (`/admin/**`, `/login`) - z CSRF
- Session-based auth dla obu

### ❌ ŹLE:
- Wyłączać CSRF globalnie: `.csrf(AbstractHttpConfigurer::disable)`
- Mieszać API i formularze w tej samej ścieżce
- Używać CSRF tokenów w REST API

## Security Considerations

### Co to zabezpiecza:
- ✅ Formularze webowe są bezpieczne przed CSRF
- ✅ API działa bez zbędnej konfiguracji
- ✅ Session-based auth działa dla obu

### Czego NIE zabezpiecza:
- ❌ XSS (Cross-Site Scripting) - wymagane inne mechanizmy
- ❌ SQL Injection - wymagane parametryzowane queries
- ❌ Brute force attacks - wymagane rate limiting

## Migracja do JWT (przyszłość)

Jeśli w przyszłości przejdziesz na JWT dla API:

1. **API:** JWT tokens w `Authorization` header (bez session)
2. **Admin panel:** Session + CSRF (jak teraz)
3. **Osobne konfiguracje** dla `/api/**` i `/admin/**`

Przykład:
```java
.securityMatcher("/api/**")
    .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    )
    .csrf(AbstractHttpConfigurer::disable)
```

## Referencje

- [OWASP CSRF Prevention](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [Spring Security CSRF](https://docs.spring.io/spring-security/reference/features/exploits/csrf.html)
- [REST API Security](https://restfulapi.net/security-essentials/)
