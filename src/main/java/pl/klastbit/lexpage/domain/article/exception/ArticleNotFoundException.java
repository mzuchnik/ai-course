package pl.klastbit.lexpage.domain.article.exception;

/**
 * Exception thrown when an article is not found.
 * Domain exception in the article bounded context.
 */
public class ArticleNotFoundException extends RuntimeException {

    private final Long articleId;

    public ArticleNotFoundException(Long articleId) {
        super("Article not found with ID: " + articleId);
        this.articleId = articleId;
    }

    public ArticleNotFoundException(String message) {
        super(message);
        this.articleId = null;
    }

    public Long getArticleId() {
        return articleId;
    }
}
