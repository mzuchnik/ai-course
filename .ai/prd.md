# PRD - MVP Strony Kancelarii Prawnej

## Kluczowe Decyzje Produktowe

1. **Stack technologiczny**: Spring Boot + Thymeleaf + MaterialUI + PostgreSQL, hostowana na VPS
2. **Architektura**: Monolityczna aplikacja z architekturą heksagonalną opartą na DDD
3. **AI Content Generation**: Workflow AI → weryfikacja prawnika → publikacja. API do wyboru: Anthropic Claude lub OpenAI
4. **Zakres MVP**: Strona informacyjna + blog + formularz kontaktowy + panel admin z generatorem treści artykułów przez AI
5. **Specjalizacja**: Prawo cywilne (niebieski) i karne (bordowy) - subtelne różnicowanie kolorystyczne
6. **Autentykacja**: Spring Security, form login, jedno konto administratora
7. **Security**: Whitelist HTML (p, h2, h3, ul, li, strong, em, a), reCAPTCHA v3, CSRF protection
8. **SEO**: Meta tags, Open Graph, sitemap.xml, robots.txt, Schema.org JSON-LD
9. **Backup**: pg_dump cron daily, 7 dni retencji
10. **Timeline**: 10-12 tygodni (part-time), CI/CD przez GitHub Actions
11. **Zawartość startowa**: Minimum 2 artykuły blogowe, 8-10 opisów usług

## Wymagania Funkcjonalne

### Frontend Publiczny
- **Strona główna**: Hero section, prezentacja prawnika, najnowsze artykuły, CTA
- **Usługi**: Podział Cywilne/Karne z szablonem: nazwa, opis, zakres, przebieg, FAQ, CTA
- **Blog**: Lista artykułów, pojedynczy artykuł z formatowaniem, social sharing
- **Formularz kontaktowy**: Imię, email, telefon (opt.), kategoria, wiadomość (min 50 znaków), reCAPTCHA v3
- **Stopka**: Dane kontaktowe, Google Maps embed, klikalny telefon

### Panel Administracyjny
- **Autentykacja**: Spring Security form login
- **Generator AI**: Temat, keywords, długość → generowanie (timeout 60s) → edytor WYSIWYG → draft/publikacja
- **Zarządzanie**: CRUD artykułów, upload obrazów z kompresją
- **Ograniczenia**: Max 20 generowań AI/dzień, limit 5000 słów/artykuł

### Backend & Infrastructure
- **Stack**: Spring Boot 3.2+, Thymeleaf, PostgreSQL 15+, Spring Data JPA
- **Architektura**: Heksagonalna (Domain → Application → Infrastructure/Adapters)
- **AI Integration**: Anthropic Claude lub OpenAI, prompt template dla języka prawniczego
- **Email**: Spring Mail SMTP, async powiadomienia
- **Security**: CSRF, XSS sanitization, BCrypt, HTTPS-only
- **Hosting**: VPS + Nginx + Let's Encrypt SSL
- **CI/CD**: GitHub Actions
- **Monitoring**: Google Analytics 4, UptimeRobot, Spring Boot Actuator

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

1. **Klient szuka pomocy prawnej**: Google → usługi → szczegóły → formularz kontaktowy
2. **Klient dzwoni z mobile**: Strona → klikalny telefon w stopce → bezpośredni kontakt
3. **Prawnik tworzy artykuł**: Login → generator AI → edycja WYSIWYG → draft → publikacja
4. **Czytelnik blogowy**: Google → artykuł → social sharing → CTA → formularz

## Kwestie Otwarte (Wymagają Decyzji)

### Przed Rozpoczęciem (Tydzień 0-1)
1. **AI API Provider**: Anthropic Claude vs OpenAI (rekomendacja: Claude 3.5 Sonnet)
2. **Nazwa domeny**: Wybór i rejestracja
3. **Branding**: Logo, kolory brandowe (hex codes), ui.md

### Przed Deploymentem (Tydzień 7-8)
4. **VPS Provider**: DigitalOcean/Linode/OVH, specyfikacja zasobów
5. **Email SMTP**: Gmail/SendGrid/własny, konfiguracja SPF/DKIM
6. **Google Services**: Analytics 4, Maps API key, reCAPTCHA keys
7. **Treść**: 2 artykuły początkowe, 8-10 opisów usług, bio prawnika

### Przed Produkcją (KRYTYCZNE - Tydzień 9-10)
8. **Dokumenty prawne**: Polityka Prywatności, klauzule RODO (BLOCKER)
9. **Backup storage**: Lokalizacja i strategia
10. **Beta testing**: Rekrutacja 5-10 testerów

---

**Status**: Gotowy do implementacji
**Timeline**: 10-12 tygodni part-time
**Ostatnia aktualizacja**: 2025-01-18