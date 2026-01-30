# PLAN IMPLEMENTACJI: Funkcjonalność AI do Generowania Artykułów

## PODSUMOWANIE WYKONAWCZE

Na podstawie gruntownej analizy kodu zaprojektowano szczegółową implementację funkcjonalności AI do generowania artykułów dla aplikacji Lexpage. Plan uwzględnia architekturę Hexagonal Architecture, wzorce DDD oraz wszystkie konwencje projektu.

---

## 1. DECYZJE ARCHITEKTONICZNE

### 1.1. AI Generation jako Domain Service czy Osobny Bounded Context?

**DECYZJA: Domain Service w istniejącym Article Bounded Context**

**Uzasadnienie:**
- AI generation jest **operacją wspierającą** tworzenie Article, nie jest osobnym agregatem
- Nie ma własnego lifecycle
- Wynik AI generation → to po prostu Draft Article
- KISS principle, unikamy over-engineering

**Trade-offs:**
- ✅ Prostsze zarządzanie transakcjami
- ✅ Mniej boilerplate code
- ✅ Łatwiejsza implementacja
- ❌ Brak historii AI generations (akceptowalne na MVP)

### 1.2. Port w Application czy Domain Layer?

**DECYZJA: `ArticleAIPort` w `application/ports/`**

**Uzasadnienie:**
- AI to **techniczna infrastruktura**, nie core business logic
- Domain nie powinien wiedzieć o AI, HTTP, zewnętrznych API
- Domain zajmuje się Article lifecycle, nie sposobem generowania content
- Zgodne z DDD: infrastructure concerns ≠ domain concerns

### 1.3. Markdown → HTML Conversion

**DECYZJA: Backend konwertuje Markdown → HTML (CommonMark Java)**

**Uzasadnienie:**
- ✅ Separacja odpowiedzialności - backend dostarcza gotowy HTML
- ✅ Reusability - możemy użyć tej logiki w innych miejscach
- ✅ Security - backend kontroluje HTML output
- ✅ Performance - CommonMark Java jest bardzo szybki

---

## 2. STRUKTURA PAKIETÓW

```
pl.klastbit.lexpage/
├── application/
│   ├── article/
│   │   ├── command/
│   │   │   └── GenerateArticleWithAICommand.java      🆕 NOWY
│   │   ├── dto/
│   │   │   └── AIGeneratedContentDto.java            🆕 NOWY
│   │   ├── exception/
│   │   │   └── AIGenerationException.java            🆕 NOWY
│   │   ├── usecase/
│   │   │   └── GenerateArticleWithAIUseCaseImpl.java 🆕 NOWY
│   │   └── GenerateArticleWithAIUseCase.java         🆕 NOWY
│   └── ports/
│       └── ArticleAIPort.java                         🆕 NOWY
│
└── infrastructure/
    ├── adapters/
    │   └── ai/                                        🆕 NOWY PAKIET
    │       ├── SpringAIArticleAdapter.java            🆕 NOWY
    │       ├── config/
    │       │   └── SpringAIConfiguration.java         🆕 NOWY
    │       └── service/
    │           └── MarkdownToHtmlConverter.java       🆕 NOWY
    ├── config/
    │   └── AIProperties.java                          🆕 NOWY
    └── web/
        ├── controller/
        │   └── ArticleAIController.java               🆕 NOWY
        └── dto/
            ├── GenerateArticleAIRequest.java          🆕 NOWY
            └── AIGeneratedContentResponse.java        🆕 NOWY
```

---

## 3. SZCZEGÓŁOWA IMPLEMENTACJA - BACKEND

### 3.1. Command (Application Layer)

**Plik:** `application/article/command/GenerateArticleWithAICommand.java`

```java
package pl.klastbit.lexpage.application.article.command;

public record GenerateArticleWithAICommand(
    String userPrompt
) {
    public GenerateArticleWithAICommand {
        if (userPrompt == null || userPrompt.isBlank()) {
            throw new IllegalArgumentException("User prompt cannot be blank");
        }
        if (userPrompt.length() > 1000) {
            throw new IllegalArgumentException("User prompt cannot exceed 1000 characters");
        }
    }
}
```

**Konwencje:**
- ✅ Record (immutable)
- ✅ Walidacja w compact constructor
- ✅ Javadoc

