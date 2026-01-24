# Plan Implementacji: Strona Główna Kancelarii Prawnej Lexpage

## Podsumowanie Wykonawcze

Rozbudowa obecnej strony głównej (2 sekcje) do profesjonalnej, konwersyjnej strony z 9 sekcjami zgodnie z najlepszymi praktykami dla kancelarii prawnych. Wykorzystanie Material Tailwind, Thymeleaf SSR, oraz architektury heksagonalnej z DDD.

**Szacowany czas:** 10-12 godzin
**Złożoność:** Średnia
**Ryzyko:** Niskie (wykorzystanie istniejącej infrastruktury)

---

## Obecny Stan

**Istniejące sekcje (src/main/resources/templates/pages/index.html):**
1. Hero Section - gradient background, h1, 2 przyciski CTA
2. Services Section - 3 karty usług (Prawo cywilne, karne, gospodarcze)

**Dostępne komponenty (27 sztuk):**
- buttons.html, cards.html, accordion.html, typography.html, inputs.html, textarea.html, rating.html, avatar.html, chip.html, alerts.html, badge.html, list.html, navbar.html, footer.html, i inne

**Brakujące komponenty (do stworzenia):**
1. testimonial.html - opinie klientów z cytatami i ocenami
2. logo-bar.html - trust badges (logotypy partnerów/mediów)
3. team-member.html - karty wizytówki prawników

---

## Docelowa Struktura (9 Sekcji)

### 1. Hero Section (Enhanced) ✨
**Status:** Rozbudowa istniejącej sekcji
**Layout:** 2 kolumny (desktop), 1 kolumna (mobile)
**Zawartość:**
- Lewo: Eyebrow text ("15 lat doświadczenia"), H1 (value proposition), Paragraph (USP), 2 przyciski CTA
- Prawo: Zdjęcie prawnika (profesjonalne, 3:4 aspect ratio)

**Elementy:**
- Eyebrow: "Kancelaria prawna z 15-letnim doświadczeniem"
- H1: "Skutecznie bronimy Twoich praw w sprawach cywilnych i karnych"
- Paragraph: "Profesjonalna obsługa prawna z gwarancją sukcesu. 98% wygranych spraw."
- CTA 1: "Bezpłatna konsultacja" (filled, white)
- CTA 2: "Zadzwoń teraz: +48 22 123 45 67" (outlined, white, icon: phone)
- Obraz: `/images/hero-lawyer.jpg`

### 2. Social Proof / Logo Bar 🏆
**Status:** Nowa sekcja
**Komponent:** logo-bar.html (nowy)
**Zawartość:** 6 logotypów mediów/organizacji z grayscale hover effect

**Logotypy:**
- Rzeczpospolita, Gazeta Prawna, Izba Adwokacka, NRA, Forbes Polska, Money.pl

**Data struktura:** `List<LogoItemDto>(src, alt, url)`

### 3. Services (Enhanced) ⚖️
**Status:** Rozbudowa istniejącej (3→6 usług)
**Komponent:** cards.html (istniejący)
**Layout:** Grid 1-2-3 kolumny (responsive)

**6 usług:**
1. Prawo cywilne (primary) - rozwody, kontrakty, odszkodowania
2. Prawo karne (burgundy) - obrona karna, reprezentacja pokrzywdzonych
3. Prawo gospodarcze (primary) - doradztwo dla firm, prawo kontraktowe
4. Prawo rodzinne (primary) - rozwody, alimenty, kontakty z dziećmi
5. Prawo spadkowe (primary) - działy spadku, testamenty
6. Windykacja należności (burgundy) - windykacja polubowna i sądowa

**Wizualne różnicowanie:** `border-l-4 border-primary-600` (civil) vs `border-burgundy-600` (criminal)

### 4. Value Proposition ("Dlaczego my?") 💎
**Status:** Nowa sekcja
**Layout:** Grid 1-2-4 (responsive), gradient background
**Zawartość:** 4 boksy z ikonami

**4 przewagi:**
1. Icon: verified → "15 lat doświadczenia" → "Ponad 1000 wygranych spraw"
2. Icon: payments → "Rozliczenie success fee" → "Płacisz tylko za sukces"
3. Icon: support_agent → "Osobiste podejście" → "Każdy klient jest najważniejszy"
4. Icon: schedule → "Dostępność 24/7" → "Kontakt w nagłych sprawach"

**Struktura boksu:** Okrągła ikona (bg-primary-100) + h3 + p

### 5. Process ("Jak wygląda współpraca?") 📋
**Status:** Nowa sekcja
**Layout:** Horizontal stepper (desktop), vertical timeline (mobile)
**Zawartość:** 3 kroki procesu

**3 kroki:**
1. Icon: calendar_today → "Bezpłatna konsultacja" → "Poznajemy sprawę, oceniamy szanse"
2. Icon: strategy → "Plan działania" → "Opracowujemy strategię, przedstawiamy ofertę"
3. Icon: verified_user → "Realizacja i sukces" → "Prowadzimy sprawę do końca"

**Desktop:** Horizontal z strzałkami między krokami
**Mobile:** Vertical timeline z łączącą linią

### 6. Team ("Nasz zespół") 👥
**Status:** Nowa sekcja
**Komponent:** team-member.html (nowy)
**Layout:** Grid 1-2-3 kolumny

**3 prawników:**
1. Dr Anna Kowalska - Radca prawny, Partner zarządzający - Prawo cywilne/rodzinne/spadkowe
2. Mec. Piotr Nowak - Adwokat, Specjalista prawa karnego - 200+ wygranych spraw karnych
3. Mec. Katarzyna Wiśniewska - Radca prawny - Prawo gospodarcze/kontraktowe/windykacja

**Każda karta:** Photo (3:4), Name, Role, Bio (2-3 zdania), Specializations (chips)

### 7. Testimonials ("Opinie klientów") ⭐
**Status:** Nowa sekcja
**Komponent:** testimonial.html (nowy)
**Layout:** Grid 1-2-3 kolumny

**6 opinii:**
- Inicjały (A.K., M.Z., P.W., J.S., E.M., T.L.)
- Role (Klient - Sprawa rozwodowa, Klient - Sprawa karna, etc.)
- Cytaty (2-3 zdania o profesjonalizmie)
- Rating: 4.5-5.0 gwiazdek

**Struktura karty:** Quote icon, Cytat, Rating stars, Avatar + Autor info

### 8. FAQ ("Najczęściej zadawane pytania") ❓
**Status:** Nowa sekcja
**Komponent:** accordion.html (istniejący)
**Zawartość:** 8 pytań

