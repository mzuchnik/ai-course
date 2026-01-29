package pl.klastbit.lexpage.application.article.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.klastbit.lexpage.application.article.dto.ArticleListItemDto;
import pl.klastbit.lexpage.application.article.dto.PageDto;
import pl.klastbit.lexpage.application.user.ports.UserRepository;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.user.Email;
import pl.klastbit.lexpage.domain.user.User;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

/**
 * Unit tests for ListArticlesUseCaseImpl.
 * Tests article listing with filtering, pagination, and search.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ListArticlesUseCaseImpl Tests")
class ListArticlesUseCaseImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListArticlesUseCaseImpl useCase;

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
    @DisplayName("should list all articles with pagination")
    void shouldListAllArticlesWithPagination() {
        // given
        Article article1 = createTestArticle("Title 1", "slug-1");
        Article article2 = createTestArticle("Title 2", "slug-2");
        List<Article> articles = Arrays.asList(article1, article2);
        Page<Article> page = new PageImpl<>(articles, PageRequest.of(0, 10), 2);

        when(articleRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(page);

        // when
        PageDto<ArticleListItemDto> result = useCase.execute(
                null, null, null, PageRequest.of(0, 10)
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.page().number()).isEqualTo(0);
        assertThat(result.page().size()).isEqualTo(10);
        assertThat(result.page().totalElements()).isEqualTo(2);

        verify(articleRepository).findAllByDeletedAtIsNull(any(Pageable.class));
    }

    @Test
    @DisplayName("should list articles filtered by status")
    void shouldListArticlesFilteredByStatus() {
        // given
        Article article = createTestArticle("Published Article", "published-article");
        List<Article> articles = Collections.singletonList(article);
        Page<Article> page = new PageImpl<>(articles, PageRequest.of(0, 10), 1);

        when(articleRepository.findAllByStatusAndDeletedAtIsNull(
                eq(ArticleStatus.PUBLISHED), any(Pageable.class)
        )).thenReturn(page);

        // when
        PageDto<ArticleListItemDto> result = useCase.execute(
                ArticleStatus.PUBLISHED, null, null, PageRequest.of(0, 10)
        );

        // then
        assertThat(result.content()).hasSize(1);
        verify(articleRepository).findAllByStatusAndDeletedAtIsNull(
                eq(ArticleStatus.PUBLISHED), any(Pageable.class)
        );
    }

    @Test
    @DisplayName("should list articles filtered by author")
    void shouldListArticlesFilteredByAuthor() {
        // given
        UserId authorId = UserId.createNew();
        Article article = createTestArticle("Article by Author", "article-by-author");
        List<Article> articles = Collections.singletonList(article);
        Page<Article> page = new PageImpl<>(articles, PageRequest.of(0, 10), 1);

        when(articleRepository.findAllByAuthorIdAndDeletedAtIsNull(
                eq(authorId), any(Pageable.class)
        )).thenReturn(page);

        // when
        PageDto<ArticleListItemDto> result = useCase.execute(
                null, authorId, null, PageRequest.of(0, 10)
        );

        // then
        assertThat(result.content()).hasSize(1);
        verify(articleRepository).findAllByAuthorIdAndDeletedAtIsNull(
                eq(authorId), any(Pageable.class)
        );
    }

    @Test
    @DisplayName("should list articles filtered by status and author")
    void shouldListArticlesFilteredByStatusAndAuthor() {
        // given
        UserId authorId = UserId.createNew();
        Article article = createTestArticle("Article", "article");
        List<Article> articles = Collections.singletonList(article);
        Page<Article> page = new PageImpl<>(articles, PageRequest.of(0, 10), 1);

        when(articleRepository.findAllByStatusAndAuthorIdAndDeletedAtIsNull(
                eq(ArticleStatus.PUBLISHED), eq(authorId), any(Pageable.class)
        )).thenReturn(page);

        // when
        PageDto<ArticleListItemDto> result = useCase.execute(
                ArticleStatus.PUBLISHED, authorId, null, PageRequest.of(0, 10)
        );

        // then
        assertThat(result.content()).hasSize(1);
        verify(articleRepository).findAllByStatusAndAuthorIdAndDeletedAtIsNull(
                eq(ArticleStatus.PUBLISHED), eq(authorId), any(Pageable.class)
        );
    }

    @Test
    @DisplayName("should search articles by keyword")
    void shouldSearchArticlesByKeyword() {
        // given
        String keyword = "search term";
        Article article = createTestArticle("Article with search term", "article-search");
        List<Article> articles = Collections.singletonList(article);
        Page<Article> page = new PageImpl<>(articles, PageRequest.of(0, 10), 1);

        when(articleRepository.searchByKeywordAndDeletedAtIsNull(
                eq(keyword), any(Pageable.class)
        )).thenReturn(page);

        // when
        PageDto<ArticleListItemDto> result = useCase.execute(
                null, null, keyword, PageRequest.of(0, 10)
        );

        // then
        assertThat(result.content()).hasSize(1);
        verify(articleRepository).searchByKeywordAndDeletedAtIsNull(
                eq(keyword), any(Pageable.class)
        );
    }

    @Test
    @DisplayName("should return empty list when no articles found")
    void shouldReturnEmptyListWhenNoArticlesFound() {
        // given
        Page<Article> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(articleRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(emptyPage);

        // when
        PageDto<ArticleListItemDto> result = useCase.execute(
                null, null, null, PageRequest.of(0, 10)
        );

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.page().totalElements()).isEqualTo(0);
    }

    // Helper method
    private Article createTestArticle(String title, String slug) {
        return Article.createDraft(
                title,
                slug,
                "Test content for article",
                "Test excerpt",
                null, null, null, null, null,
                testUserId
        );
    }
}