### 3.2. DTO (Application Layer)

**Plik:** `application/article/dto/AIGeneratedContentDto.java`

```java
package pl.klastbit.lexpage.application.article.dto;

public record AIGeneratedContentDto(
    String title,
    String content  // HTML content (converted from Markdown)
) {
    public static AIGeneratedContentDto of(String title, String content) {
        return new AIGeneratedContentDto(title, content);
    }
}
```

### 3.3. Exception (Application Layer)

**Plik:** `application/article/exception/AIGenerationException.java`

```java
package pl.klastbit.lexpage.application.article.exception;

public class AIGenerationException extends RuntimeException {
    public AIGenerationException(String message) {
        super(message);
    }

    public AIGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 3.4. Port Interface (Application Layer)

**Plik:** `application/ports/ArticleAIPort.java`

```java
package pl.klastbit.lexpage.application.ports;

import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;

/**
 * Outbound port for AI article generation.
 * Infrastructure layer provides the implementation.
 */
public interface ArticleAIPort {

    /**
     * Generates article content based on user's prompt.
     *
     * @param userPrompt User's description of what article to generate
     * @return AI-generated title and HTML content
     * @throws AIGenerationException if generation fails
     */
    AIGeneratedContentDto generateArticleContent(String userPrompt);
}
```

### 3.5. Use Case (Application Layer)

**Plik:** `application/article/GenerateArticleWithAIUseCase.java` (interface)

```java
package pl.klastbit.lexpage.application.article;

import pl.klastbit.lexpage.application.article.command.GenerateArticleWithAICommand;
import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;

public interface GenerateArticleWithAIUseCase {
    AIGeneratedContentDto execute(GenerateArticleWithAICommand command);
}
```

**Plik:** `application/article/usecase/GenerateArticleWithAIUseCaseImpl.java`

```java
package pl.klastbit.lexpage.application.article.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.klastbit.lexpage.application.article.GenerateArticleWithAIUseCase;
import pl.klastbit.lexpage.application.article.command.GenerateArticleWithAICommand;
import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;
import pl.klastbit.lexpage.application.article.exception.AIGenerationException;
import pl.klastbit.lexpage.application.ports.ArticleAIPort;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateArticleWithAIUseCaseImpl implements GenerateArticleWithAIUseCase {

    private final ArticleAIPort articleAIPort;

    @Override
    public AIGeneratedContentDto execute(GenerateArticleWithAICommand command) {
        log.info("Generating article with AI for prompt: {}",
            command.userPrompt().substring(0, Math.min(50, command.userPrompt().length())));

        try {
            AIGeneratedContentDto result = articleAIPort.generateArticleContent(command.userPrompt());

            log.info("AI generation successful. Title: {}, Content length: {}",
                result.title(), result.content().length());

            return result;

        } catch (Exception e) {
            log.error("AI generation failed: {}", e.getMessage(), e);
            throw new AIGenerationException("Failed to generate article: " + e.getMessage(), e);
        }
    }
}
```

### 3.6. Configuration Properties (Infrastructure)

**Plik:** `infrastructure/config/AIProperties.java`

```java
package pl.klastbit.lexpage.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.ai")
@Getter
@Setter
public class AIProperties {
    private String apiKey;
    private String modelName;
    private Double temperature = 0.7;
    private Integer maxTokens = 2000;
}
```

**application.properties:**
```properties
# AI Configuration
app.ai.api-key=${OPENROUTER_API_KEY:your-api-key-here}
app.ai.model-name=openai/gpt-4
app.ai.temperature=0.7
app.ai.max-tokens=2000
```

### 3.7. Spring AI Configuration (Infrastructure)

**Plik:** `infrastructure/adapters/ai/config/SpringAIConfiguration.java`

```java
package pl.klastbit.lexpage.infrastructure.adapters.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.klastbit.lexpage.infrastructure.config.AIProperties;

@Configuration
@RequiredArgsConstructor
public class SpringAIConfiguration {

    private final AIProperties aiProperties;

    @Bean
    public OpenAiApi openRouterApi() {
        return new OpenAiApi("https://openrouter.ai/api/v1", aiProperties.getApiKey());
    }