**Pytania:**
1. Ile kosztuje konsultacja? → Pierwsza 30 min gratis
2. Jak wygląda success fee? → Tylko za sukces, % indywidualny
3. Czy poza Warszawą? → Tak, cała Polska + online
4. Jak długo trwa sprawa? → Karne 6-18m, cywilne 12-24m
5. Kontakt po godzinach? → 24/7 dla pilnych karnych, 8-20 dla cywilnych
6. Jakie dokumenty? → Wszystkie związane, pomożemy odtworzyć
7. Porady online? → Tak, Zoom/Meet/Teams
8. Przerwanie współpracy? → Tak, rozliczenie za wykonaną pracę

### 9. Final CTA + Contact Form 📞
**Status:** Nowa sekcja
**Layout:** 2 kolumny (desktop) - CTA info + formularz

**Lewa kolumna (gradient background, white text):**
- H2: "Potrzebujesz pomocy prawnej?"
- Paragraph: "Skontaktuj się z nami. Pierwsza konsultacja bezpłatna."
- 4 info karty (white/10 backdrop-blur):
    - Phone: +48 22 123 45 67
    - Email: kontakt@lexpage.pl
    - Address: ul. Marszałkowska 123, Warszawa
    - Hours: Pon-Pt 8-20, Sob 10-14

**Prawa kolumna (white card):**
- Formularz kontaktowy (reuse z contact.html)
- Fields: Imię, Nazwisko, Email, Telefon, Kategoria (select), Wiadomość (textarea)
- Submit button

---

## Implementacja Krok po Kroku

### FAZA 1: Stworzenie DTOs (30 min)

**Plik:** `src/main/java/pl/klastbit/lexpage/infrastructure/web/dto/homepage/HomepageDtos.java`

```java
package pl.klastbit.lexpage.infrastructure.web.dto.homepage;

import java.util.List;

// 1. Lawyer profile for team section
public record LawyerProfileDto(
    String name,
    String role,
    String photo,
    String bio,
    List<String> specializations
) {}

// 2. Client testimonial
public record TestimonialDto(
    String quote,
    String author,
    String role,
    double rating
) {}

// 3. FAQ item
public record FaqItemDto(
    String title,
    String content  // HTML allowed
) {}

// 4. Service tile
public record ServiceTileDto(
    String title,
    String description,
    String icon,  // Material icon name
    String category,  // "civil" or "criminal"
    List<String> examples
) {}

// 5. Process step
public record ProcessStepDto(
    String icon,
    String title,
    String description
) {}

// 6. Logo item for trust bar
public record LogoItemDto(
    String src,
    String alt,
    String url
) {}

// 7. Value proposition box
public record ValuePropositionDto(
    String icon,
    String title,
    String description
) {}
```

**Weryfikacja:** Kompilacja bez błędów

---

### FAZA 2: Stworzenie Nowych Komponentów (2-3h)

#### 2.1 Testimonial Component (45 min)

**Plik:** `src/main/resources/templates/fragments/components/testimonial.html`

**Główny fragment:**
```html
<div th:fragment="testimonial(quote, author, role, rating, variant, customClass)">
  <!-- Quote icon -->
  <i class="material-icons text-4xl text-primary-200 mb-2">format_quote</i>

  <!-- Quote text -->
  <p class="text-gray-700 italic mb-4">"[[${quote}]]"</p>

  <!-- Rating (reuse rating.html) -->
  <div class="mb-4">
    <div th:replace="~{fragments/components/rating :: rating(${rating}, 5, true, null, 'sm', 'yellow', false, 0.5, '')}"></div>
  </div>

  <!-- Author info -->
  <div class="flex items-center gap-3">
    <!-- Avatar with initials -->
    <div th:replace="~{fragments/components/avatar :: avatar(null, ${author}, ${author}, 'md', 'circular', false, '')}"></div>

    <div>
      <p class="font-semibold text-gray-900 text-sm">[[${author}]]</p>
      <p class="text-xs text-gray-600">[[${role}]]</p>
    </div>
  </div>
</div>
```

**Convenience fragment:**
```html
<th:block th:fragment="testimonialCard(quote, author, role, rating)"
          th:replace="~{fragments/components/testimonial :: testimonial(${quote}, ${author}, ${role}, ${rating}, 'card', '')}">
</th:block>
```

**Styling:** White card, rounded-xl, shadow-md, p-6

**KRYTYCZNE:** Użyj `<th:block th:replace>` dla convenience fragments!

---

#### 2.2 Logo Bar Component (30 min)

**Plik:** `src/main/resources/templates/fragments/components/logo-bar.html`

```html
<div th:fragment="logoBar(logos, title, grayscale, customClass)"
     th:class="'py-12 ' + ${customClass ?: ''}">

  <!-- Optional title -->
  <h3 th:if="${title}"
      class="text-center text-gray-600 text-sm uppercase tracking-wider mb-8">
    [[${title}]]
  </h3>

  <!-- Logo grid: 2-4-6 columns -->
  <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-8 items-center justify-items-center">
    <div th:each="logo : ${logos}">
      <a th:href="${logo.url}"
         target="_blank"
         class="opacity-60 hover:opacity-100 transition-opacity duration-300">
        <img th:src="${logo.src}"
             th:alt="${logo.alt}"
             th:class="'h-12 w-auto object-contain ' + ${grayscale ? 'grayscale hover:grayscale-0 transition-all duration-300' : ''}"
             loading="lazy">
      </a>
    </div>
  </div>
</div>
```

**Convenience fragment:**
```html
<th:block th:fragment="simple(logos, title)"
          th:replace="~{fragments/components/logo-bar :: logoBar(${logos}, ${title}, true, '')}">
</th:block>
```

---

#### 2.3 Team Member Component (60 min)

**Plik:** `src/main/resources/templates/fragments/components/team-member.html`

```html
<div th:fragment="teamMember(name, role, photo, bio, specializations, variant, customClass)"
     th:class="'bg-white rounded-xl shadow-md overflow-hidden ' + ${customClass ?: ''}">

  <!-- Photo (3:4 aspect ratio) -->
  <div class="aspect-[3/4] overflow-hidden bg-gray-200">
    <img th:src="${photo}"
         th:alt="${name}"
         class="w-full h-full object-cover hover:scale-105 transition-transform duration-300">
  </div>

  <!-- Content -->
  <div class="p-6">
    <h3 class="text-xl font-bold text-gray-900 mb-1">[[${name}]]</h3>
    <p class="text-primary-600 font-medium mb-3">[[${role}]]</p>
    <p class="text-gray-700 text-sm mb-4 leading-relaxed">[[${bio}]]</p>

    <!-- Specializations as chips -->
    <div class="flex flex-wrap gap-2">
      <th:block th:each="spec : ${specializations}">
        <th:block th:replace="~{fragments/components/chip :: simple(${spec})}"></th:block>
      </th:block>
    </div>
  </div>
</div>
```

