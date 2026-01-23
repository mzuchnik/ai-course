package pl.klastbit.lexpage.infrastructure.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC controller for serving Thymeleaf pages.
 * Inbound adapter in hexagonal architecture.
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Strona główna - Lexpage");
        model.addAttribute("pageDescription", "Professional legal services you can trust");
        return "pages/index";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Kontakt - Lexpage");
        model.addAttribute("pageDescription", "Get in touch with our legal experts");
        return "pages/contact";
    }
}