    @Bean
    public OpenAiChatModel chatModel(OpenAiApi openRouterApi) {
        return new OpenAiChatModel(openRouterApi,
            OpenAiChatOptions.builder()
                .withModel(aiProperties.getModelName())
                .withTemperature(aiProperties.getTemperature())
                .withMaxTokens(aiProperties.getMaxTokens())
                .build()
        );
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
```

### 3.8. Markdown → HTML Converter (Infrastructure)

**Plik:** `infrastructure/adapters/ai/service/MarkdownToHtmlConverter.java`

```java
package pl.klastbit.lexpage.infrastructure.adapters.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MarkdownToHtmlConverter {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownToHtmlConverter() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    public String convert(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }

        try {
            Node document = parser.parse(markdown);
            String html = renderer.render(document);

            log.debug("Converted Markdown to HTML. Input length: {}, Output length: {}",
                markdown.length(), html.length());

            return html;

        } catch (Exception e) {
            log.error("Failed to convert Markdown to HTML", e);
            throw new RuntimeException("Markdown conversion failed", e);
        }
    }
}
```

### 3.9. AI Adapter (Infrastructure) - GŁÓWNA LOGIKA

**Plik:** `infrastructure/adapters/ai/SpringAIArticleAdapter.java`

```java
package pl.klastbit.lexpage.infrastructure.adapters.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;
import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;
import pl.klastbit.lexpage.application.article.exception.AIGenerationException;
import pl.klastbit.lexpage.application.ports.ArticleAIPort;
import pl.klastbit.lexpage.infrastructure.adapters.ai.service.MarkdownToHtmlConverter;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringAIArticleAdapter implements ArticleAIPort {

    private final ChatClient chatClient;
    private final MarkdownToHtmlConverter markdownConverter;

    private static final String SYSTEM_PROMPT = """
        You are a professional Polish content writer.
        Generate a blog article based on the user's request.

        Return ONLY a JSON object with this structure:
        {
          "title": "Article title in Polish",
          "content": "Article content in Markdown format"
        }

        Guidelines:
        - Title: max 100 characters, engaging and SEO-friendly
        - Content: 500-2000 words, well-structured with headings
        - Use Markdown formatting (##, ###, **, *, lists)
        - Write in Polish language
        - Professional, informative tone
        - Include introduction, body with sections, conclusion
        """;

    @Override
    public AIGeneratedContentDto generateArticleContent(String userPrompt) {
        log.info("Generating article with AI. Prompt length: {}", userPrompt.length());

        try {
            var outputConverter = new BeanOutputConverter<>(AIResponse.class);
            String format = outputConverter.getFormat();
            String fullPrompt = SYSTEM_PROMPT + "\n\nUser request: " + userPrompt +
                "\n\nFormat: " + format;

            String response = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();

            log.debug("AI raw response: {}", response);

            AIResponse aiResponse = outputConverter.convert(response);

            if (aiResponse == null || aiResponse.title() == null || aiResponse.content() == null) {
                throw new AIGenerationException("AI returned invalid response structure");
            }

            String htmlContent = markdownConverter.convert(aiResponse.content());

            log.info("AI generation successful. Title: {}, HTML length: {}",
                aiResponse.title(), htmlContent.length());

            return AIGeneratedContentDto.of(aiResponse.title(), htmlContent);

        } catch (AIGenerationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during AI generation", e);
            throw new AIGenerationException("AI generation failed: " + e.getMessage(), e);
        }
    }

    private record AIResponse(
        @JsonProperty("title") String title,
        @JsonProperty("content") String content
    ) {}
}
```

### 3.10. REST Controller (Infrastructure)

**Plik:** `infrastructure/web/controller/ArticleAIController.java`

```java
package pl.klastbit.lexpage.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.klastbit.lexpage.application.article.GenerateArticleWithAIUseCase;
import pl.klastbit.lexpage.application.article.command.GenerateArticleWithAICommand;
import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;
import pl.klastbit.lexpage.infrastructure.web.controller.dto.AIGeneratedContentResponse;
import pl.klastbit.lexpage.infrastructure.web.controller.dto.GenerateArticleAIRequest;

@RestController
@RequestMapping("/api/articles/ai")
@RequiredArgsConstructor
@Slf4j
public class ArticleAIController {

    private final GenerateArticleWithAIUseCase generateArticleWithAIUseCase;