**Convenience fragment:**
```html
<th:block th:fragment="teamCard(name, role, photo, bio, specializations)"
          th:replace="~{fragments/components/team-member :: teamMember(${name}, ${role}, ${photo}, ${bio}, ${specializations}, 'card', '')}">
</th:block>
```

**KRYTYCZNE:** `<th:block>` dla nested `th:replace`!

**Weryfikacja:** Uruchom `./gradlew tailwindWatch`, sprawdź czy komponenty renderują się poprawnie w izolacji

---

### FAZA 3: Aktualizacja PageController (1h)

**Plik:** `src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/PageController.java`

**Import DTOs:**
```java
import pl.klastbit.lexpage.infrastructure.web.dto.homepage.*;
import java.util.List;
```

**Rozbudowa metody `index()`:**

```java
@GetMapping("/")
public String index(Model model) {
    // SEO metadata
    model.addAttribute("pageTitle", "Kancelaria Prawna Lexpage - Skuteczna Pomoc Prawna w Warszawie");
    model.addAttribute("pageDescription", "Profesjonalna kancelaria prawna z 15-letnim doświadczeniem. Prawo cywilne, karne, gospodarcze. Bezpłatna konsultacja.");

    // === SECTION 1: Hero ===
    model.addAttribute("heroEyebrow", "Kancelaria prawna z 15-letnim doświadczeniem");
    model.addAttribute("heroTitle", "Skutecznie bronimy Twoich praw w sprawach cywilnych i karnych");
    model.addAttribute("heroSubtitle", "Profesjonalna obsługa prawna z gwarancją sukcesu. 98% wygranych spraw.");
    model.addAttribute("heroImage", "/images/hero-lawyer.jpg");

    // === SECTION 2: Trust Logos ===
    List<LogoItemDto> trustLogos = List.of(
        new LogoItemDto("/images/logos/rzeczpospolita.svg", "Rzeczpospolita", "#"),
        new LogoItemDto("/images/logos/gazeta-prawna.svg", "Gazeta Prawna", "#"),
        new LogoItemDto("/images/logos/izba-adwokacka.svg", "Izba Adwokacka", "#"),
        new LogoItemDto("/images/logos/nra.svg", "Naczelna Rada Adwokacka", "#"),
        new LogoItemDto("/images/logos/forbes.svg", "Forbes Polska", "#"),
        new LogoItemDto("/images/logos/money.svg", "Money.pl", "#")
    );
    model.addAttribute("trustLogos", trustLogos);

    // === SECTION 3: Services (6 items) ===
    List<ServiceTileDto> services = List.of(
        new ServiceTileDto("Prawo cywilne", "Kompleksowa obsługa spraw cywilnych", "gavel", "civil",
            List.of("Sprawy rozwodowe", "Sprawy kontraktowe", "Odszkodowania")),
        new ServiceTileDto("Prawo karne", "Profesjonalna obrona w postępowaniach karnych", "policy", "criminal",
            List.of("Obrona w sprawach karnych", "Reprezentacja pokrzywdzonych", "Sprawy gospodarcze")),
        new ServiceTileDto("Prawo gospodarcze", "Wsparcie prawne dla firm i przedsiębiorców", "business_center", "civil",
            List.of("Doradztwo dla firm", "Prawo kontraktowe", "Restrukturyzacje")),
        new ServiceTileDto("Prawo rodzinne", "Empatyczna pomoc w sprawach rodzinnych", "family_restroom", "civil",
            List.of("Rozwody", "Alimenty", "Kontakty z dziećmi")),
        new ServiceTileDto("Prawo spadkowe", "Obsługa spraw spadkowych od A do Z", "account_balance", "civil",
            List.of("Działy spadku", "Testamenty", "Stwierdzenie nabycia spadku")),
        new ServiceTileDto("Windykacja należności", "Skuteczne odzyskiwanie długów", "request_quote", "criminal",
            List.of("Windykacja polubowna", "Windykacja sądowa", "Success fee"))
    );
    model.addAttribute("services", services);

    // === SECTION 4: Value Propositions (4 items) ===
    List<ValuePropositionDto> valueProps = List.of(
        new ValuePropositionDto("verified", "15 lat doświadczenia", "Ponad 1000 wygranych spraw w całej Polsce"),
        new ValuePropositionDto("payments", "Rozliczenie success fee", "Płacisz tylko za sukces - bez ryzyka"),
        new ValuePropositionDto("support_agent", "Osobiste podejście", "Każdy klient jest dla nas najważniejszy"),
        new ValuePropositionDto("schedule", "Dostępność 24/7", "Kontakt w nagłych sprawach karnych")
    );
    model.addAttribute("valueProps", valueProps);

    // === SECTION 5: Process Steps (3 steps) ===
    List<ProcessStepDto> processSteps = List.of(
        new ProcessStepDto("calendar_today", "Bezpłatna konsultacja", "Poznajemy sprawę i oceniamy szanse powodzenia"),
        new ProcessStepDto("strategy", "Plan działania", "Opracowujemy strategię i przedstawiamy ofertę"),
        new ProcessStepDto("verified_user", "Realizacja i sukces", "Prowadzimy sprawę do końca")
    );
    model.addAttribute("processSteps", processSteps);

    // === SECTION 6: Team Members (3 lawyers) ===
    List<LawyerProfileDto> teamMembers = List.of(
        new LawyerProfileDto("Dr Anna Kowalska", "Radca prawny, Partner zarządzający",
            "/images/team/anna-kowalska.jpg",
            "15 lat doświadczenia w prawie cywilnym i rodzinnym. Absolwentka UW, doktor nauk prawnych.",
            List.of("Prawo cywilne", "Prawo rodzinne", "Sprawy spadkowe")),
        new LawyerProfileDto("Mec. Piotr Nowak", "Adwokat, Specjalista prawa karnego",
            "/images/team/piotr-nowak.jpg",
            "Ponad 200 wygranych spraw karnych. Członek Izby Adwokackiej w Warszawie.",
            List.of("Prawo karne", "Sprawy gospodarcze", "Postępowania wykroczeniowe")),
        new LawyerProfileDto("Mec. Katarzyna Wiśniewska", "Radca prawny",
            "/images/team/katarzyna-wisniewska.jpg",
            "Specjalizacja w prawie gospodarczym i kontraktowym. MBA w zarządzaniu.",
            List.of("Prawo gospodarcze", "Prawo kontraktowe", "Windykacja"))
    );
    model.addAttribute("teamMembers", teamMembers);

    // === SECTION 7: Testimonials (6 reviews) ===
    List<TestimonialDto> testimonials = List.of(
        new TestimonialDto("Profesjonalna obsługa i pełne zaangażowanie. Dzięki Pani Kowalskiej wygrałem sprawę rozwodową w rekordowym czasie.",
            "A.K.", "Klient - Sprawa rozwodowa", 5.0),
        new TestimonialDto("Pan Nowak obronił mnie w trudnej sprawie karnej. Czułem się bezpiecznie i dobrze poinformowany.",
            "M.Z.", "Klient - Sprawa karna", 5.0),
        new TestimonialDto("Kancelaria pomogła mi odzyskać należność za kontrakt. Success fee to uczciwe rozwiązanie.",
            "P.W.", "Przedsiębiorca - Windykacja", 4.5),
        new TestimonialDto("Pani Wiśniewska pomogła w sporządzeniu umowy spółki. Wszystko wyjaśniła zrozumiałym językiem.",
            "J.S.", "Klient - Prawo gospodarcze", 5.0),
        new TestimonialDto("Sprawa spadkowa załatwiona sprawnie i bez komplikacji. Profesjonalizm i cierpliwość.",
            "E.M.", "Klient - Sprawa spadkowa", 4.5),
        new TestimonialDto("Konsultacja telefoniczna wyjaśniła wszystkie wątpliwości. Bardzo kompetentna obsługa.",
            "T.L.", "Klient - Konsultacja", 5.0)
    );
    model.addAttribute("testimonials", testimonials);

    // === SECTION 8: FAQ (8 items) ===
    List<FaqItemDto> faqItems = List.of(
        new FaqItemDto("Ile kosztuje konsultacja?",
            "<p>Pierwsza konsultacja (do 30 minut) jest <strong>całkowicie bezpłatna</strong>.</p>"),
        new FaqItemDto("Jak wygląda rozliczenie success fee?",
            "<p>W wybranych sprawach oferujemy model success fee - płacisz tylko jeśli wygramy sprawę.</p>"),
        new FaqItemDto("Czy prowadzicie sprawy poza Warszawą?",
            "<p>Tak, obsługujemy klientów w <strong>całej Polsce</strong>. Konsultacje możemy przeprowadzić online.</p>"),
        new FaqItemDto("Jak długo trwa typowa sprawa sądowa?",
            "<p>Sprawy karne: 6-18 miesięcy, sprawy cywilne: 12-24 miesiące. Na konsultacji przedstawimy timeline.</p>"),
        new FaqItemDto("Czy mogę się skontaktować po godzinach?",
            "<p>Tak! Oferujemy wsparcie <strong>24/7 dla pilnych spraw karnych</strong>.</p>"),
        new FaqItemDto("Jakie dokumenty przygotować?",
            "<p>Wszystkie dokumenty związane ze sprawą. Jeśli nie masz - pomożemy je uzyskać.</p>"),
        new FaqItemDto("Czy udzielają Państwo porad online?",
            "<p>Tak, prowadzimy konsultacje przez Zoom, Google Meet, Teams.</p>"),
        new FaqItemDto("Czy mogę przerwać współpracę?",
            "<p>Tak, możesz wypowiedzieć pełnomocnictwo. Rozliczymy się za wykonaną pracę.</p>")
    );
    model.addAttribute("faqItems", faqItems);

    return "pages/index";
}
```

