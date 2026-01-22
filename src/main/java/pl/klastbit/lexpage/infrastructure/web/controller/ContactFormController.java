package pl.klastbit.lexpage.infrastructure.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.klastbit.lexpage.application.contact.command.SubmitContactFormCommand;
import pl.klastbit.lexpage.application.contact.result.ContactFormResult;
import pl.klastbit.lexpage.application.contact.service.ContactFormApplicationService;
import pl.klastbit.lexpage.infrastructure.web.dto.request.SubmitContactFormRequest;
import pl.klastbit.lexpage.infrastructure.web.dto.response.ContactFormResponse;

/**
 * REST controller for contact form endpoints.
 * Inbound adapter in hexagonal architecture.
 */
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Slf4j
public class ContactFormController {

    private final ContactFormApplicationService contactFormService;

    @PostMapping
    public ResponseEntity<ContactFormResponse> submitContactForm(
            @Valid @RequestBody SubmitContactFormRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Received contact form submission from IP: {}", getClientIp(httpRequest));

        SubmitContactFormCommand command = new SubmitContactFormCommand(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone(),
            request.category(),
            request.message(),
            getClientIp(httpRequest),
            getUserAgent(httpRequest)
        );

        ContactFormResult result = contactFormService.submitContactForm(command);

        return ResponseEntity.ok(ContactFormResponse.fromResult(result));
    }

    /**
     * Extracts client IP address from request, handling proxy headers.
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        // Handle multiple IPs in X-Forwarded-For (take first one)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * Extracts user agent from request.
     */
    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