    @PostMapping("/generate")
    public ResponseEntity<AIGeneratedContentResponse> generateArticle(
        @Valid @RequestBody GenerateArticleAIRequest request
    ) {
        log.info("POST /api/articles/ai/generate - prompt length: {}", request.userPrompt().length());

        GenerateArticleWithAICommand command = new GenerateArticleWithAICommand(request.userPrompt());
        AIGeneratedContentDto result = generateArticleWithAIUseCase.execute(command);

        return ResponseEntity.ok(AIGeneratedContentResponse.from(result));
    }
}
```

**Plik:** `infrastructure/web/controller/dto/GenerateArticleAIRequest.java`

```java
package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenerateArticleAIRequest(
    @NotBlank(message = "Prompt jest wymagany")
    @Size(max = 1000, message = "Prompt nie może przekraczać 1000 znaków")
    String userPrompt
) {}
```

**Plik:** `infrastructure/web/controller/dto/AIGeneratedContentResponse.java`

```java
package pl.klastbit.lexpage.infrastructure.web.controller.dto;

import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;

public record AIGeneratedContentResponse(
    String title,
    String content
) {
    public static AIGeneratedContentResponse from(AIGeneratedContentDto dto) {
        return new AIGeneratedContentResponse(dto.title(), dto.content());
    }
}
```

### 3.11. Global Exception Handler Update

Dodaj do istniejącego `GlobalExceptionApiHandler.java`:

```java
@ExceptionHandler(AIGenerationException.class)
public ProblemDetail handleAIGenerationException(AIGenerationException ex) {
    log.warn("AI generation failed: {}", ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.SERVICE_UNAVAILABLE,
        "Nie udało się wygenerować artykułu przez AI. Spróbuj ponownie."
    );

    problemDetail.setTitle("AI Generation Failed");
    problemDetail.setType(URI.create("https://klastbit.pl/errors/ai-generation-failed"));
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
}
```

---

## 4. SZCZEGÓŁOWA IMPLEMENTACJA - FRONTEND

### 4.1. Przycisk "Generuj z AI" w formularzu

W `templates/pages/admin/blogs/form.html`, dodaj po linii 65 (przed Title Field):

```html
<!-- AI Generation Button -->
<div class="mb-6 p-4 bg-gradient-to-r from-purple-50 to-blue-50 rounded-lg border border-purple-200">
    <div class="flex items-center justify-between">
        <div class="flex items-center">
            <i class="material-icons text-purple-600 mr-2">auto_awesome</i>
            <div>
                <h3 class="text-sm font-medium text-gray-900">Wygeneruj artykuł z pomocą AI</h3>
                <p class="text-xs text-gray-600 mt-0.5">Opisz temat, a AI stworzy dla Ciebie kompletny artykuł</p>
            </div>
        </div>
        <button type="button"
                onclick="openAIModal()"
                class="px-4 py-2 bg-purple-600 text-white text-sm font-medium rounded-lg hover:bg-purple-700 transition-colors inline-flex items-center"
                data-ripple-light="true">
            <i class="material-icons mr-1 text-base">auto_awesome</i>
            Generuj z AI
        </button>
    </div>
</div>
```

### 4.2. Modal HTML Structure

Dodaj przed zamknięciem fragmentu content (przed `</div>` na linii 601):

```html
<!-- AI Generation Modal -->
<div id="aiGenerateModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full hidden z-50">
    <div class="relative top-20 mx-auto p-5 border w-full max-w-2xl shadow-lg rounded-xl bg-white">
        <div class="mt-3">
            <!-- Modal Header -->
            <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg leading-6 font-medium text-gray-900">
                    <i class="material-icons align-middle mr-2">auto_awesome</i>
                    Generuj Artykuł z AI
                </h3>
                <button onclick="closeAIModal()"
                        class="text-gray-400 hover:text-gray-600 transition-colors">
                    <i class="material-icons">close</i>
                </button>
            </div>

            <!-- Modal Body -->
            <div class="px-4 py-3">
                <label for="aiPromptInput" class="block text-sm font-medium text-gray-700 mb-2">
                    Opisz jaki artykuł chcesz wygenerować
                </label>
                <textarea id="aiPromptInput"
                          rows="4"
                          maxlength="1000"
                          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 resize-none"
                          placeholder="Np. 'Artykuł o najlepszych praktykach w Spring Boot 3.0...'"></textarea>
                <div class="mt-1 flex justify-between items-center">
                    <p id="aiPromptError" class="text-sm text-red-600 hidden"></p>
                    <p class="text-sm text-gray-500">
                        <span id="aiPromptCount">0</span>/1000
                    </p>
                </div>

