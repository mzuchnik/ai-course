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
5. AI zwraca tekst → edytor WYSIWYG (TinyMCE/Quill)
6. Edycja: formatowanie, dodanie obrazów, korekta prawnicza
7. Zapis jako Draft lub Publikacja

**Akceptacja:**
- Czas generowania <60s dla artykułu 2000 słów
- AI success rate >95%
- Limit 20 generowań/dzień (licznik w UI)
- WYSIWYG obsługuje: h2, h3, p, ul, li, strong, em, a
- Podgląd artykułu przed publikacją
- SEO: auto-generowanie meta description z pierwszych 160 znaków

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
4. Edycja w WYSIWYG → Zapisz
5. Publikacja natychmiastowa (cache invalidation)

**Akceptacja:**
- Upload obrazów: max 5MB, auto-kompresja do WebP
- Wersjonowanie treści (możliwość cofnięcia zmian)
- Wiadomości kontaktowe: oznaczanie jako przeczytane, archiwizacja

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