**Weryfikacja:** Kompilacja, uruchomienie aplikacji bez błędów

---

### FAZA 4: Aktualizacja Template index.html (3-4h)

**Plik:** `src/main/resources/templates/pages/index.html`

**Struktura:** Zastąp istniejący content fragment 9 sekcjami

#### Section 1: Enhanced Hero (update existing)

```html
<section class="bg-gradient-to-br from-primary-600 to-primary-400 py-20">
  <div class="container mx-auto px-4">
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
      <!-- Left: Text content -->
      <div class="text-white">
        <p class="text-sm uppercase tracking-wider mb-2 text-white/80" th:text="${heroEyebrow}">Eyebrow</p>
        <h1 class="text-4xl md:text-5xl font-bold mb-4" th:text="${heroTitle}">Hero Title</h1>
        <p class="text-xl text-white/90 mb-8" th:text="${heroSubtitle}">Subtitle</p>

        <div class="flex flex-col sm:flex-row gap-4">
          <th:block th:replace="~{fragments/components/buttons :: button('Bezpłatna konsultacja', 'button', 'filled', 'white', 'lg', false, false, 'calendar_today', 'left', '', null)}"></th:block>
          <th:block th:replace="~{fragments/components/buttons :: button('Zadzwoń: +48 22 123 45 67', 'button', 'outlined', 'white', 'lg', false, false, 'phone', 'left', '', 'tel:+48221234567')}"></th:block>
        </div>
      </div>

      <!-- Right: Lawyer photo -->
      <div class="hidden lg:block">
        <img th:src="${heroImage}"
             alt="Profesjonalny prawnik"
             class="rounded-2xl shadow-2xl w-full h-auto aspect-[3/4] object-cover">
      </div>
    </div>
  </div>
</section>
```

#### Section 2: Logo Bar

```html
<section class="bg-gray-50 py-16">
  <div class="container mx-auto px-4">
    <div th:replace="~{fragments/components/logo-bar :: simple(${trustLogos}, 'Zaufali nam:')}"></div>
  </div>
</section>
```

#### Section 3: Services (update existing)

```html
<section class="bg-white py-20">
  <div class="container mx-auto px-4">
    <h2 class="text-3xl font-bold text-center mb-4">Zakres usług</h2>
    <p class="text-center text-gray-600 mb-12 max-w-2xl mx-auto">
      Kompleksowa obsługa prawna w kluczowych dziedzinach prawa
    </p>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div th:each="service : ${services}"
           th:replace="~{fragments/components/cards :: card(
             ${service.title},
             ${service.description},
             ~{::service-content},
             ~{::service-actions},
             ${service.category == 'civil' ? 'border-l-4 border-primary-600' : 'border-l-4 border-burgundy-600'}
           )}">

        <div th:fragment="service-content">
          <ul class="space-y-1">
            <li th:each="example : ${service.examples}" class="text-gray-700 text-sm flex items-start gap-2">
              <i class="material-icons text-base text-primary-600">check_circle</i>
              <span th:text="${example}">Example</span>
            </li>
          </ul>
        </div>

        <div th:fragment="service-actions">
          <th:block th:replace="~{fragments/components/buttons :: button('Więcej', 'button', 'text', ${service.category == 'civil' ? 'primary' : 'burgundy'}, 'sm', false, false, 'arrow_forward', 'right', '', null)}"></th:block>
        </div>
      </div>
    </div>
  </div>
</section>
```

