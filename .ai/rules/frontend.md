# Frontend Guidelines - Lexpage

## Stack Technologiczny

**UI Framework:** Material Tailwind HTML v2.3.2
**CSS Framework:** Tailwind CSS v3.4.1
**Template Engine:** Thymeleaf (Spring Boot)
**JavaScript:** Vanilla JS (no frameworks)
**Build Tools:** npm + Gradle (node-gradle plugin v7.1.0)
**Node.js:** v20.11.0 LTS (auto-downloaded by Gradle)

## Architektura Frontend

### Server-Side Rendering (SSR)
- **NIE używamy** SPA (React, Angular, Vue)
- **TAK używamy** Thymeleaf server-side templates
- **JavaScript** tylko dla interaktywności (Material Tailwind ripple effects, mobile menu, forms)
- **Routing** przez Spring MVC Controllers

### Dlaczego SSR?
- Lepsza SEO (content rendered server-side)
- Szybszy first paint
- Prostsza architektura (brak API-first approach)
- Lepsze wsparcie dla progressive enhancement

---

## Struktura Katalogów

```
lexpage/
├── frontend/                          # Frontend source files
│   ├── css/
│   │   └── input.css                 # Tailwind CSS input (directives)
│   └── js/
│       └── app.js                    # Custom JavaScript
│
├── src/main/resources/
│   ├── static/                       # Compiled/static assets (gitignored)
│   │   ├── css/
│   │   │   └── output.css            # ⚠️ GENERATED - nie edytuj!
│   │   ├── js/
│   │   │   ├── material-tailwind.js  # ⚠️ GENERATED - ripple effects
│   │   │   └── app.js                # ⚠️ COPIED from frontend/js/
│   │   └── images/                   # Static images, logos
│   │
│   └── templates/                    # Thymeleaf templates
│       ├── layouts/
│       │   ├── base.html             # Base HTML structure (head, scripts)
│       │   └── main.html             # Main layout (navbar + content + footer)
│       │
│       ├── fragments/
│       │   └── components/           # Reusable Material Tailwind components
│       │       ├── buttons.html      # Button variants
│       │       ├── inputs.html       # Input fields with floating labels
│       │       ├── cards.html        # Card component
│       │       ├── alerts.html       # Alert messages
│       │       ├── navbar.html       # Site navigation
│       │       └── footer.html       # Site footer
│       │
│       └── pages/                    # Full page templates
│           ├── index.html            # Homepage
│           └── contact.html          # Contact form page
│
├── scripts/
│   └── copy-material-tailwind.js     # Copies MT JS to static/
│
├── package.json                       # npm dependencies & scripts
├── tailwind.config.js                 # Tailwind + Material Tailwind config
└── postcss.config.js                  # PostCSS config
```

---

## Konwencje Thymeleaf

### 1. Layout Pattern

**Base Layout** (`layouts/base.html`):
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="pl">
<head>
    <title th:text="${pageTitle ?: 'Lexpage'}">Lexpage</title>
    <link rel="stylesheet" th:href="@{/css/output.css}">
    <!-- Material Icons, Fonts -->
</head>
<body>
    <div th:replace="${content}"></div>
    <script th:src="@{/js/material-tailwind.js}"></script>
    <script th:src="@{/js/app.js}"></script>
</body>
</html>
```

**Main Layout** (`layouts/main.html`):
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layouts/base :: html}">
<body>
    <div th:fragment="content">
        <nav th:replace="~{fragments/components/navbar :: navbar}"></nav>
        <main class="min-h-screen bg-gray-50">
            <th:block th:replace="${pageContent}"></th:block>
        </main>
        <footer th:replace="~{fragments/components/footer :: footer}"></footer>
    </div>
</body>
</html>
```

**Page Template** (`pages/example.html`):
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layouts/main :: html}">
<head>
    <title th:text="${pageTitle}">Page Title</title>
</head>
<body>
<div th:fragment="pageContent">
    <!-- Your page content here -->
