package pl.klastbit.lexpage.infrastructure.adapters.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

/**
 * Service for converting Markdown to HTML.
 * Uses CommonMark library for safe and compliant conversion.
 */
@Component
@Slf4j
public class MarkdownToHtmlConverter {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownToHtmlConverter() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    public String convert(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }

        try {
            Node document = parser.parse(markdown);
            String html = renderer.render(document);

            log.debug("Converted Markdown to HTML. Input length: {}, Output length: {}",
                markdown.length(), html.length());

            return html;

        } catch (Exception e) {
            log.error("Failed to convert Markdown to HTML", e);
            throw new RuntimeException("Markdown conversion failed", e);
        }
    }
}