#### Section 4: Value Proposition

```html
<section class="bg-gradient-to-br from-gray-50 to-white py-20">
  <div class="container mx-auto px-4">
    <h2 class="text-3xl font-bold text-center mb-4">Dlaczego warto nam zaufać?</h2>
    <p class="text-center text-gray-600 mb-12 max-w-2xl mx-auto">
      Profesjonalizm, doświadczenie i zaangażowanie na najwyższym poziomie
    </p>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
      <div th:each="prop : ${valueProps}" class="text-center p-6">
        <div class="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <i class="material-icons text-3xl text-primary-600" th:text="${prop.icon}">icon</i>
        </div>
        <h3 class="text-xl font-bold mb-2" th:text="${prop.title}">Title</h3>
        <p class="text-gray-600" th:text="${prop.description}">Description</p>
      </div>
    </div>
  </div>
</section>
```

#### Section 5: Process

```html
<section class="bg-white py-20">
  <div class="container mx-auto px-4">
    <h2 class="text-3xl font-bold text-center mb-4">Jak wygląda współpraca?</h2>
    <p class="text-center text-gray-600 mb-12">Proste 3 kroki do rozwiązania sprawy</p>

    <!-- Desktop: horizontal -->
    <div class="hidden md:flex justify-between items-start gap-8 max-w-4xl mx-auto relative">
      <div th:each="step, iterStat : ${processSteps}" class="flex-1 relative">
        <div class="flex flex-col items-center">
          <div class="w-12 h-12 bg-primary-600 text-white rounded-full flex items-center justify-center mb-4 text-xl font-bold z-10">
            [[${iterStat.index + 1}]]
          </div>
          <i class="material-icons text-4xl text-primary-600 mb-2" th:text="${step.icon}">icon</i>
          <h3 class="font-bold text-lg text-center mb-2" th:text="${step.title}">Title</h3>
          <p class="text-gray-600 text-sm text-center" th:text="${step.description}">Description</p>
        </div>

        <!-- Arrow between steps -->
        <div th:if="${!iterStat.last}"
             class="absolute top-6 left-full w-8 flex items-center justify-center -ml-4 z-0">
          <i class="material-icons text-gray-300 text-3xl">arrow_forward</i>
        </div>
      </div>
    </div>

    <!-- Mobile: vertical timeline -->
    <div class="md:hidden space-y-6 max-w-md mx-auto">
      <div th:each="step, iterStat : ${processSteps}" class="flex gap-4">
        <div class="flex flex-col items-center">
          <div class="w-10 h-10 bg-primary-600 text-white rounded-full flex items-center justify-center font-bold text-sm">
            [[${iterStat.index + 1}]]
          </div>
          <div th:if="${!iterStat.last}" class="w-0.5 flex-1 bg-gray-300 mt-2 min-h-[60px]"></div>
        </div>

        <div class="flex-1 pb-6">
          <i class="material-icons text-3xl text-primary-600 mb-2" th:text="${step.icon}">icon</i>
          <h3 class="font-bold text-lg mb-1" th:text="${step.title}">Title</h3>
          <p class="text-gray-600 text-sm" th:text="${step.description}">Description</p>
        </div>
      </div>
    </div>
  </div>
</section>
```

#### Section 6: Team

```html
<section class="bg-gray-50 py-20">
  <div class="container mx-auto px-4">
    <h2 class="text-3xl font-bold text-center mb-4">Nasz zespół</h2>
    <p class="text-center text-gray-600 mb-12 max-w-2xl mx-auto">
      Doświadczeni prawnicy z empatią i profesjonalizmem
    </p>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
      <div th:each="lawyer : ${teamMembers}">
        <div th:replace="~{fragments/components/team-member :: teamCard(
          ${lawyer.name},
          ${lawyer.role},
          ${lawyer.photo},
          ${lawyer.bio},
          ${lawyer.specializations}
        )}"></div>
      </div>
    </div>
  </div>
</section>
```

#### Section 7: Testimonials

```html
<section class="bg-white py-20">
  <div class="container mx-auto px-4">
    <h2 class="text-3xl font-bold text-center mb-4">Co mówią nasi klienci?</h2>
    <p class="text-center text-gray-600 mb-12">Prawdziwe opinie od prawdziwych klientów</p>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div th:each="testimonial : ${testimonials}">
        <div th:replace="~{fragments/components/testimonial :: testimonialCard(
          ${testimonial.quote},
          ${testimonial.author},
          ${testimonial.role},
          ${testimonial.rating}
        )}"></div>
      </div>
    </div>
  </div>
</section>
```

#### Section 8: FAQ

```html
<section class="bg-gray-50 py-20">
  <div class="container mx-auto px-4">
    <div class="max-w-3xl mx-auto">
      <h2 class="text-3xl font-bold text-center mb-4">Najczęściej zadawane pytania</h2>
      <p class="text-center text-gray-600 mb-12">Znajdź odpowiedzi na popularne pytania</p>

      <div th:replace="~{fragments/components/accordion :: accordion('faq-accordion', ${faqItems}, false, '')}"></div>
    </div>
  </div>
</section>
```

#### Section 9: Final CTA + Contact