</div>
</body>
</html>
```

### 2. Component Pattern

**ZAWSZE** twórz reusable components w `fragments/components/`.

**Parametryzowane Fragmenty:**
```html
<!-- Component Definition -->
<button th:fragment="button(text, type, variant, color, size, fullWidth, disabled, icon, iconPosition, customClass, onclick)"
        th:type="${type ?: 'button'}"
        th:class="'...' + ${customClass ?: ''}">
    <span th:text="${text}">Button</span>
</button>

<!-- Component Usage - Bezpośrednie użycie w stronie -->
<th:block th:replace="~{fragments/components/buttons :: button(
    'Wyślij',
    'submit',
    'filled',
    'primary',
    'md',
    true,
    false,
    'send',
    'right',
    '',
    null
)}"></th:block>

<!-- ⚠️ UWAGA: Zawsze używaj <th:block th:replace>, nie <button th:replace>! -->
```

**Convenience Fragments** (shortcuts):
```html
<!-- Definition - UWAGA: Użyj <th:block>, nie <button>! -->
<th:block th:fragment="submit(text)"
          th:replace="~{fragments/components/buttons :: button(
              ${text}, 'submit', 'filled', 'primary', 'md', false, false, null, 'left', '', null
          )}">
</th:block>

<!-- Usage -->
<th:block th:replace="~{fragments/components/buttons :: submit('Zapisz')}"></th:block>
```

### 3. Fragment Content Pattern

Używaj named fragments dla dynamicznej treści w komponentach:

```html
<!-- Card with dynamic content -->
<div th:replace="~{fragments/components/cards :: card(
    'Title',
    'Subtitle',
    ~{::card-content},
    ~{::card-actions},
    ''
)}">

    <div th:fragment="card-content">
        <p>Custom content goes here</p>
    </div>

    <div th:fragment="card-actions">
        <th:block th:replace="~{fragments/components/buttons :: primary('Action', null)}"></th:block>
    </div>
</div>
```

### 4. ⚠️ KRYTYCZNA ZASADA: Używaj `<th:block>` z `th:replace`

**NIGDY** nie używaj elementów HTML (np. `<button>`, `<div>`) z atrybutem `th:replace` wewnątrz zagnieżdżonych fragmentów. To powoduje **nieskończoną pętlę** i StackOverflowException.

**❌ ŹLE - Powoduje nieskończoną pętlę:**
```html
<!-- W fragmencie przekazywanym jako parametr -->
<div th:fragment="card-actions">
    <button th:replace="~{fragments/components/buttons :: button(...)}"></button>
</div>

<!-- W convenience fragments -->
<button th:fragment="primary(text, icon)"
        th:replace="~{fragments/components/buttons :: button(...)}">
</button>
```

**✅ DOBRZE - Użyj `<th:block>`:**
```html
<!-- W fragmencie przekazywanym jako parametr -->
<div th:fragment="card-actions">
    <th:block th:replace="~{fragments/components/buttons :: button(...)}"></th:block>
</div>

<!-- W convenience fragments -->
<th:block th:fragment="primary(text, icon)"
          th:replace="~{fragments/components/buttons :: button(...)}">
</th:block>
```

**Dlaczego to działa:**
- `<th:block>` nie generuje żadnego HTML, służy tylko do logiki Thymeleaf
- Eliminuje problem rekursywnego zastępowania elementów
- Thymeleaf prawidłowo zastępuje blok bez tworzenia zagnieżdżonych struktur

**Kiedy używać `<th:block>`:**
- Gdy fragment ma atrybut `th:replace` i jest przekazywany jako parametr do innego fragmentu
- W convenience fragments, które wywołują inne fragmenty
- W ogólności: **zawsze gdy używasz `th:replace` na fragmencie, który sam używa `th:replace`**

---

## Material Tailwind Components

### Dostępne Komponenty

| Komponent | Plik | Przykład użycia |
|-----------|------|-----------------|
| Button | `buttons.html` | `~{fragments/components/buttons :: button(...)}` |
| Input | `inputs.html` | `~{fragments/components/inputs :: input(...)}` |
| Alert | `alerts.html` | `~{fragments/components/alerts :: success('Message', true)}` |
| Card | `cards.html` | `~{fragments/components/cards :: card(...)}` |
| Navbar | `navbar.html` | `~{fragments/components/navbar :: navbar}` |
| Footer | `footer.html` | `~{fragments/components/footer :: footer}` |

### Button Variants

```html
<!-- Filled (default) -->
<th:block th:replace="~{fragments/components/buttons :: button('Text', 'button', 'filled', 'primary', 'md', false, false, null, 'left', '', null)}"></th:block>

