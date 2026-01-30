package pl.klastbit.lexpage.application.article.command;

/**
 * Command to generate article content using AI.
 * Contains user's natural language prompt describing the desired article.
 */
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