```html
<section class="bg-gradient-to-br from-primary-600 to-primary-400 py-20">
  <div class="container mx-auto px-4">
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-12">
      <!-- Left: CTA & Contact Info -->
      <div class="text-white">
        <h2 class="text-3xl md:text-4xl font-bold mb-4">Potrzebujesz pomocy prawnej?</h2>
        <p class="text-white/90 text-lg mb-8">
          Skontaktuj się z nami już dziś. Pierwsza konsultacja jest bezpłatna.
        </p>

        <div class="space-y-4">
          <div class="flex items-center gap-4 bg-white/10 backdrop-blur-sm rounded-lg p-4">
            <i class="material-icons text-3xl">phone</i>
            <div>
              <p class="text-sm text-white/70">Telefon</p>
              <a href="tel:+48221234567" class="font-semibold hover:underline">+48 22 123 45 67</a>
            </div>
          </div>

          <div class="flex items-center gap-4 bg-white/10 backdrop-blur-sm rounded-lg p-4">
            <i class="material-icons text-3xl">email</i>
            <div>
              <p class="text-sm text-white/70">Email</p>
              <a href="mailto:kontakt@lexpage.pl" class="font-semibold hover:underline">kontakt@lexpage.pl</a>
            </div>
          </div>

          <div class="flex items-center gap-4 bg-white/10 backdrop-blur-sm rounded-lg p-4">
            <i class="material-icons text-3xl">location_on</i>
            <div>
              <p class="text-sm text-white/70">Adres</p>
              <p class="font-semibold">ul. Marszałkowska 123, 00-001 Warszawa</p>
            </div>
          </div>

          <div class="flex items-center gap-4 bg-white/10 backdrop-blur-sm rounded-lg p-4">
            <i class="material-icons text-3xl">schedule</i>
            <div>
              <p class="text-sm text-white/70">Godziny otwarcia</p>
              <p class="font-semibold">Pon-Pt 8:00-20:00, Sob 10:00-14:00</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Contact Form Card -->
      <div class="bg-white rounded-xl shadow-2xl p-8">
        <h3 class="text-2xl font-bold text-gray-900 mb-6">Wyślij wiadomość</h3>

        <form id="homepage-contact-form" class="space-y-4">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div th:replace="~{fragments/components/inputs :: input('firstName', 'firstName', 'Imię', 'text', null, null, true, false, null, 'person')}"></div>
            <div th:replace="~{fragments/components/inputs :: input('lastName', 'lastName', 'Nazwisko', 'text', null, null, true, false, null, 'person')}"></div>
          </div>

          <div th:replace="~{fragments/components/inputs :: email('email', 'email', 'Email', null, true, null)}"></div>
          <div th:replace="~{fragments/components/inputs :: phone('phone', 'phone', 'Telefon', null, false, null)}"></div>

          <div th:replace="~{fragments/components/select :: select('category', 'category', 'Kategoria sprawy',
            ${ {'civil': 'Sprawo cywilna', 'criminal': 'Sprawa karna', 'business': 'Prawo gospodarcze', 'other': 'Inna'} },
            'Wybierz kategorię', true, false, 'md', null, null, null, '')}"></div>

          <div th:replace="~{fragments/components/textarea :: textarea('message', 'message', 'Wiadomość', 'Opisz swoją sprawę...', null, 4, true, false, false, null, 'vertical', null, 'Minimum 50 znaków', false, '')}"></div>

          <div class="pt-4">
            <th:block th:replace="~{fragments/components/buttons :: button('Wyślij wiadomość', 'submit', 'filled', 'primary', 'lg', true, false, 'send', 'right', '', null)}"></th:block>
          </div>
        </form>
      </div>
    </div>
  </div>
</section>
```

**KRYTYCZNE dla Sections 3 & 6-9:** Zawsze `<th:block th:replace>` dla nested fragments!

**Weryfikacja:** Sprawdź w przeglądarce, czy wszystkie sekcje się renderują

---

### FAZA 5: Schema.org JSON-LD (15 min)

**Lokalizacja:** Na końcu `index.html`, wewnątrz fragmentu `content` (przed zamknięciem `</div>`)

```html
<!-- SEO: Schema.org Structured Data -->
<script type="application/ld+json" th:inline="javascript">
{
  "@context": "https://schema.org",
  "@type": "LegalService",
  "name": "Kancelaria Prawna Lexpage",
  "image": "/images/hero-lawyer.jpg",
  "description": "Profesjonalna kancelaria prawna z 15-letnim doświadczeniem. Prawo cywilne, karne, gospodarcze.",
  "address": {
    "@type": "PostalAddress",
    "streetAddress": "ul. Marszałkowska 123",
    "addressLocality": "Warszawa",
    "postalCode": "00-001",
    "addressCountry": "PL"
  },
  "telephone": "+48221234567",
  "email": "kontakt@lexpage.pl",
  "url": "https://lexpage.pl",
  "openingHoursSpecification": [
    {
      "@type": "OpeningHoursSpecification",
      "dayOfWeek": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
      "opens": "08:00",
      "closes": "20:00"
    },
    {
      "@type": "OpeningHoursSpecification",
      "dayOfWeek": "Saturday",
      "opens": "10:00",
      "closes": "14:00"
    }
  ],
  "priceRange": "$$",
  "areaServed": {
    "@type": "Country",
    "name": "Poland"
  },
  "aggregateRating": {
    "@type": "AggregateRating",
    "ratingValue": "4.9",
    "reviewCount": "127"
  }
}
</script>
```

**Weryfikacja:** https://validator.schema.org - wklej URL lub HTML

---

### FAZA 6: Obrazy i Assety (1h)

**Katalog:** `src/main/resources/static/images/`

#### Placeholder Images (do szybkiego prototypowania)

```html
<!-- Hero lawyer -->
https://placehold.co/1200x1600/0ea5e9/ffffff?text=Lawyer

<!-- Team members -->
https://ui-avatars.com/api/?name=Anna+Kowalska&size=600&background=0ea5e9&color=fff&bold=true
https://ui-avatars.com/api/?name=Piotr+Nowak&size=600&background=e0426a&color=fff&bold=true
https://ui-avatars.com/api/?name=Katarzyna+Wisniewska&size=600&background=0ea5e9&color=fff&bold=true

<!-- Logos -->
https://placehold.co/200x60/cccccc/666666?text=Logo1
https://placehold.co/200x60/cccccc/666666?text=Logo2
... (6 total)
```

#### Struktura katalogów obrazów

```
static/images/
├── hero-lawyer.jpg              # 1200x1600px, <300KB, WebP format
├── team/
│   ├── anna-kowalska.jpg        # 600x800px, <150KB
│   ├── piotr-nowak.jpg          # 600x800px, <150KB
│   └── katarzyna-wisniewska.jpg # 600x800px, <150KB
└── logos/
    ├── rzeczpospolita.svg
    ├── gazeta-prawna.svg
    ├── izba-adwokacka.svg
    ├── nra.svg
    ├── forbes.svg
    └── money.svg
```

**Optymalizacja:**
- Format: WebP dla zdjęć, SVG dla logotypów
- Rozmiar: Hero <300KB, Team photos <150KB każde
- Lazy loading: `loading="lazy"` dla obrazów poniżej fold

**Weryfikacja:** Wszystkie obrazy ładują się bez 404

---

### FAZA 7: Responsive Testing (1h)

**Narzędzia:** Chrome DevTools > Device Toolbar

**Test Cases:**

1. **Mobile (375px - iPhone SE)**
    - [ ] Hero: 1 kolumna, zdjęcie ukryte lub poniżej
    - [ ] Przyciski: full-width lub stacked
    - [ ] Services: 1 kolumna grid
    - [ ] Value props: 1 kolumna
    - [ ] Process: vertical timeline
    - [ ] Team: 1 kolumna
    - [ ] Testimonials: 1 kolumna
    - [ ] FAQ: accordion działa
    - [ ] Contact form: 1 kolumna

