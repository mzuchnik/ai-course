package pl.klastbit.lexpage.application.article.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DeleteArticleUseCaseImpl.
 * Tests article soft delete functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteArticleUseCaseImpl Tests")
class DeleteArticleUseCaseImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private DeleteArticleUseCaseImpl useCase;

    private UserId testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UserId.createNew();
    }

    @Test
    @DisplayName("should soft delete article")
    void shouldSoftDeleteArticle() {
        // given
        Long articleId = 1L;
        Article article = Article.createDraft(
                "Test Title",
                "test-slug",
                "Test content",
                null, null, null, null, null, null,
                testUserId
        );

        when(articleRepository.findByIdAndDeletedAtIsNull(articleId))
                .thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        useCase.execute(articleId);

        // then
        assertThat(article.isDeleted()).isTrue();
        assertThat(article.getDeletedAt()).isNotNull();

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
        verify(articleRepository).save(article);
    }

    @Test
    @DisplayName("should throw ArticleNotFoundException when article not found")
    void shouldThrowArticleNotFoundExceptionWhenArticleNotFound() {
        // given
        Long articleId = 999L;

        when(articleRepository.findByIdAndDeletedAtIsNull(articleId))
                .thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> useCase.execute(articleId))
                .isInstanceOf(ArticleNotFoundException.class)
                .hasMessageContaining("999");

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
        verify(articleRepository, never()).save(any(Article.class));
    }
}
