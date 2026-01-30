package pl.klastbit.lexpage.infrastructure.adapters.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI configuration for OpenRouter integration.
 * Manually configures ChatModel bean with OpenRouter endpoint.
 */
@Configuration
public class SpringAIConfiguration {

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    @Value("${spring.ai.openai.chat.options.temperature}")
    private Double temperature;

    @Value("${spring.ai.openai.chat.options.max-tokens}")
    private Integer maxTokens;

    @Bean
    public ChatModel chatModel() {
        // Use OpenAiApi builder
        var api = OpenAiApi.builder()
            .baseUrl(baseUrl)
            .apiKey(() -> apiKey)
            .completionsPath("/chat/completions")  // baseUrl already contains /api/v1
            .embeddingsPath("/embeddings")          // baseUrl already contains /api/v1
            .build();

        var options = OpenAiChatOptions.builder()
            .model(model)
            .temperature(temperature)
            .maxTokens(maxTokens)
            .build();

        // Spring AI 2.x uses builder pattern
        return OpenAiChatModel.builder()
            .openAiApi(api)
            .defaultOptions(options)
            .build();
    }
}