2. **Tablet (768px - iPad)**
    - [ ] Hero: 1-2 kolumny
    - [ ] Services: 2 kolumny grid
    - [ ] Value props: 2 kolumny
    - [ ] Process: horizontal stepper
    - [ ] Team: 2 kolumny
    - [ ] Testimonials: 2 kolumny
    - [ ] Contact: 2 kolumny (CTA + form)

3. **Desktop (1440px)**
    - [ ] Hero: 2 kolumny, zdjęcie widoczne
    - [ ] Services: 3 kolumny
    - [ ] Value props: 4 kolumny
    - [ ] Process: horizontal z strzałkami
    - [ ] Team: 3 kolumny
    - [ ] Testimonials: 3 kolumny
    - [ ] All text readable, spacing comfortable

**Weryfikacja:** Wszystkie sekcje responsive bez horizontal scroll

---

### FAZA 8: Accessibility & Performance (1h)

#### Accessibility Checklist

**Tools:**
- WAVE: https://wave.webaim.org
- aXe DevTools extension
- Lighthouse (Chrome DevTools)

**Checks:**
- [ ] All images have `alt` text
- [ ] Heading hierarchy: h1 → h2 (no h3 without h2)
- [ ] Color contrast ≥ 4.5:1 (text on background)
- [ ] Keyboard navigation: Tab through all buttons/links
- [ ] ARIA labels on icon-only buttons
- [ ] Form labels properly associated
- [ ] Accordion keyboard accessible

#### Performance Checklist

**Lighthouse targets:**
- Performance: >90
- Accessibility: >95
- Best Practices: >90
- SEO: >95

**Optimizations:**
- [ ] Images compressed (<200KB each)
- [ ] Lazy loading: `loading="lazy"` on below-fold images
- [ ] Tailwind CSS purged (production build)
- [ ] No console errors
- [ ] Page load <3s (desktop, fast 3G)

**Weryfikacja:** Run Lighthouse audit, check scores

---

## Ocena Ryzyka i Mitygacja

### WYSOKIE RYZYKO ⚠️

**1. Infinite Loop z `th:replace`**
- **Problem:** `<button th:replace>` w nested fragments → StackOverflowException
- **Symptom:** Strona nie ładuje się, wiele duplikatów elementów, błąd w konsoli serwera
- **Rozwiązanie:** ZAWSZE używaj `<th:block th:replace>` dla convenience fragments i nested calls
- **Lokalizacje:** testimonial.html, team-member.html, logo-bar.html convenience fragments, oraz sekcje 3, 6-9 w index.html

**2. CSS Nie Rebuilds**
- **Problem:** Nowe klasy Tailwind nie są rozpoznawane
- **Symptom:** Brak stylowania, komponenty wyglądają jak plain HTML
- **Rozwiązanie:**
    - Uruchom `./gradlew tailwindWatch` w osobnym terminalu
    - Sprawdź `tailwind.config.js` content paths: `'src/main/resources/templates/**/*.html'`
    - Zrestartuj watch jeśli nie pomaga

**3. JavaScript Nie Renderuje Się**
- **Problem:** Skrypty poza fragmentem `content` nie są renderowane
- **Symptom:** Accordion nie działa, brak interakcji
- **Rozwiązanie:** Wszystkie `<script>` tagi MUSZĄ być WEWNĄTRZ fragmentu content (przed zamknięciem `</div>`)
- **Weryfikacja:** View Source (Ctrl+U), szukaj kodu JavaScript

### ŚREDNIE RYZYKO ⚡

**4. Obrazy 404**
- **Problem:** Obrazy nie znalezione
- **Rozwiązanie:** Użyj `th:src="@{/images/file.jpg}"` dla poprawnego context path
- **Placeholder:** Użyj placehold.co lub ui-avatars.com w fazie prototypowania

**5. Accordion Nie Rozwija Się**
- **Problem:** Brak JavaScript Material Tailwind
- **Rozwiązanie:** Sprawdź czy `material-tailwind.js` jest załadowany w base.html
- **Weryfikacja:** Console → brak błędów 404

**6. Formularz Kontaktowy**
- **Problem:** Formularz w sekcji 9 nie submittuje
- **Rozwiązanie:** To jest tylko HTML, backend contact form endpoint musi być zaimplementowany osobno (nie w tym planie)
- **Workaround:** Link do istniejącej strony `/contact` lub mailto:

### NISKIE RYZYKO 🟢

**7. Slow Page Load**
- **Rozwiązanie:** Kompresja obrazów, lazy loading, WebP format

**8. Kolory Inconsistent**
- **Rozwiązanie:** Używaj tylko `primary-*` (civil law) i `burgundy-*` (criminal law), nigdy generycznych `blue-*`

---

## Weryfikacja i Testy

### Test Cases (End-to-End)

**Pre-deployment:**

1. **Kompilacja i Uruchomienie**
   ```bash
   ./gradlew clean build
   ./gradlew bootRun
   ```
    - [ ] Brak błędów kompilacji
    - [ ] Aplikacja startuje bez wyjątków
    - [ ] Strona ładuje się na http://localhost:8080

2. **Sekcje Renderują Się**
    - [ ] Section 1: Hero z 2 kolumnami
    - [ ] Section 2: 6 logotypów w grid
    - [ ] Section 3: 6 kart usług
    - [ ] Section 4: 4 boksy value props
    - [ ] Section 5: 3 kroki procesu (timeline)
    - [ ] Section 6: 3 karty prawników
    - [ ] Section 7: 6 testimonials
    - [ ] Section 8: Accordion FAQ (8 items)
    - [ ] Section 9: CTA + formularz kontaktowy

3. **Interakcje**
    - [ ] Accordion FAQ: kliknięcie rozwija/zwija
    - [ ] Wszystkie przyciski są klikalne
    - [ ] Linki telefoniczne działają (`tel:`)
    - [ ] Linki email działają (`mailto:`)

4. **Responsive**
    - [ ] Mobile 375px: wszystko czytelne, brak horizontal scroll
    - [ ] Tablet 768px: grids przechodzą na 2 kolumny
    - [ ] Desktop 1440px: pełne 3-4 kolumny grids

5. **SEO**
    - [ ] `<title>` tag poprawny
    - [ ] Meta description obecna
    - [ ] Schema.org JSON-LD waliduje się (validator.schema.org)
    - [ ] Heading hierarchy: h1 → h2 (nie h1 → h3)

6. **Performance**
    - [ ] Lighthouse Performance >80 (dev mode)
    - [ ] Brak console errors (browser DevTools)
    - [ ] Wszystkie obrazy ładują się
    - [ ] Page load <5s (dev mode, localhost)

**Post-deployment (Production):**
- [ ] Lighthouse Performance >90
- [ ] GTmetrix Grade A/B
- [ ] Mobile-friendly test (Google)
- [ ] Schema.org validator