                <!-- Loading State -->
                <div id="aiLoadingState" class="mt-4 p-4 bg-blue-50 rounded-lg hidden">
                    <div class="flex items-center">
                        <div class="animate-spin rounded-full h-5 w-5 border-b-2 border-primary-600 mr-3"></div>
                        <span class="text-sm text-gray-700">Generuję artykuł... To może potrwać do 30 sekund.</span>
                    </div>
                </div>

                <!-- AI Response Preview -->
                <div id="aiResponsePreview" class="mt-4 hidden">
                    <div class="border-t border-gray-200 pt-4">
                        <h4 class="text-sm font-medium text-gray-900 mb-2">Wygenerowana treść:</h4>
                        <div class="mb-3">
                            <label class="block text-xs font-medium text-gray-600 mb-1">Tytuł:</label>
                            <div id="aiGeneratedTitle"
                                 class="px-3 py-2 bg-gray-50 rounded border border-gray-200 text-sm font-medium"></div>
                        </div>
                        <div>
                            <label class="block text-xs font-medium text-gray-600 mb-1">Treść (pierwsze 200 znaków):</label>
                            <div id="aiGeneratedContentPreview"
                                 class="px-3 py-2 bg-gray-50 rounded border border-gray-200 text-sm text-gray-700 max-h-24 overflow-y-auto"></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Modal Footer -->
            <div class="flex gap-3 px-4 py-3 border-t border-gray-200">
                <button id="aiGenerateBtn"
                        onclick="generateWithAI()"
                        class="flex-1 px-4 py-2 bg-primary-600 text-white text-base font-medium rounded-lg hover:bg-primary-700 transition-colors"
                        data-ripple-light="true">
                    <i class="material-icons align-middle mr-1">auto_awesome</i>
                    Generuj
                </button>

                <button id="aiUseGeneratedBtn"
                        onclick="useGeneratedContent()"
                        class="flex-1 px-4 py-2 bg-green-600 text-white text-base font-medium rounded-lg hover:bg-green-700 transition-colors hidden"
                        data-ripple-light="true">
                    <i class="material-icons align-middle mr-1">check</i>
                    Użyj tej treści
                </button>

                <button onclick="closeAIModal()"
                        class="px-4 py-2 bg-gray-200 text-gray-800 text-base font-medium rounded-lg hover:bg-gray-300 transition-colors"
                        data-ripple-light="true">
                    Anuluj
                </button>
            </div>
        </div>
    </div>
</div>
```

### 4.3. JavaScript Logic

Dodaj przed zamknięciem `</script>` na końcu form.html:

```javascript
// ==================== AI GENERATION LOGIC ====================

let aiGeneratedData = null;

function openAIModal() {
    document.getElementById('aiGenerateModal').classList.remove('hidden');
    document.getElementById('aiPromptInput').focus();
}

function closeAIModal() {
    document.getElementById('aiGenerateModal').classList.add('hidden');
    document.getElementById('aiPromptInput').value = '';
    document.getElementById('aiPromptCount').textContent = '0';
    document.getElementById('aiLoadingState').classList.add('hidden');
    document.getElementById('aiResponsePreview').classList.add('hidden');
    document.getElementById('aiGenerateBtn').classList.remove('hidden');
    document.getElementById('aiUseGeneratedBtn').classList.add('hidden');
    aiGeneratedData = null;
}

document.getElementById('aiPromptInput').addEventListener('input', function() {
    const length = this.value.length;
    document.getElementById('aiPromptCount').textContent = length;

    if (length > 1000) {
        showAIPromptError('Prompt nie może przekraczać 1000 znaków');
    } else {
        hideAIPromptError();
    }
});

async function generateWithAI() {
    const promptInput = document.getElementById('aiPromptInput');
    const prompt = promptInput.value.trim();

    if (!prompt) {
        showAIPromptError('Prompt jest wymagany');
        return;
    }

    if (prompt.length > 1000) {
        showAIPromptError('Prompt nie może przekraczać 1000 znaków');
        return;
    }

    hideAIPromptError();

    document.getElementById('aiLoadingState').classList.remove('hidden');
    document.getElementById('aiResponsePreview').classList.add('hidden');
    document.getElementById('aiGenerateBtn').disabled = true;

    try {
        const response = await fetch('/api/articles/ai/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userPrompt: prompt
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.detail || 'Nie udało się wygenerować artykułu');
        }

        aiGeneratedData = await response.json();
        showAIGeneratedPreview(aiGeneratedData);

    } catch (error) {
        console.error('AI generation error:', error);
        showAIPromptError('Błąd: ' + error.message);
    } finally {
        document.getElementById('aiLoadingState').classList.add('hidden');
        document.getElementById('aiGenerateBtn').disabled = false;
    }
}

