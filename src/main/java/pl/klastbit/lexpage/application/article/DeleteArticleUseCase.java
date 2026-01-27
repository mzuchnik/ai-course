package pl.klastbit.lexpage.application.article;

/**
 * Use case for soft-deleting an article.
 * Inbound port in Hexagonal Architecture.
 */
public interface DeleteArticleUseCase {

    /**
     * Soft-deletes an article by setting deletedAt timestamp.
     *
     * @param articleId ID of the article to delete
     */
    void execute(Long articleId);
}