<!-- Outlined -->
<th:block th:replace="~{fragments/components/buttons :: button('Text', 'button', 'outlined', 'primary', 'md', false, false, null, 'left', '', null)}"></th:block>

<!-- Text -->
<th:block th:replace="~{fragments/components/buttons :: button('Text', 'button', 'text', 'primary', 'md', false, false, null, 'left', '', null)}"></th:block>

<!-- With icon -->
<th:block th:replace="~{fragments/components/buttons :: button('Send', 'button', 'filled', 'primary', 'md', false, false, 'send', 'right', '', null)}"></th:block>

<!-- Shortcuts -->
<th:block th:replace="~{fragments/components/buttons :: primary('Text', 'icon_name')}"></th:block>
<th:block th:replace="~{fragments/components/buttons :: submit('Wyślij')}"></th:block>
```

### Input Fields

```html
<!-- Basic input -->
<div th:replace="~{fragments/components/inputs :: input('id', 'name', 'Label', 'text', null, null, true, false, null, 'icon')}"></div>

<!-- Email with icon -->
<div th:replace="~{fragments/components/inputs :: email('email', 'email', 'Email', null, true, null)}"></div>

<!-- Phone -->
<div th:replace="~{fragments/components/inputs :: phone('phone', 'phone', 'Telefon', null, false, null)}"></div>

<!-- Password -->
<div th:replace="~{fragments/components/inputs :: password('password', 'password', 'Hasło', true, null)}"></div>

<!-- With error -->
<div th:replace="~{fragments/components/inputs :: input('firstName', 'firstName', 'Imię', 'text', null, null, true, false, ${errors['firstName']}, 'person')}"></div>
```

### Alerts

```html
<!-- Success -->
<div th:replace="~{fragments/components/alerts :: success('Operation successful!', true)}"></div>

<!-- Error -->
<div th:replace="~{fragments/components/alerts :: error('An error occurred.', true)}"></div>

<!-- Warning -->
<div th:replace="~{fragments/components/alerts :: warning('Warning message.', false)}"></div>
```

---

## Tailwind CSS Konfiguracja

### Custom Colors (Brand Palette)

```javascript
// tailwind.config.js
colors: {
  primary: {     // Primary blue
    500: '#0ea5e9',
    600: '#0284c7',
    700: '#0369a1',
    // ... full scale
  },
  burgundy: {    // Accent burgundy
    500: '#e0426a',
    600: '#cc2251',
    700: '#a91741',
    // ... full scale
  }
}
```

**Używaj:**
- `bg-primary-600` zamiast `bg-blue-500`
- `text-burgundy-700` dla akcentów

### Utility Classes

```css
/* Custom utilities w frontend/css/input.css */
@layer components {
  .btn-primary {
    @apply bg-primary-600 text-white hover:bg-primary-700 focus:ring-4 focus:ring-primary-300;
  }
}
```

### Material Icons

```html
<!-- W HTML -->
<i class="material-icons">icon_name</i>

<!-- Lista ikon: https://fonts.google.com/icons -->
```

---

## Build Process

### Development Workflow

**Terminal 1 - Spring Boot:**
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```
- Hot-reload templates (no restart)
- DevTools enabled
- LiveReload on port 35729

**Terminal 2 - Tailwind Watch:**
```bash
./gradlew tailwindWatch
```
- Watches `templates/**/*.html`
- Rebuilds CSS on change
- Browser auto-refreshes (LiveReload)

### Production Build

```bash
./gradlew clean build
```
- Minifies Tailwind CSS
- Purges unused classes
- Enables template caching
- Static resources cache (1 year)

### Gradle Tasks

