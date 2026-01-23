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
| **Layout & Navigation** |||
| Navbar | `navbar.html` | `~{fragments/components/navbar :: navbar}` |
| Footer | `footer.html` | `~{fragments/components/footer :: footer}` |
| Breadcrumbs | `breadcrumbs.html` | `~{fragments/components/breadcrumbs :: breadcrumbs(items, '/', '')}` |
| Tabs | `tabs.html` | `~{fragments/components/tabs :: tabs(id, tabs, 'underline', 'primary', false, '')}` |
| **Forms & Inputs** |||
| Button | `buttons.html` | `~{fragments/components/buttons :: button(...)}` |
| Input | `inputs.html` | `~{fragments/components/inputs :: input(...)}` |
| Textarea | `textarea.html` | `~{fragments/components/textarea :: textarea(...)}` |
| Select | `select.html` | `~{fragments/components/select :: select(...)}` |
| Checkbox | `checkbox.html` | `~{fragments/components/checkbox :: checkbox(...)}` |
| Radio | `radio.html` | `~{fragments/components/radio :: radio(...)}` |
| Switch | `switch.html` | `~{fragments/components/switch :: switch(...)}` |
| Slider | `slider.html` | `~{fragments/components/slider :: slider(...)}` |
| **Data Display** |||
| Card | `cards.html` | `~{fragments/components/cards :: card(...)}` |
| List | `list.html` | `~{fragments/components/list :: list(items, 'simple', '', '')}` |
| Avatar | `avatar.html` | `~{fragments/components/avatar :: avatar(src, alt, initials, 'md', 'circular', false, '')}` |
| Badge | `badge.html` | `~{fragments/components/badge :: badge(text, 'primary', 'filled', 'md', false, false, '')}` |
| Chip | `chip.html` | `~{fragments/components/chip :: chip(text, null, null, 'gray', 'filled', 'md', false, null, '')}` |
| Accordion | `accordion.html` | `~{fragments/components/accordion :: accordion(id, items, false, '')}` |
| Tooltip | `tooltip.html` | `~{fragments/components/tooltip :: tooltip(id, text, 'top', 'hover', content, '')}` |
| Typography | `typography.html` | `~{fragments/components/typography :: h1(text, '')}` |
| **Feedback** |||
| Alert | `alerts.html` | `~{fragments/components/alerts :: success('Message', true)}` |
| Dialog | `dialog.html` | `~{fragments/components/dialog :: dialog(id, title, content, actions, 'md', true, '')}` |
| Progress | `progress.html` | `~{fragments/components/progress :: progress(value, 100, label, true, 'primary', 'md', false, false, '')}` |
| Spinner | `spinner.html` | `~{fragments/components/spinner :: spinner('border', 'md', 'primary', null, false, '')}` |
| Rating | `rating.html` | `~{fragments/components/rating :: rating(value, 5, true, null, 'md', 'yellow', false, 1, '')}` |
| **Navigation** |||
| Dropdown | `dropdown.html` | `~{fragments/components/dropdown :: dropdown(id, buttonText, null, items, 'primary', 'filled', 'md', 'left', '')}` |
| Pagination | `pagination.html` | `~{fragments/components/pagination :: pagination(currentPage, totalPages, baseUrl, 'page', 'numbered', true, 7, '')}` |

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

### Nowe Komponenty - Przykłady Użycia

#### Accordion

```html
<!-- Simple accordion -->
<div th:replace="~{fragments/components/accordion :: accordion('myAccordion', ${items}, false, '')}"></div>

<!-- Where items is a List with objects having 'title' and 'content' -->
```

#### Avatar

