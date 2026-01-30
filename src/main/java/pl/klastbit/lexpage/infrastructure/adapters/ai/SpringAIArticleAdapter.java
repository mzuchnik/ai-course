package pl.klastbit.lexpage.infrastructure.adapters.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;
import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;
import pl.klastbit.lexpage.application.article.exception.AIGenerationException;
import pl.klastbit.lexpage.application.ports.ArticleAIPort;
import pl.klastbit.lexpage.infrastructure.adapters.ai.service.MarkdownToHtmlConverter;

/**
 * Spring AI adapter implementation for ArticleAIPort.
 * Integrates with OpenRouter API to generate article content using AI.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringAIArticleAdapter implements ArticleAIPort {

    private final ChatModel chatModel;
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

            String response = chatModel.call(new Prompt(fullPrompt))
                .getResult()
                .getOutput()
                .getText();

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
