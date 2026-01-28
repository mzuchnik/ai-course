package pl.klastbit.lexpage.infrastructure.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MVC controller for serving admin blog management views.
 * Inbound adapter (Primary/Driving) in Hexagonal Architecture.
 *
 * This controller serves Thymeleaf templates for the admin panel.
 * All CRUD operations are handled by JavaScript through REST API (ArticleController).
 *
 * Note: Authentication and authorization are not implemented yet - to be added later.
 */
@Controller
@RequestMapping("/admin/blogs")
public class AdminBlogController {

    /**
     * GET /admin/blogs - Lista artykułów z filtrowaniem i akcjami.
     *
     * @param model Model dla Thymeleaf
     * @return Widok listy artykułów (pages/admin/blogs/index)
     */
    @GetMapping
    public String listArticles(Model model) {
        model.addAttribute("pageTitle", "Zarządzanie Blogami");
        return "pages/admin/blogs/index";
    }

    /**
     * GET /admin/blogs/new - Formularz dodawania nowego artykułu.
     *
     * @param model Model dla Thymeleaf
     * @return Widok formularza (pages/admin/blogs/form) w trybie tworzenia
     */
    @GetMapping("/new")
    public String newArticle(Model model) {
        model.addAttribute("pageTitle", "Nowy Artykuł");
        model.addAttribute("mode", "create");
        return "pages/admin/blogs/form";
    }

    /**
     * GET /admin/blogs/{id}/edit - Formularz edycji istniejącego artykułu.
     *
     * @param id    ID artykułu do edycji
     * @param model Model dla Thymeleaf
     * @return Widok formularza (pages/admin/blogs/form) w trybie edycji
     */
    @GetMapping("/{id}/edit")
    public String editArticle(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Edytuj Artykuł");
        model.addAttribute("mode", "edit");
        model.addAttribute("articleId", id);
        return "pages/admin/blogs/form";
    }
}