function showAIGeneratedPreview(data) {
    document.getElementById('aiGeneratedTitle').textContent = data.title;

    const plainText = data.content.replace(/<[^>]+>/g, '');
    const preview = plainText.length > 200
        ? plainText.substring(0, 200) + '...'
        : plainText;
    document.getElementById('aiGeneratedContentPreview').textContent = preview;

    document.getElementById('aiResponsePreview').classList.remove('hidden');
    document.getElementById('aiGenerateBtn').classList.add('hidden');
    document.getElementById('aiUseGeneratedBtn').classList.remove('hidden');
}

function useGeneratedContent() {
    if (!aiGeneratedData) return;

    document.getElementById('titleInput').value = aiGeneratedData.title;
    document.getElementById('contentEditor').innerHTML = aiGeneratedData.content;

    updateCharCount('title', aiGeneratedData.title.length, 255);
    const contentText = aiGeneratedData.content.replace(/<[^>]+>/g, '');
    updateCharCount('content', contentText.length, 25000);

    validateField('title');
    validateField('content');

    closeAIModal();

    document.getElementById('articleForm').scrollIntoView({ behavior: 'smooth' });
    showSuccess('Treść wygenerowana przez AI została wstawiona do formularza');
}

function showAIPromptError(message) {
    const errorElement = document.getElementById('aiPromptError');
    errorElement.textContent = message;
    errorElement.classList.remove('hidden');
}

