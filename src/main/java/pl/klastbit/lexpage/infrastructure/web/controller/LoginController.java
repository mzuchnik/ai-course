package pl.klastbit.lexpage.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.klastbit.lexpage.infrastructure.adapters.security.UserPrincipal;

/**
 * Controller for authentication pages (login, admin dashboard).
 * Handles login form display and admin panel entry point.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    /**
     * Displays the login form.
     * Shows error message if authentication failed.
     * Shows logout message if user just logged out.
     *
     * @param error  optional parameter indicating authentication failure
     * @param logout optional parameter indicating successful logout
     * @param model  Spring MVC model
     * @return Thymeleaf template path
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) Boolean error,
            @RequestParam(required = false) Boolean logout,
            Model model
    ) {
        log.debug("Displaying login page. Error: {}, Logout: {}", error, logout);

        model.addAttribute("pageTitle", "Logowanie - Lexpage");

        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("errorMessage", "Nieprawidłowy email lub hasło");
            log.debug("Login error displayed");
        }

        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute("logoutMessage", "Zostałeś pomyślnie wylogowany");
            log.info("User logged out successfully");
        }

        return "pages/auth/login";
    }

    /**
     * Admin panel dashboard (entry point after login).
     * Displays welcome page with username of logged-in user.
     *
     * @param model Spring MVC model
     * @return Thymeleaf template path
     */
    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        // Get currently authenticated user from Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            String username = userPrincipal.getDisplayName();
            log.info("Admin dashboard accessed by user: {}", username);

            model.addAttribute("pageTitle", "Panel Administracyjny - Lexpage");
            model.addAttribute("username", username);
            model.addAttribute("email", userPrincipal.getUsername()); // email

            return "pages/admin/dashboard";
        }

        // Shouldn't happen due to Spring Security, but handle gracefully
        log.warn("Admin dashboard accessed without authentication");
        return "redirect:/login";
    }
}