```html
<!-- Avatar with image -->
<th:block th:replace="~{fragments/components/avatar :: avatar('/images/user.jpg', 'John Doe', null, 'md', 'circular', false, '')}"></th:block>

<!-- Avatar with initials -->
<th:block th:replace="~{fragments/components/avatar :: avatar(null, 'John Doe', 'JD', 'md', 'circular', false, '')}"></th:block>

<!-- Avatar with status -->
<div th:replace="~{fragments/components/avatar :: avatarWithStatus('/images/user.jpg', 'John Doe', null, 'md', 'circular', false, 'online', '')}"></div>

<!-- Avatar group -->
<div th:replace="~{fragments/components/avatar :: avatarGroup(${users}, 'md', 5)}"></div>
```

#### Badge

```html
<!-- Primary badge -->
<th:block th:replace="~{fragments/components/badge :: badge('New', 'primary', 'filled', 'md', false, false, '')}"></th:block>

<!-- Badge with icon -->
<span th:replace="~{fragments/components/badge :: badgeWithIcon('3 notifications', 'notifications', 'left', 'error', 'filled', 'md', false, '')}"></span>

<!-- Pill badge -->
<th:block th:replace="~{fragments/components/badge :: pill('Active', 'success')}"></th:block>

<!-- Dot indicator -->
<th:block th:replace="~{fragments/components/badge :: dot('online')}"></th:block>
```

#### Breadcrumbs

```html
<!-- Basic breadcrumbs -->
<nav th:replace="~{fragments/components/breadcrumbs :: breadcrumbs(${breadcrumbItems}, 'chevron_right', '')}"></nav>

<!-- With background -->
<nav th:replace="~{fragments/components/breadcrumbs :: breadcrumbsWithBg(${breadcrumbItems}, 'chevron_right', '')}"></nav>

<!-- Where breadcrumbItems is a List with objects having 'text' and 'url' -->
```

#### Checkbox

```html
<!-- Simple checkbox -->
<div th:replace="~{fragments/components/checkbox :: checkbox('agree', 'agree', 'I agree to terms', 'true', false, false, 'primary', null, null, '')}"></div>

<!-- Checkbox group -->
<fieldset th:replace="~{fragments/components/checkbox :: checkboxGroup('Select options', 'options', ${checkboxOptions}, false, 'primary', null, '')}"></fieldset>

<!-- Toggle switch styled checkbox -->
<div th:replace="~{fragments/components/checkbox :: toggle('notifications', 'notifications', 'Enable notifications', false, false, 'primary', 'Get notified about updates', '')}"></div>
```

#### Chip

```html
<!-- Simple chip -->
<th:block th:replace="~{fragments/components/chip :: simple('Technology')}"></th:block>

<!-- Chip with icon -->
<div th:replace="~{fragments/components/chip :: chip('Favorites', 'star', null, 'primary', 'filled', 'md', false, null, '')}"></div>

<!-- Removable chip -->
<div th:replace="~{fragments/components/chip :: chip('Filter', null, null, 'gray', 'filled', 'md', true, 'removeChip()', '')}"></div>

<!-- Chip group -->
<div th:replace="~{fragments/components/chip :: chipGroup(${tags}, 'gray', 'filled', 'md', false, '')}"></div>
```

#### Dialog/Modal

```html
<!-- Basic dialog -->
<div th:replace="~{fragments/components/dialog :: dialog('myDialog', 'Dialog Title', ~{::dialog-content}, ~{::dialog-actions}, 'md', true, '')}">
    <div th:fragment="dialog-content">
        <p>Dialog content goes here</p>
    </div>
    <div th:fragment="dialog-actions">
        <th:block th:replace="~{fragments/components/buttons :: primary('Save', null)}"></th:block>
    </div>
</div>

<!-- Confirmation dialog -->
<div th:replace="~{fragments/components/dialog :: confirmDialog('confirmDelete', 'Delete Item', 'Are you sure?', 'Delete', 'Cancel', 'error', 'handleDelete()')}"></div>

<!-- Alert dialog -->
<div th:replace="~{fragments/components/dialog :: alertDialog('alertDialog', 'Success', 'Operation completed!', 'check_circle', 'text-green-600', 'OK')}"></div>
```

#### Dropdown

