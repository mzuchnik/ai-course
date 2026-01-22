package pl.klastbit.lexpage.domain.ai;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * AIGeneration Domain Entity.
 * Encapsulates AI content generation business logic including tracking and limits.
 */
public class AIGeneration {

    private Long id;
    private Long userId;
    private String prompt;
    private String keywords;
    private Integer wordCount;
    private String generatedContent;
    private String model;
    private Integer tokensUsed;
    private Integer generationTimeMs;
    private GenerationStatus status;
    private String errorMessage;
    private Long articleId;
    private LocalDateTime createdAt;

    public static final int DAILY_GENERATION_LIMIT = 20;
    public static final int MAX_WORD_COUNT = 5000;

    private AIGeneration() {}

    /**
     * Factory method to create a successful generation.
     */
    public static AIGeneration createSuccessful(
            Long userId,
            String prompt,
            String keywords,
            String generatedContent,
            String model,
            Integer tokensUsed,
            Integer generationTimeMs
    ) {
        AIGeneration generation = new AIGeneration();
        generation.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        generation.prompt = Objects.requireNonNull(prompt, "Prompt cannot be null");
        generation.keywords = keywords;
        generation.generatedContent = Objects.requireNonNull(generatedContent, "Generated content cannot be null");
        generation.model = Objects.requireNonNull(model, "Model cannot be null");
        generation.tokensUsed = tokensUsed;
        generation.generationTimeMs = generationTimeMs;
        generation.status = GenerationStatus.SUCCESS;
        generation.wordCount = calculateWordCount(generatedContent);
        generation.createdAt = LocalDateTime.now();

        generation.validateWordCount();

        return generation;
    }

    /**
     * Factory method to create a failed generation.
     */
    public static AIGeneration createFailed(
            Long userId,
            String prompt,
            String keywords,
            String model,
            String errorMessage,
            GenerationStatus status
    ) {
        if (status == GenerationStatus.SUCCESS) {
            throw new IllegalArgumentException("Cannot create failed generation with SUCCESS status");
        }

        AIGeneration generation = new AIGeneration();
        generation.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        generation.prompt = Objects.requireNonNull(prompt, "Prompt cannot be null");
        generation.keywords = keywords;
        generation.model = Objects.requireNonNull(model, "Model cannot be null");
        generation.errorMessage = errorMessage;
        generation.status = status;
        generation.generatedContent = ""; // Empty for failed generations
        generation.createdAt = LocalDateTime.now();

        return generation;
    }

    /**
     * Factory method to reconstruct an existing AI generation from database.
     * Used by infrastructure layer mappers. No business validation applied.
     */
    public static AIGeneration ofExisting(
            Long id,
            Long userId,
            String prompt,
            String keywords,
            Integer wordCount,
            String generatedContent,
            String model,
            Integer tokensUsed,
            Integer generationTimeMs,
            GenerationStatus status,
            String errorMessage,
            Long articleId,
            LocalDateTime createdAt
    ) {
        AIGeneration generation = new AIGeneration();
        generation.id = id;
        generation.userId = userId;
        generation.prompt = prompt;
        generation.keywords = keywords;
        generation.wordCount = wordCount;
        generation.generatedContent = generatedContent;
        generation.model = model;
        generation.tokensUsed = tokensUsed;
        generation.generationTimeMs = generationTimeMs;
        generation.status = status;
        generation.errorMessage = errorMessage;
        generation.articleId = articleId;
        generation.createdAt = createdAt;
        return generation;
    }

    /**
     * Links this generation to an article.
     */
    public void linkToArticle(Long articleId) {
        if (this.status != GenerationStatus.SUCCESS) {
            throw new IllegalStateException("Cannot link failed generation to article");
        }
        this.articleId = Objects.requireNonNull(articleId, "Article ID cannot be null");
    }

    /**
     * Checks if generation was successful.
     */
    public boolean isSuccessful() {
        return status == GenerationStatus.SUCCESS;
    }

    /**
     * Checks if generation failed.
     */
    public boolean isFailed() {
        return status == GenerationStatus.FAILED;
    }

    /**
     * Checks if generation timed out.
     */
    public boolean isTimedOut() {
        return status == GenerationStatus.TIMEOUT;
    }

    /**
     * Checks if this generation is linked to an article.
     */
    public boolean isLinkedToArticle() {
        return articleId != null;
    }

    /**
     * Validates that word count does not exceed the maximum.
     */
    private void validateWordCount() {
        if (wordCount != null && wordCount > MAX_WORD_COUNT) {
            throw new IllegalArgumentException(
                "Generated content exceeds maximum word count of " + MAX_WORD_COUNT
            );
        }
    }

    /**
     * Calculates word count from content.
     */
    private static int calculateWordCount(String content) {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getKeywords() {
        return keywords;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public String getGeneratedContent() {
        return generatedContent;
    }

    public String getModel() {
        return model;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public Integer getGenerationTimeMs() {
        return generationTimeMs;
    }

    public GenerationStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Long getArticleId() {
        return articleId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters for infrastructure layer (reconstruction from DB)

    public void setId(Long id) {
        this.id = id;
    }

    void setStatus(GenerationStatus status) {
        this.status = status;
    }

    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