```bash
# Install npm dependencies (automatic with bootRun)
./gradlew npmInstall

# Build frontend assets (minified)
./gradlew buildFrontend

# Watch CSS changes (development)
./gradlew tailwindWatch

# Clean frontend build artifacts
./gradlew cleanFrontend
```

---

## Spring MVC Controllers

### Page Controller Pattern

```java
@Controller
public class PageController {

    @GetMapping("/example")
    public String examplePage(Model model) {
        // Add page metadata
        model.addAttribute("pageTitle", "Example Page - Lexpage");
        model.addAttribute("pageDescription", "SEO description");

        // Add page data
        model.addAttribute("data", someData);

        // Return template path (relative to templates/)
        return "pages/example";  // -> templates/pages/example.html
    }
}
```

**Location:** `src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/`

**UWAGA:** PageController to **inbound adapter** w hexagonal architecture!

---

## Konwencje Kodowania

### 1. Nazewnictwo

**Templates:**
- Pages: `pages/example-page.html` (kebab-case)
- Components: `fragments/components/component-name.html`
- Layouts: `layouts/layout-name.html`

**Fragments:**
- Main fragment: `th:fragment="componentName(...)"` (camelCase)
- Convenience fragments: `th:fragment="shortcut(...)"` (camelCase)

**CSS Classes:**
- Używaj Tailwind utility classes
- Custom classes w `@layer components`
- NO inline styles (używaj Tailwind)

### 2. Responsywność

**Mobile-first approach:**
```html
<!-- Default: mobile -->
<div class="text-sm py-2">

<!-- Tablet and up -->
<div class="md:text-base md:py-4">

<!-- Desktop -->
<div class="lg:text-lg lg:py-6">
```

**Breakpoints:**
- `sm:` 640px
- `md:` 768px
- `lg:` 1024px
- `xl:` 1280px
- `2xl:` 1536px

### 3. Accessibility

**ZAWSZE:**
- `alt` dla obrazów
- `aria-label` dla ikon bez tekstu
- Semantic HTML (`<nav>`, `<main>`, `<footer>`)
- Keyboard navigation (Material Tailwind handles this)
- Color contrast (WCAG AA minimum)

### 4. SEO

```html
<!-- In controller -->
model.addAttribute("pageTitle", "Specific Title - Lexpage");
model.addAttribute("pageDescription", "Page description for SEO");
model.addAttribute("pageKeywords", "keyword1, keyword2");

<!-- In base.html -->
<title th:text="${pageTitle ?: 'Lexpage'}">Lexpage</title>
<meta name="description" th:content="${pageDescription ?: 'Default description'}">
```

---

## Dodawanie Nowych Komponentów

### Krok 1: Utwórz Fragment

`src/main/resources/templates/fragments/components/new-component.html`:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>

<!--
  Component Documentation

  Parametry:
  - param1: Description (required/optional)
  - param2: Description (default: value)
-->

<div th:fragment="componentName(param1, param2)"
     th:class="'base-classes ' + ${param2 ?: 'default'}">

    <span th:text="${param1}">Default content</span>
</div>

<!-- Convenience fragment - UWAGA: Użyj <th:block>, nie <div>! -->
<th:block th:fragment="shortcut(param1)"
          th:replace="~{fragments/components/new-component :: componentName(${param1}, 'default')}">
</th:block>

</body>
</html>
```

### Krok 2: Użyj w Stronie

```html
<div th:replace="~{fragments/components/new-component :: componentName('value', 'custom-class')}"></div>
```

### Krok 3: Dodaj do Dokumentacji

Zaktualizuj tę sekcję w `frontend.md`.

---

## Dodawanie Nowych Stron

### Krok 1: Utwórz Template

`src/main/resources/templates/pages/new-page.html`:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layouts/main :: html}">
<head>
    <title th:text="${pageTitle}">New Page</title>
</head>
<body>

<div th:fragment="pageContent">
    <!-- Hero Section -->
    <section class="bg-gradient-to-br from-primary-600 to-primary-400 py-20">
        <div class="container mx-auto px-4">
            <h1 class="text-4xl font-bold text-white">Page Title</h1>
        </div>
    </section>

    <!-- Content Section -->
    <section class="py-16">
        <div class="container mx-auto px-4">
            <!-- Your content -->
        </div>
    </section>
</div>

</body>
</html>
```

