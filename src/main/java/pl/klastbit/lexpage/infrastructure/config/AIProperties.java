package pl.klastbit.lexpage.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for AI article generation.
 * Properties are loaded from application.properties with prefix 'app.ai'.
 */
@Component
@ConfigurationProperties(prefix = "app.ai")
@Getter
@Setter
public class AIProperties {
    private String apiUrl;
    private String apiKey;
    private String modelName;
    private Double temperature = 0.7;
    private Integer maxTokens = 2000;
}