```html
<!-- Basic dropdown -->
<div th:replace="~{fragments/components/dropdown :: dropdown('myDropdown', 'Actions', null, ${menuItems}, 'primary', 'filled', 'md', 'left', '')}"></div>

<!-- User dropdown -->
<div th:replace="~{fragments/components/dropdown :: userDropdown('userMenu', ${userName}, ${userEmail}, ${avatarUrl}, null, ${userMenuItems}, '')}"></div>

<!-- Icon dropdown -->
<div th:replace="~{fragments/components/dropdown :: iconDropdown('moreMenu', 'more_vert', ${actions}, 'gray', 'right', '')}"></div>
```

#### List

```html
<!-- Simple list -->
<ul th:replace="~{fragments/components/list :: list(${listItems}, 'simple', '', '')}"></ul>

<!-- Bordered list -->
<ul th:replace="~{fragments/components/list :: bordered(${listItems})}"></ul>

<!-- Selectable list -->
<ul th:replace="~{fragments/components/list :: selectableList(${items}, 'selectedItems', 'divided', '', '')}"></ul>

<!-- Action list -->
<ul th:replace="~{fragments/components/list :: actionList(${items}, 'bordered', '', '')}"></ul>
```

#### Pagination

```html
<!-- Simple pagination -->
<nav th:replace="~{fragments/components/pagination :: pagination(${currentPage}, ${totalPages}, '/items', 'page', 'numbered', true, 7, '')}"></nav>

<!-- With page size selector -->
<div th:replace="~{fragments/components/pagination :: paginationWithSize(${currentPage}, ${totalPages}, '/items', 'page', ${pageSizes}, ${pageSize}, 'size', 'numbered', true, 7, '')}"></div>

<!-- Load more button -->
<div th:replace="~{fragments/components/pagination :: loadMore(${currentPage}, ${totalPages}, '/items?page=' + ${currentPage + 1}, false, '')}"></div>
```

#### Progress Bar

```html
<!-- Simple progress bar -->
<div th:replace="~{fragments/components/progress :: progress(65, 100, 'Upload progress', true, 'primary', 'md', false, false, '')}"></div>

<!-- Striped and animated -->
<div th:replace="~{fragments/components/progress :: striped(75, 'Loading')}"></div>

<!-- Circular progress -->
<div th:replace="~{fragments/components/progress :: circularProgress(80, 100, 80, 8, 'primary', true, null, '')}"></div>

<!-- Indeterminate spinner -->
<div th:replace="~{fragments/components/progress :: indeterminate('primary', 'md', 'Loading...', '')}"></div>

<!-- Stepped progress -->
<div th:replace="~{fragments/components/progress :: steppedProgress(${steps}, ${currentStep}, 'primary', '')}"></div>
```

#### Radio Button

```html
<!-- Simple radio -->
<div th:replace="~{fragments/components/radio :: radio('option1', 'choice', 'Option 1', 'value1', false, false, 'primary', null, null, '')}"></div>

<!-- Radio group -->
<fieldset th:replace="~{fragments/components/radio :: radioGroup('Select an option', 'choice', ${radioOptions}, false, 'primary', 'vertical', null, '')}"></fieldset>

<!-- Card radio -->
<label th:replace="~{fragments/components/radio :: cardRadio('cardOption1', 'plan', 'Basic Plan', '$9/month', 'account_circle', 'basic', false, false, '')}"></label>

<!-- Button radio group -->
<div th:replace="~{fragments/components/radio :: buttonRadioGroup('view', ${viewOptions}, 'primary', 'md', '')}"></div>
```

#### Rating

