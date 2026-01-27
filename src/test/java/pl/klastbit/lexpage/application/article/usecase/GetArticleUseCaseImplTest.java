package pl.klastbit.lexpage.application.article.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GetArticleUseCaseImpl.
 * Tests article retrieval by ID.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetArticleUseCaseImpl Tests")
class GetArticleUseCaseImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private GetArticleUseCaseImpl useCase;

    private UserId testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UserId.createNew();
    }

    @Test
    @DisplayName("should return article by ID")
    void shouldReturnArticleById() {
        // given
        Long articleId = 1L;
        Article article = Article.createDraft(
                "Test Title",
                "test-slug",
                "Test content",
                "Test excerpt",
                "Meta Title",
                "Meta Description",
                null, null,
                Arrays.asList("test", "keywords"),
                testUserId
        );

        when(articleRepository.findByIdAndDeletedAtIsNull(articleId))
                .thenReturn(Optional.of(article));

        // when
        ArticleDetailDto result = useCase.execute(articleId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Test Title");
        assertThat(result.slug()).isEqualTo("test-slug");
        assertThat(result.content()).isEqualTo("Test content");
        assertThat(result.excerpt()).isEqualTo("Test excerpt");

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
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
    }
}
