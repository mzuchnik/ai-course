package pl.klastbit.lexpage.application.article.command;

import pl.klastbit.lexpage.domain.user.UserId;

import java.util.List;

/**
 * Command for updating an existing article.
 * Immutable command object (Record) following CQRS pattern.
 */
public record UpdateArticleCommand(
        Long id,
        String title,
        String content,
        String excerpt,
        String metaTitle,
        String metaDescription,
        String ogImageUrl,
        String canonicalUrl,
        List<String> keywords,
        UserId updatedBy
) {
}
