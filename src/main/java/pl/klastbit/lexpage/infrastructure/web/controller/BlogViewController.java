package pl.klastbit.lexpage.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.klastbit.lexpage.application.article.GetArticleUseCase;
import pl.klastbit.lexpage.application.article.ListArticlesUseCase;
import pl.klastbit.lexpage.application.article.dto.ArticleDetailDto;
import pl.klastbit.lexpage.application.article.dto.ArticleListItemDto;
import pl.klastbit.lexpage.application.article.dto.PageDto;
import pl.klastbit.lexpage.domain.article.ArticleStatus;
import pl.klastbit.lexpage.domain.article.exception.ArticleNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * MVC Controller for public blog views (Server-Side Rendering).
 * Handles listing articles and viewing individual article details.
 * Inbound adapter in Hexagonal Architecture.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class BlogViewController {

    private final ListArticlesUseCase listArticlesUseCase;
    private final GetArticleUseCase getArticleUseCase;

    /**
     * Displays paginated list of published articles (3x3 grid, 9 per page).
     * Route: /blog?page=0
     */
    @GetMapping("/blog")
    public String listArticles(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        log.info("Displaying blog list page: {}", page);

        // Validate page number
        if (page < 0) {
            log.warn("Invalid page number: {}, redirecting to page 0", page);
            return "redirect:/blog?page=0";
        }

        // Fetch published articles (9 per page, sorted by publishedAt DESC)
        PageRequest pageable = PageRequest.of(page, 9, Sort.by(Sort.Direction.DESC, "publishedAt"));
        PageDto<ArticleListItemDto> articles = listArticlesUseCase.execute(
                ArticleStatus.PUBLISHED,
                null,  // no author filter
                null,  // no keyword filter
                pageable
        );

        // Handle out-of-bounds page number
        if (page > 0 && articles.page().totalPages() > 0 && page >= articles.page().totalPages()) {
            int lastPage = articles.page().totalPages() - 1;
            log.warn("Page {} exceeds total pages {}, redirecting to last page", page, lastPage);
            return "redirect:/blog?page=" + lastPage;
        }

        // SEO metadata
        model.addAttribute("pageTitle", "Blog - Lexpage");
        model.addAttribute("pageDescription", "Odkryj nasze najnowsze artykuły prawnicze. Porady, analizy i praktyczne wskazówki dla każdego.");

        // Page data
        model.addAttribute("articles", articles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.page().totalPages());

        // No index for pages beyond first
        if (page > 0) {
            model.addAttribute("robotsContent", "noindex, follow");
        }

        return "pages/blog/index";
    }

    /**
     * Displays detailed view of a single published article.
     * Route: /blog/{slug}
     */
    @GetMapping("/blog/{slug}")
    public String viewArticle(
            @PathVariable String slug,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Displaying article with slug: {}", slug);

        try {
            // Fetch article by slug (only PUBLISHED)
            ArticleDetailDto article = getArticleUseCase.executeBySlug(slug);

            // Get related articles (4 newest, then exclude current and take 3)
            PageRequest relatedPageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "publishedAt"));
            PageDto<ArticleListItemDto> allRecent = listArticlesUseCase.execute(
                    ArticleStatus.PUBLISHED,
                    null,
                    null,
                    relatedPageable
            );

            // Filter out current article and take 3
            List<ArticleListItemDto> relatedArticles = allRecent.content().stream()
                    .filter(a -> !a.slug().equals(slug))
                    .limit(3)
                    .toList();

            // Format published date
            String formattedDate = formatPublishedDate(article.publishedAt());

            // Create breadcrumbs
            List<Map<String, String>> breadcrumbs = List.of(
                    Map.of("text", "Blog", "url", "/blog"),
                    Map.of("text", article.title(), "url", "")  // current page, no URL
            );

            // SEO metadata
            String pageTitle = article.metaTitle() != null && !article.metaTitle().isBlank()
                    ? article.metaTitle()
                    : article.title() + " - Lexpage";

            String canonicalUrl = article.canonicalUrl() != null && !article.canonicalUrl().isBlank()
                    ? article.canonicalUrl()
                    : "/blog/" + article.slug();

            String keywords = article.keywords() != null && !article.keywords().isEmpty()
                    ? String.join(", ", article.keywords())
                    : null;

            model.addAttribute("pageTitle", pageTitle);
            model.addAttribute("pageDescription", article.metaDescription());
            model.addAttribute("keywords", keywords);
            model.addAttribute("canonicalUrl", canonicalUrl);

            // Open Graph metadata
            model.addAttribute("ogImageUrl", article.ogImageUrl());
            model.addAttribute("ogType", "article");
            model.addAttribute("publishedAt", article.publishedAt());
            model.addAttribute("updatedAt", article.updatedAt());

            // Page data
            model.addAttribute("article", article);
            model.addAttribute("formattedPublishedDate", formattedDate);
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("relatedArticles", relatedArticles);

            return "pages/blog/article";

        } catch (ArticleNotFoundException e) {
            log.warn("Article not found or not published: {}", slug);
            redirectAttributes.addFlashAttribute("error", "Artykuł nie został znaleziony.");
            return "redirect:/blog";
        } catch (IllegalArgumentException e) {
            log.warn("Invalid slug format: {}", slug);
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowy adres artykułu.");
            return "redirect:/blog";
        }
    }

    /**
     * Formats published date in Polish locale.
     * Format: "26 stycznia 2026"
     *
     * @param publishedAt Article publication timestamp
     * @return Formatted date string in Polish
     */
    private String formatPublishedDate(LocalDateTime publishedAt) {
        if (publishedAt == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("pl-PL"));
        return publishedAt.format(formatter);
    }
}