---

## Krytyczne Pliki

### Do Stworzenia (4 nowe pliki)

1. **`src/main/java/pl/klastbit/lexpage/infrastructure/web/dto/homepage/HomepageDtos.java`**
    - 7 Java Records (DTOs)
    - ~50 linii kodu

2. **`src/main/resources/templates/fragments/components/testimonial.html`**
    - Komponent opinii klientów
    - ~80 linii HTML

3. **`src/main/resources/templates/fragments/components/logo-bar.html`**
    - Komponent trust badges
    - ~40 linii HTML

4. **`src/main/resources/templates/fragments/components/team-member.html`**
    - Komponent karty prawnika
    - ~90 linii HTML

### Do Modyfikacji (2 pliki)

5. **`src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/PageController.java`**
    - Rozbudowa metody `index()` z danymi dla 9 sekcji
    - Z ~30 linii do ~250 linii
    - **NAJWAŻNIEJSZY PLIK** - źródło danych

6. **`src/main/resources/templates/pages/index.html`**
    - Rozbudowa z 2 sekcji do 9 sekcji
    - Z ~100 linii do ~500 linii
    - **NAJWAŻNIEJSZY PLIK** - główna strona

### Do Dodania (10-12 obrazów)

7. **`src/main/resources/static/images/`**
    - hero-lawyer.jpg
    - team/anna-kowalska.jpg, piotr-nowak.jpg, katarzyna-wisniewska.jpg
    - logos/ (6 SVG files)

---

## Sukces Implementacji

### Funkcjonalne

- ✅ Wszystkie 9 sekcji obecne i funkcjonalne
- ✅ Responsive na mobile/tablet/desktop
- ✅ Brak błędów w konsoli (browser i serwer)
- ✅ Accordion FAQ działa (expand/collapse)
- ✅ Wszystkie przyciski i linki klikalne
- ✅ Obrazy ładują się poprawnie

### Techniczne

- ✅ Lighthouse Performance >90 (production)
- ✅ Lighthouse Accessibility >95
- ✅ Lighthouse SEO >95
- ✅ Page load <3s (homepage)
- ✅ Brak StackOverflowException
- ✅ Tailwind CSS kompiluje się poprawnie
- ✅ Schema.org JSON-LD waliduje się

### Wizualne

- ✅ Profesjonalny wygląd (Material Tailwind)
- ✅ Spójne kolory (primary blue, burgundy accent)
- ✅ Czytelna typografia (heading hierarchy)
- ✅ Odpowiednie spacing między sekcjami
- ✅ Hover states na przyciskach/linkach

### Biznesowe

- ✅ Jasny value proposition w hero
- ✅ Trust signals (logos, testimonials, stats)
- ✅ Wielokrotne CTA (przyciski w hero, końcowa sekcja)
- ✅ Low-friction contact (formularz + telefon + email)
- ✅ FAQ odpowiada na obiekcje
- ✅ Zespół zhumanizowany (zdjęcia, bios)

---

## Post-Implementacja (Przyszłe Ulepszenia)

### Faza 8 (Nice-to-Have)

1. **A/B Testing**
    - CTA button text variations
    - Testimonial count (3 vs 6)
    - FAQ placement

2. **Analytics**
    - Google Analytics 4 events (button clicks, form submits)
    - Heatmaps (Hotjar, Microsoft Clarity)
    - Scroll depth tracking

3. **Conversion Optimization**
    - Exit-intent popup
    - Sticky mobile CTA button
    - Live chat widget (Tawk.to)
    - Calendly booking integration

4. **Content Enhancements**
    - Video testimonials (YouTube embeds)
    - Case studies section
    - Blog preview (latest 3 articles)
    - Awards/certifications section

5. **Technical**
    - Image lazy loading (native or library)
    - Service worker for offline
    - Dark mode toggle
    - Internationalization (PL/EN)

---

## Timeline Estimate

**Total: 10-12 hours (part-time)**

- Faza 1: DTOs (30 min)
- Faza 2: Komponenty (2-3h)
    - testimonial.html: 45 min
    - logo-bar.html: 30 min
    - team-member.html: 60 min
    - Testing: 30 min
- Faza 3: PageController (1h)
- Faza 4: index.html template (3-4h)
    - Section 1-3: 1h
    - Section 4-6: 1.5h
    - Section 7-9: 1.5h
- Faza 5: Schema.org (15 min)
- Faza 6: Obrazy (1h)
- Faza 7: Responsive testing (1h)
- Faza 8: A11y & Performance (1h)

**Breakdown by expertise:**
- Junior dev: 14-16h
- Mid-level dev: 10-12h
- Senior dev: 8-10h

---

## Wsparcie i Dokumentacja

**Guidelines:**
- Frontend: `.ai/rules/frontend.md` - Material Tailwind patterns, Thymeleaf conventions
- Backend: `.ai/rules/backend.md` - Java Records, Hexagonal Architecture, DDD
- PRD: `.ai/prd.md` - Business requirements, user stories

**Komponenty Reference:**
- Lista wszystkich 27 komponentów: frontend.md linie 289-324
- Przykłady użycia: frontend.md linie 326-728

**Troubleshooting:**
- Infinite loop: frontend.md linie 1055-1080
- JavaScript nie renderuje: frontend.md linie 1082-1104
- CSS nie rebuilds: frontend.md linie 1028-1035

**Kontakt:**
- Issues: GitHub repository
- Documentation: CLAUDE.md, frontend.md, backend.md

---

## Podsumowanie

Ten plan transformuje obecną 2-sekcyjną stronę główną w profesjonalną, 9-sekcyjną stronę kancelarii prawnej, która:

1. **Buduje zaufanie** - przez social proof (logos), testimonials, team profiles
2. **Wyjaśnia proces** - timeline 3 kroków, FAQ
3. **Ułatwia konwersję** - wielokrotne CTA, prosty formularz kontaktowy
4. **Jest dostępna** - responsive, accessible, SEO-optimized
5. **Skaluje się** - reusable components, clean architecture, maintainable code

**Kluczowe zasady:**
- Użyj `<th:block th:replace>` dla nested fragments (uniknij infinite loop!)
- Wszystkie `<script>` tagi wewnątrz fragmentu content
- Mobile-first responsive design
- Material Tailwind components + Tailwind CSS
- Java Records dla DTOs
- Hexagonal Architecture (controller = inbound adapter)

**Rozpocznij od:** Fazy 1 (DTOs) → Faza 2 (Komponenty) → Faza 3 (Controller) → Faza 4 (Template)

🚀 **Gotowe do implementacji!**
