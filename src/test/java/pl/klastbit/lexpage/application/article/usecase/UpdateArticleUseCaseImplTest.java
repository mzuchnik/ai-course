package pl.klastbit.lexpage.application.article.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.klastbit.lexpage.application.article.command.UpdateArticleCommand;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UpdateArticleUseCaseImpl.
 * Tests article update with slug regeneration.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateArticleUseCaseImpl Tests")
class UpdateArticleUseCaseImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private UpdateArticleUseCaseImpl useCase;

    private UserId testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UserId.createNew();
    }

    @Test
    @DisplayName("should update existing article")
    void shouldUpdateExistingArticle() {
        // given
        Long articleId = 1L;
        Article existingArticle = Article.createDraft(
                "Old Title",
                "old-slug",
                "Old content",
                null, null, null, null, null, null,
                testUserId
        );

        UpdateArticleCommand command = new UpdateArticleCommand(
                articleId,
                "New Title",
                "New content",
                "New excerpt",
                "New Meta Title",
                "New Meta Description",
                null, null,
                Arrays.asList("updated", "keywords"),
                testUserId
        );

        when(articleRepository.findByIdAndDeletedAtIsNull(articleId))
                .thenReturn(Optional.of(existingArticle));
        when(articleRepository.existsBySlugAndDeletedAtIsNull(any())).thenReturn(false);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ArticleDetailDto result = useCase.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("New Title");
        assertThat(result.content()).isEqualTo("New content");
        assertThat(result.excerpt()).isEqualTo("New excerpt");
        assertThat(result.metaTitle()).isEqualTo("New Meta Title");
        assertThat(result.keywords()).containsExactly("updated", "keywords");

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
        verify(articleRepository).save(existingArticle);
    }

    @Test
    @DisplayName("should regenerate slug when title changes")
    void shouldRegenerateSlugWhenTitleChanges() {
        // given
        Long articleId = 1L;
        Article existingArticle = Article.createDraft(
                "Old Title",
                "old-title",
                "Test content",
                null, null, null, null, null, null,
                testUserId
        );

        UpdateArticleCommand command = new UpdateArticleCommand(
                articleId,
                "Brand New Title",
                "Test content",
                null, null, null, null, null, null,
                testUserId
        );

        when(articleRepository.findByIdAndDeletedAtIsNull(articleId))
                .thenReturn(Optional.of(existingArticle));
        when(articleRepository.existsBySlugAndDeletedAtIsNull("brand-new-title")).thenReturn(false);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ArticleDetailDto result = useCase.execute(command);

        // then
        assertThat(result.slug()).isEqualTo("brand-new-title");
        verify(articleRepository).existsBySlugAndDeletedAtIsNull("brand-new-title");
    }

    @Test
    @DisplayName("should throw ArticleNotFoundException when article not found")
    void shouldThrowArticleNotFoundExceptionWhenArticleNotFound() {
        // given
        Long articleId = 999L;
        UpdateArticleCommand command = new UpdateArticleCommand(
                articleId,
                "Title", "Content", null, null, null, null, null, null,
                testUserId
        );

        when(articleRepository.findByIdAndDeletedAtIsNull(articleId))
                .thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ArticleNotFoundException.class)
                .hasMessageContaining("999");

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
        verify(articleRepository, never()).save(any(Article.class));
    }
}