function hideAIPromptError() {
    document.getElementById('aiPromptError').classList.add('hidden');
}
```

---

## 5. GRADLE DEPENDENCIES

Dodaj do `build.gradle`:

```gradle
dependencies {
    // ... existing dependencies ...

    // Spring AI for OpenRouter integration
    implementation platform('org.springframework.ai:spring-ai-bom:1.0.0-M4')
    implementation 'org.springframework.ai:spring-ai-openai'

    // CommonMark for Markdown to HTML conversion
    implementation 'org.commonmark:commonmark:0.22.0'
}
```

**UWAGA:** Sprawdź Maven Central dla najnowszej wersji Spring AI!

---

## 6. USER FLOW

1. User otwiera `/admin/blogs/new`
2. User klika "Generuj z AI" → modal się otwiera
3. User wpisuje prompt (np. "Artykuł o Hexagonal Architecture")
4. User klika "Generuj" → loading state (30s)
5. AI zwraca tytuł + content → preview w modalu
6. User klika "Użyj tej treści" → pola formularza się wypełniają
7. User może edytować lub zapisać jako draft/publish

---

## 7. TRADE-OFFS I KLUCZOWE DECYZJE

### 7.1. Dlaczego NIE zapisujemy Article automatycznie?

**DECYZJA: Zwracamy tylko content, user musi kliknąć "Zapisz"**

✅ User review - user powinien zobaczyć content przed zapisem
✅ Edycja - user może poprawić AI content
✅ Kontrola - user decyduje draft/publish
✅ UX - jasny flow: generuj → przejrzyj → edytuj → zapisz

### 7.2. Structured Output vs Regex Parsing

**DECYZJA: Spring AI `BeanOutputConverter` + JSON**

✅ Type safety - Java Record
✅ Walidacja - auto exception gdy JSON invalid
✅ Maintainability - łatwe dodanie pól
✅ Framework support - built-in

### 7.3. Markdown → HTML w Backend vs Frontend

**DECYZJA: Backend konwertuje**

✅ Separacja - backend dostarcza gotowy HTML
✅ Reusability - możemy użyć gdzie indziej
✅ Security - backend kontroluje output
✅ Performance - CommonMark Java szybki

---

## 8. SEKWENCJA IMPLEMENTACJI (Kolejność kroków)

### Faza 1: Backend Foundation (6h)

1. **Gradle dependencies** (15min)
   - Dodaj Spring AI + CommonMark

2. **Configuration** (30min)
   - `AIProperties.java`
   - `application.properties`

3. **Application Layer** (2h)
   - `GenerateArticleWithAICommand.java`
   - `AIGeneratedContentDto.java`
   - `AIGenerationException.java`
   - `ArticleAIPort.java` (interface)
   - `GenerateArticleWithAIUseCase.java` (interface + impl)

4. **Infrastructure: Spring AI Setup** (1h)
   - `SpringAIConfiguration.java`

5. **Infrastructure: Markdown Converter** (30min)
   - `MarkdownToHtmlConverter.java`

6. **Infrastructure: AI Adapter** (2h)
   - `SpringAIArticleAdapter.java` (główna logika)

7. **Infrastructure: REST API** (1h)
   - `ArticleAIController.java`
   - `GenerateArticleAIRequest.java`
   - `AIGeneratedContentResponse.java`
   - Update `GlobalExceptionApiHandler.java`

### Faza 2: Frontend Implementation (5h)

8. **Modal HTML** (1h)
   - Dodaj przycisk "Generuj z AI"
   - Dodaj modal structure

9. **JavaScript Logic** (2h)
   - Modal open/close
   - Character counter
   - API call
   - Preview display
   - Use generated content

10. **Integration & Polish** (1h)
    - Form integration
    - Validation
    - Error handling
    - Loading states

11. **Testing & Refinement** (1h)
    - Manual testing
    - UI/UX adjustments
    - Error scenario testing

### Faza 3: Testing (4h)

12. **Unit Tests** (2h)
    - Use Case test
    - Markdown Converter test

13. **Integration Tests** (1h)
    - REST endpoint test

14. **Manual E2E Testing** (1h)
    - Full flow testing
    - Edge cases
    - Error scenarios

**TOTAL: ~15 hours**

---

## 9. CRITICAL FILES FOR IMPLEMENTATION

**Backend (Java):**

1. **`application/ports/ArticleAIPort.java`**
   - Reason: Core abstraction, defines AI integration contract

2. **`infrastructure/adapters/ai/SpringAIArticleAdapter.java`**
   - Reason: Main AI logic - Spring AI + OpenRouter + Markdown conversion

3. **`application/article/usecase/GenerateArticleWithAIUseCaseImpl.java`**
   - Reason: Use Case orchestration, business flow

4. **`infrastructure/web/controller/ArticleAIController.java`**
   - Reason: REST API endpoint, frontend integration

5. **`infrastructure/config/AIProperties.java`**
   - Reason: Configuration properties

**Frontend:**

6. **`templates/pages/admin/blogs/form.html`**
   - Reason: Modal HTML + JavaScript logic, całkowita funkcjonalność UI

**Configuration:**

7. **`build.gradle`**
   - Reason: Dependencies (Spring AI, CommonMark)

8. **`application.properties`**
   - Reason: AI configuration (API key, model)

---

## 10. PODSUMOWANIE

### Kluczowe Zalety Architektury:

✅ **Hexagonal Architecture** - czysty podział odpowiedzialności
✅ **Port/Adapter Pattern** - łatwa wymiana AI providera
✅ **Domain Independence** - domain nie wie o AI
✅ **Testability** - Use Case testuje się z mockowanym portem
✅ **User Control** - user przegląda AI content przed zapisem
✅ **Error Resilience** - proper exception handling
✅ **Extensibility** - łatwe dodanie features (multiple models, regenerate)

### Zgodność z Konwencjami Projektu:

✅ Lombok (`@RequiredArgsConstructor`, `@Slf4j`)
✅ Records dla DTOs/Commands
✅ Constructor injection
✅ Port/Adapter pattern
✅ Global exception handler z ProblemDetail
✅ SLF4J logging
✅ Material Tailwind modal design
✅ JavaScript w fragmentach content
✅ @ConfigurationProperties pattern

**Plan jest kompletny i gotowy do implementacji!** 🚀
