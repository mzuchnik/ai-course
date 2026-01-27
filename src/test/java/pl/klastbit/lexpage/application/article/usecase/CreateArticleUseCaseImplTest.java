package pl.klastbit.lexpage.application.article.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.klastbit.lexpage.application.article.command.CreateArticleCommand;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.domain.article.Article;
import pl.klastbit.lexpage.domain.article.ArticleRepository;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.user.UserId;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateArticleUseCaseImpl.
 * Tests article creation with slug generation and meta description auto-generation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateArticleUseCaseImpl Tests")
class CreateArticleUseCaseImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private CreateArticleUseCaseImpl useCase;

    private UserId testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UserId.createNew();
    }

    @Test
    @DisplayName("should create draft article with generated slug")
    void shouldCreateDraftArticleWithGeneratedSlug() {
        // given
        CreateArticleCommand command = new CreateArticleCommand(
                "Test Article Title",
                "Test content for the article",
                "Test excerpt",
                "Meta Title",
                null,  // metaDescription - should be auto-generated
                null, null,
                Arrays.asList("test", "article"),
                testUserId,
                testUserId,
                testUserId
        );

        when(articleRepository.existsBySlugAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            // Simulate setting ID after save
            return Article.ofExisting(
                    1L,
                    article.getTitle(),
                    article.getSlug(),
                    article.getContent(),
                    article.getExcerpt(),
                    article.getStatus(),
                    article.getAuthorId(),
                    article.getPublishedAt(),
                    article.getMetaTitle(),
                    article.getMetaDescription(),
                    article.getOgImageUrl(),
                    article.getCanonicalUrl(),
                    article.getKeywords(),
                    article.getCreatedBy(),
                    article.getUpdatedBy(),
                    article.getCreatedAt(),
                    article.getUpdatedAt(),
                    article.getDeletedAt()
            );
        });

        // when
        ArticleDetailDto result = useCase.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Test Article Title");
        assertThat(result.slug()).isEqualTo("test-article-title");
        assertThat(result.content()).isEqualTo("Test content for the article");
        assertThat(result.status()).isEqualTo(ArticleStatus.DRAFT);
        assertThat(result.metaDescription()).isNotBlank(); // Should be auto-generated

        verify(articleRepository).existsBySlugAndDeletedAtIsNull("test-article-title");
        verify(articleRepository).save(any(Article.class));
    }

    @Test
    @DisplayName("should generate unique slug when slug already exists")
    void shouldGenerateUniqueSlugWhenSlugAlreadyExists() {
        // given
        CreateArticleCommand command = new CreateArticleCommand(
                "Test Title",
                "Test content",
                null, null, null, null, null, null,
                testUserId, testUserId, testUserId
        );

        when(articleRepository.existsBySlugAndDeletedAtIsNull("test-title"))
                .thenReturn(true)   // First check in execute
                .thenReturn(true);  // First check in makeSlugUnique
        when(articleRepository.existsBySlugAndDeletedAtIsNull("test-title-1")).thenReturn(false);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            return Article.ofExisting(
                    1L, article.getTitle(), article.getSlug(), article.getContent(),
                    article.getExcerpt(), article.getStatus(), article.getAuthorId(),
                    article.getPublishedAt(), article.getMetaTitle(), article.getMetaDescription(),
                    article.getOgImageUrl(), article.getCanonicalUrl(), article.getKeywords(),
                    article.getCreatedBy(), article.getUpdatedBy(),
                    article.getCreatedAt(), article.getUpdatedAt(), article.getDeletedAt()
            );
        });

        // when
        ArticleDetailDto result = useCase.execute(command);

        // then
        assertThat(result.slug()).isEqualTo("test-title-1");
        verify(articleRepository, times(2)).existsBySlugAndDeletedAtIsNull("test-title");
        verify(articleRepository).existsBySlugAndDeletedAtIsNull("test-title-1");
    }

    @Test
    @DisplayName("should auto-generate meta description when not provided")
    void shouldAutoGenerateMetaDescriptionWhenNotProvided() {
        // given
        String longContent = "This is a very long content that should be truncated to 160 characters. " +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation.";

        CreateArticleCommand command = new CreateArticleCommand(
                "Test Title",
                longContent,
                null, null, null, null, null, null,
                testUserId, testUserId, testUserId
        );

        when(articleRepository.existsBySlugAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            return Article.ofExisting(
                    1L, article.getTitle(), article.getSlug(), article.getContent(),
                    article.getExcerpt(), article.getStatus(), article.getAuthorId(),
                    article.getPublishedAt(), article.getMetaTitle(), article.getMetaDescription(),
                    article.getOgImageUrl(), article.getCanonicalUrl(), article.getKeywords(),
                    article.getCreatedBy(), article.getUpdatedBy(),
                    article.getCreatedAt(), article.getUpdatedAt(), article.getDeletedAt()
            );
        });

        // when
        ArticleDetailDto result = useCase.execute(command);

        // then
        assertThat(result.metaDescription())
                .isNotBlank()
                .hasSizeLessThanOrEqualTo(160)
                .startsWith("This is a very long content");
    }

    @Test
    @DisplayName("should handle Polish characters in slug generation")
    void shouldHandlePolishCharactersInSlugGeneration() {
        // given
        CreateArticleCommand command = new CreateArticleCommand(
                "Artykuł o żądłach pszczół",  // Polish characters: ą, ó, ł, ż
                "Test content",
                null, null, null, null, null, null,
                testUserId, testUserId, testUserId
        );

        when(articleRepository.existsBySlugAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            return Article.ofExisting(
                    1L, article.getTitle(), article.getSlug(), article.getContent(),
                    article.getExcerpt(), article.getStatus(), article.getAuthorId(),
                    article.getPublishedAt(), article.getMetaTitle(), article.getMetaDescription(),
                    article.getOgImageUrl(), article.getCanonicalUrl(), article.getKeywords(),
                    article.getCreatedBy(), article.getUpdatedBy(),
                    article.getCreatedAt(), article.getUpdatedAt(), article.getDeletedAt()
            );
        });

        // when
        ArticleDetailDto result = useCase.execute(command);

        // then
        assertThat(result.slug())
                .matches("[a-z0-9-]+")  // Only lowercase alphanumeric and hyphens
                .doesNotContain("ą", "ó", "ł", "ż");
    }
}