```html
<!-- Simple rating (read-only) -->
<div th:replace="~{fragments/components/rating :: rating(4.5, 5, true, null, 'md', 'yellow', false, 0.5, '')}"></div>

<!-- Rating with reviews count -->
<div th:replace="~{fragments/components/rating :: ratingWithReviews(4.2, 5, 128, true, 'md', 'yellow', '')}"></div>

<!-- Aggregate rating with distribution -->
<div th:replace="~{fragments/components/rating :: ratingAggregate(${avgRating}, 5, ${totalRatings}, 'md', true, ${distribution}, '')}"></div>

<!-- Interactive rating -->
<div th:replace="~{fragments/components/rating :: interactiveRating('rating', 5, 0, 'lg', 'yellow', '')}"></div>
```

#### Select

```html
<!-- Basic select -->
<div th:replace="~{fragments/components/select :: select('country', 'country', 'Country', ${countries}, 'Select country', false, false, 'md', null, null, null, '')}"></div>

<!-- Multi-select -->
<div th:replace="~{fragments/components/select :: multiSelect('skills', 'skills', 'Skills', ${skillOptions}, true, false, 'md', null, 'Hold Ctrl to select multiple', '')}"></div>

<!-- Grouped select -->
<div th:replace="~{fragments/components/select :: groupedSelect('category', 'category', 'Category', ${groupedOptions}, 'Select category', false, false, 'md', null, null, '')}"></div>
```

#### Slider

```html
<!-- Simple slider -->
<div th:replace="~{fragments/components/slider :: slider('volume', 'volume', 'Volume', 0, 100, 1, 50, false, 'primary', true, false, null, '')}"></div>

<!-- Slider with min/max labels -->
<div th:replace="~{fragments/components/slider :: withMinMax('price', 'price', 'Price', 0, 1000)}"></div>

<!-- Dual range slider -->
<div th:replace="~{fragments/components/slider :: dualSlider('priceRange', 'minPrice', 'maxPrice', 'Price Range', 0, 1000, 10, 100, 800, false, 'primary', true, '')}"></div>

<!-- Slider with marks -->
<div th:replace="~{fragments/components/slider :: sliderWithMarks('rating', 'rating', 'Rating', 0, 10, 1, 5, ${marks}, false, 'primary', true, '')}"></div>
```

#### Spinner/Loader

```html
<!-- Border spinner -->
<th:block th:replace="~{fragments/components/spinner :: spinner('border', 'md', 'primary', null, false, '')}"></th:block>

<!-- Dots spinner -->
<th:block th:replace="~{fragments/components/spinner :: dots()}"></th:block>

<!-- Centered with label -->
<div th:replace="~{fragments/components/spinner :: centered('Loading content...')}"></div>

<!-- Button with spinner -->
<button th:replace="~{fragments/components/spinner :: buttonWithSpinner('Save', ${isLoading}, false, 'primary', 'filled', 'md', 'submit', '')}"></button>

<!-- Overlay spinner -->
<div th:replace="~{fragments/components/spinner :: overlaySpinner('Please wait...', false, 'lg', 'primary', '')}"></div>

<!-- Skeleton loader -->
<div th:replace="~{fragments/components/spinner :: skeleton('text', 3, null, null, '')}"></div>
```

#### Switch/Toggle

```html
<!-- Simple switch -->
<div th:replace="~{fragments/components/switch :: switch('enabled', 'enabled', 'Enable feature', false, false, 'primary', 'md', null, 'left', '')}"></div>

<!-- Switch with description -->
<div th:replace="~{fragments/components/switch :: withDescription('notifications', 'notifications', 'Push Notifications', 'Receive notifications on your device')}"></div>

<!-- Switch with icons -->
<div th:replace="~{fragments/components/switch :: switchWithIcon('darkMode', 'darkMode', 'Dark Mode', false, false, 'primary', 'md', 'check', 'close', '')}"></div>

<!-- Compact switch -->
<label th:replace="~{fragments/components/switch :: compactSwitch('compact', 'compact', 'Compact', false, false, 'primary', '')}"></label>
```

#### Tabs