### Krok 2: Dodaj Controller Endpoint

`src/main/java/pl/klastbit/lexpage/infrastructure/web/controller/PageController.java`:

```java
@GetMapping("/new-page")
public String newPage(Model model) {
    model.addAttribute("pageTitle", "New Page - Lexpage");
    model.addAttribute("pageDescription", "SEO description");
    return "pages/new-page";
}
```

### Krok 3: Dodaj do Nawigacji

`src/main/resources/templates/fragments/components/navbar.html`:

```html
<li>
    <a href="/new-page">New Page</a>
</li>
```

---

## Troubleshooting

### CSS nie się przebudowuje

**Problem:** Zmiany w templates nie odświeżają CSS.

**Rozwiązanie:**
1. Sprawdź czy `tailwindWatch` jest uruchomiony
2. Sprawdź `tailwind.config.js` content paths
3. Zrestartuj `tailwindWatch`

### Komponenty Material Tailwind nie stylują się

**Problem:** Buttony, inputy wyglądają jak plain HTML.

**Rozwiązanie:**
1. Sprawdź czy `output.css` istnieje: `ls src/main/resources/static/css/`
2. Uruchom: `./gradlew buildFrontend`
3. Sprawdź browser console dla 404 errors

### Ripple effect nie działa

**Problem:** Kliknięcie buttona nie pokazuje ripple animation.

**Rozwiązanie:**
1. Sprawdź czy `material-tailwind.js` jest załadowany (DevTools > Network)
2. Sprawdź console errors
3. Zweryfikuj `data-ripple-light` attribute na buttonie

### StackOverflowException / Nieskończona pętla w Thymeleaf

**Problem:** Aplikacja wyrzuca `StackOverflowException`, bardzo wiele przycisków pojawia się na stronie, lub strona nie ładuje się.

**Przyczyna:** Używanie `<button th:replace>`, `<div th:replace>` lub innych elementów HTML z `th:replace` wewnątrz zagnieżdżonych fragmentów.

**Rozwiązanie:**
1. Znajdź wszystkie miejsca gdzie używasz `th:replace` wewnątrz fragmentów przekazywanych jako parametry
2. Zamień np. `<button th:replace="...">` na `<th:block th:replace="...">`
3. Szczególnie sprawdź:
   - Convenience fragments w plikach komponentów
   - Fragmenty `card-actions`, `card-content` przekazywane do kart
   - Przyciski wewnątrz fragmentów

**Przykład:**
```html
<!-- ❌ ŹLE - powoduje StackOverflow -->
<div th:fragment="card-actions">
    <button th:replace="~{buttons :: button(...)}"></button>
</div>

<!-- ✅ DOBRZE -->
<div th:fragment="card-actions">
    <th:block th:replace="~{buttons :: button(...)}"></th:block>
</div>
```

### Hot-reload nie działa

**Problem:** Zmiany w templates wymagają restartu aplikacji.

**Rozwiązanie:**
1. Sprawdź czy używasz profilu `dev`: `--spring.profiles.active=dev`
2. Sprawdź `application-dev.properties`: `spring.thymeleaf.cache=false`
3. Sprawdź czy `spring-boot-devtools` jest w dependencies

### LiveReload nie odświeża przeglądarki

**Problem:** Muszę ręcznie odświeżać przeglądarkę.

**Rozwiązanie:**
1. Zainstaluj LiveReload browser extension
2. Sprawdź czy DevTools jest enabled: `spring.devtools.livereload.enabled=true`
3. Sprawdź port 35729 (czy jest otwarty)

---

## Best Practices

### ✅ DO

- **Używaj komponentów** - nie duplikuj kodu HTML
- **Mobile-first** - projektuj od najmniejszego ekranu
- **Semantic HTML** - `<nav>`, `<main>`, `<section>`, `<article>`
- **Tailwind utilities** - zamiast custom CSS
- **Server-side validation** - nigdy nie ufaj klientowi
- **SEO metadata** - dla każdej strony
- **Alt text** - dla wszystkich obrazów
- **Accessibility** - ARIA labels, keyboard navigation

