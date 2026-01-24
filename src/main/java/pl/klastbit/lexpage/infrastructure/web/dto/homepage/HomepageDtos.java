package pl.klastbit.lexpage.infrastructure.web.dto.homepage;

import java.util.List;

/**
 * DTOs for homepage sections.
 * Using Java Records for immutable data transfer objects.
 */
public final class HomepageDtos {

    private HomepageDtos() {
        // Utility class - prevent instantiation
    }

    /**
     * Lawyer profile for team section.
     *
     * @param name           Full name of the lawyer
     * @param role           Professional role/title
     * @param photo          Path to photo image
     * @param bio            Short biography (2-3 sentences)
     * @param specializations List of legal specialization areas
     */
    public record LawyerProfileDto(
            String name,
            String role,
            String photo,
            String bio,
            List<String> specializations
    ) {}

    /**
     * Client testimonial/review.
     *
     * @param quote  The testimonial text
     * @param author Client initials (e.g., "A.K.")
     * @param role   Client role/case type (e.g., "Klient - Sprawa rozwodowa")
     * @param rating Rating value (0.0 to 5.0)
     */
    public record TestimonialDto(
            String quote,
            String author,
            String role,
            double rating
    ) {}

    /**
     * FAQ item for accordion.
     *
     * @param title   Question text
     * @param content Answer HTML content
     */
    public record FaqItemDto(
            String title,
            String content
    ) {}

    /**
     * Service tile for services section.
     *
     * @param title       Service name
     * @param description Short description
     * @param icon        Material icon name
     * @param category    "civil" or "criminal" for color coding
     * @param examples    List of example cases/sub-services
     */
    public record ServiceTileDto(
            String title,
            String description,
            String icon,
            String category,
            List<String> examples
    ) {}

    /**
     * Process step for cooperation timeline.
     *
     * @param icon        Material icon name
     * @param title       Step name
     * @param description Step description
     */
    public record ProcessStepDto(
            String icon,
            String title,
            String description
    ) {}

    /**
     * Logo item for trust bar.
     *
     * @param src Image source path
     * @param alt Alt text
     * @param url Link URL (or "#" if none)
     */
    public record LogoItemDto(
            String src,
            String alt,
            String url
    ) {}

    /**
     * Value proposition box.
     *
     * @param icon        Material icon name
     * @param title       Value proposition title
     * @param description Detailed description
     */
    public record ValuePropositionDto(
            String icon,
            String title,
            String description
    ) {}
}
