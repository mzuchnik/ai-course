package pl.klastbit.lexpage.application.article.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;
import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Unit tests for PublishArticleUseCaseImpl.
 * Tests article publishing state transition.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PublishArticleUseCaseImpl Tests")
class PublishArticleUseCaseImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PublishArticleUseCaseImpl useCase;

    private UserId testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserId = UserId.createNew();
        testUser = User.ofExisting(testUserId, "testuser", Email.of("test@example.com"), "encoded_password", true);

        // Mock UserRepository to return test user (lenient for tests that don't use it)
        lenient().when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("should publish draft article")
    void shouldPublishDraftArticle() {
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
        ArticleDetailDto result = useCase.execute(articleId);

        // then
        assertThat(result.status()).isEqualTo(ArticleStatus.PUBLISHED);
        assertThat(result.publishedAt()).isNotNull();

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
        verify(articleRepository).save(article);
    }

    @Test
    @DisplayName("should throw exception when trying to publish already published article")
    void shouldThrowExceptionWhenTryingToPublishAlreadyPublishedArticle() {
        // given
        Long articleId = 1L;
        Article article = Article.createDraft(
                "Test Title",
                "test-slug",
                "Test content",
                null, null, null, null, null, null,
                testUserId
        );
        article.publish(); // Already published

        when(articleRepository.findByIdAndDeletedAtIsNull(articleId))
                .thenReturn(Optional.of(article));

        // when/then
        assertThatThrownBy(() -> useCase.execute(articleId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already published");

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
        verify(articleRepository, never()).save(any(Article.class));
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
                .isInstanceOf(ArticleNotFoundException.class);

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
    }
}