### ❌ DON'T

- **NIE edytuj** `static/css/output.css` (generated)
- **NIE edytuj** `static/js/material-tailwind.js` (copied)
- **NIE używaj** `<button th:replace>` lub `<div th:replace>` - zawsze użyj `<th:block th:replace>` (powoduje nieskończoną pętlę!)
- **NIE używaj** inline styles - używaj Tailwind
- **NIE twórz** custom CSS bez potrzeby
- **NIE duplikuj** komponentów - użyj fragments
- **NIE pomijaj** responsywności
- **NIE zapomnij** o error handling w formularzach
- **NIE używaj** `!important` w CSS

### 🎯 Code Review Checklist

Przed commitem sprawdź:

- [ ] **KRYTYCZNE:** Wszystkie `th:replace` używają `<th:block>`, nie `<button>` czy `<div>` (zapobiega nieskończonym pętlom)
- [ ] Komponenty są reusable (w `fragments/components/`)
- [ ] Strona jest responsywna (przetestuj mobile/tablet/desktop)
- [ ] SEO metadata ustawione (title, description)
- [ ] Accessibility - semantic HTML, alt text, ARIA
- [ ] Tailwind watch działa (CSS builds correctly)
- [ ] No console errors (browser DevTools)
- [ ] Forms mają validation i error handling
- [ ] Images są zoptymalizowane (WebP, compressed)
- [ ] Links działają (no 404)
- [ ] Hot-reload działa w dev mode

---

## Resources

**Material Tailwind:**
- Docs: https://www.material-tailwind.com/docs/html/introduction
- Components: https://www.material-tailwind.com/docs/html/button

**Tailwind CSS:**
- Docs: https://tailwindcss.com/docs
- Playground: https://play.tailwindcss.com

**Material Icons:**
- Icons: https://fonts.google.com/icons
- Usage: `<i class="material-icons">icon_name</i>`

**Thymeleaf:**
- Docs: https://www.thymeleaf.org/documentation.html
- Tutorial: https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html

---

## Quick Reference

### Common Tailwind Classes

```css
/* Layout */
.container       /* Max-width container, responsive */
.mx-auto         /* Center horizontally */
.px-4            /* Padding horizontal 1rem */
.py-16           /* Padding vertical 4rem */

/* Flexbox */
.flex            /* display: flex */
.flex-col        /* flex-direction: column */
.items-center    /* align-items: center */
.justify-between /* justify-content: space-between */
.gap-4           /* gap: 1rem */

/* Grid */
.grid                    /* display: grid */
.grid-cols-1             /* 1 column (mobile) */
.md:grid-cols-2          /* 2 columns (tablet+) */
.lg:grid-cols-3          /* 3 columns (desktop+) */

/* Typography */
.text-4xl        /* font-size: 2.25rem */
.font-bold       /* font-weight: 700 */
.text-center     /* text-align: center */
.text-white      /* color: white */

/* Spacing */
.mb-4            /* margin-bottom: 1rem */
.space-y-6       /* gap between children (vertical) */

/* Backgrounds */
.bg-primary-600  /* Custom primary color */
.bg-white        /* White background */
.bg-gray-50      /* Light gray background */

/* Borders & Shadows */
.rounded-lg      /* border-radius: 0.5rem */
.shadow-md       /* box-shadow (medium) */
.border          /* border: 1px solid */

/* Responsive */
.md:text-5xl     /* @media (min-width: 768px) */
.lg:py-20        /* @media (min-width: 1024px) */
```

### Material Tailwind Patterns

```html
<!-- Button with ripple -->
<button class="... shadow-md hover:shadow-lg" data-ripple-light="true">

<!-- Input with floating label -->
<input class="peer ..." placeholder=" ">
<label class="peer-placeholder-shown:... peer-focus:...">

<!-- Card -->
<div class="bg-white shadow-md rounded-xl">
```

---

**Ostatnia aktualizacja:** 2025-01-23
**Wersja:** 1.0
**Maintainer:** Claude AI + Development Team
