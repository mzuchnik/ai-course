package pl.klastbit.lexpage.application.article.command;

import pl.klastbit.lexpage.domain.user.UserId;

import java.util.List;

/**
 * Command for creating a new draft article.
 * Immutable command object (Record) following CQRS pattern.
 */
public record CreateArticleCommand(
        String title,
        String content,
        String excerpt,
        String metaTitle,
        String metaDescription,
        String ogImageUrl,
        String canonicalUrl,
        List<String> keywords,
        UserId authorId,
        UserId createdBy,
        UserId updatedBy
) {
}