```html
<!-- Underline tabs -->
<div th:replace="~{fragments/components/tabs :: tabs('myTabs', ${tabItems}, 'underline', 'primary', false, '')}"></div>

<!-- Pills tabs -->
<div th:replace="~{fragments/components/tabs :: pills('pillTabs', ${tabItems})}"></div>

<!-- Full width tabs -->
<div th:replace="~{fragments/components/tabs :: fullWidth('fullTabs', ${tabItems})}"></div>

<!-- Vertical tabs -->
<div th:replace="~{fragments/components/tabs :: verticalTabs('verticalTabs', ${tabItems}, 'primary', '')}"></div>

<!-- Where tabItems is a List with objects having 'id', 'label', 'icon', 'content', 'active' -->
```

#### Textarea

```html
<!-- Simple textarea -->
<div th:replace="~{fragments/components/textarea :: textarea('message', 'message', 'Message', 'Enter your message', null, 4, false, false, false, null, 'vertical', null, null, false, '')}"></div>

<!-- With character count -->
<div th:replace="~{fragments/components/textarea :: withCharCount('bio', 'bio', 'Biography', 500)}"></div>

<!-- Auto-resize textarea -->
<div th:replace="~{fragments/components/textarea :: autoResizeTextarea('comment', 'comment', 'Comment', 'Type your comment', null, 3, 10, false, false, null, null, '')}"></div>

<!-- Rich text editor -->
<div th:replace="~{fragments/components/textarea :: richTextarea('content', 'content', 'Content', 'Enter content', null, 6, false, false, null, null, null, '')}"></div>
```

#### Tooltip

```html
<!-- Simple tooltip -->
<span th:replace="~{fragments/components/tooltip :: tooltip('tip1', 'Helpful information', 'top', 'hover', ~{::tooltip-trigger}, '')}">
    <span th:fragment="tooltip-trigger">Hover me</span>
</span>

<!-- Icon with tooltip -->
<span th:replace="~{fragments/components/tooltip :: iconTooltip('helpTip', 'help_outline', 'This is helpful information', 'top', 'text-gray-600', 'text-base', '')}"></span>

<!-- Help text with tooltip -->
<span th:replace="~{fragments/components/tooltip :: helpTooltip('help1', 'Field Label', 'Explanation of this field', 'top', '')}"></span>

<!-- Rich tooltip -->
<div th:replace="~{fragments/components/tooltip :: richTooltip('richTip', 'Tooltip Title', 'Detailed description here', 'top', ~{::trigger}, '')}">
    <button th:fragment="trigger" class="btn">Hover me</button>
</div>
```

#### Typography

```html
<!-- Headings -->
<th:block th:replace="~{fragments/components/typography :: h1('Main Title', '')}"></th:block>
<th:block th:replace="~{fragments/components/typography :: h2('Section Title', '')}"></th:block>

<!-- Paragraph -->
<p th:replace="~{fragments/components/typography :: paragraph('Lorem ipsum dolor sit amet...', '')}"></p>

<!-- Lead paragraph -->
<p th:replace="~{fragments/components/typography :: lead('Introduction text...', '')}"></p>

<!-- Links -->
<a th:replace="~{fragments/components/typography :: link('Click here', '/page', '')}"></a>
<a th:replace="~{fragments/components/typography :: externalLink('External site', 'https://example.com', '')}"></a>

<!-- Code -->
<code th:replace="~{fragments/components/typography :: code('const x = 10;', '')}"></code>

<!-- Blockquote -->
<blockquote th:replace="~{fragments/components/typography :: blockquote('Quote text', 'Author Name', '')}"></blockquote>

<!-- Colored text -->
<span th:replace="~{fragments/components/typography :: coloredText('Success!', 'success', '')}"></span>

<!-- Gradient text -->
<span th:replace="~{fragments/components/typography :: gradientText('Premium Feature', 'text-3xl')}"></span>

<!-- Lists -->
<ul th:replace="~{fragments/components/typography :: ul(${items}, '')}"></ul>
<ol th:replace="~{fragments/components/typography :: ol(${items}, '')}"></ol>
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
