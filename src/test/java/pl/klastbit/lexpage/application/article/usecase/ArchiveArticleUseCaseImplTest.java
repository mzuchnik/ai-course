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
 * Unit tests for ArchiveArticleUseCaseImpl.
 * Tests article archiving state transition.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ArchiveArticleUseCaseImpl Tests")
class ArchiveArticleUseCaseImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArchiveArticleUseCaseImpl useCase;

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
    @DisplayName("should archive published article")
    void shouldArchivePublishedArticle() {
        // given
        Long articleId = 1L;
        Article article = Article.createDraft(
                "Test Title",
                "test-slug",
                "Test content",
                null, null, null, null, null, null,
                testUserId
        );
        article.publish(); // Make it published first

        when(articleRepository.findByIdAndDeletedAtIsNull(articleId))
                .thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ArticleDetailDto result = useCase.execute(articleId);

        // then
        assertThat(result.status()).isEqualTo(ArticleStatus.ARCHIVED);
        assertThat(result.publishedAt()).isNotNull(); // Should remain

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
        verify(articleRepository).save(article);
    }

    @Test
    @DisplayName("should throw exception when trying to archive draft article")
    void shouldThrowExceptionWhenTryingToArchiveDraftArticle() {
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

        // when/then
        assertThatThrownBy(() -> useCase.execute(articleId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot archive article that is not in PUBLISHED status");

        verify(articleRepository).findByIdAndDeletedAtIsNull(articleId);
        verify(articleRepository, never()).save(any(Article.class));
    }
}
