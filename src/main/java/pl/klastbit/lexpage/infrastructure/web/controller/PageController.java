package pl.klastbit.lexpage.infrastructure.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.klastbit.lexpage.infrastructure.web.dto.homepage.HomepageDtos.*;

import java.util.List;

/**
 * MVC controller for serving Thymeleaf pages.
 * Inbound adapter in hexagonal architecture.
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String index(Model model) {
        // SEO metadata
        model.addAttribute("pageTitle", "Kancelaria Prawna Lexpage - Skuteczna Pomoc Prawna w Warszawie");
        model.addAttribute("pageDescription", "Profesjonalna kancelaria prawna z 15-letnim doświadczeniem. Prawo cywilne, karne, gospodarcze. Bezpłatna konsultacja.");

        // === SECTION 1: Hero ===
        model.addAttribute("heroEyebrow", "Kancelaria prawna z 15-letnim doświadczeniem");
        model.addAttribute("heroTitle", "Skutecznie bronimy Twoich praw w sprawach cywilnych i karnych");
        model.addAttribute("heroSubtitle", "Profesjonalna obsługa prawna z gwarancją sukcesu. 98% wygranych spraw.");
        model.addAttribute("heroImage", "https://placehold.co/1200x600/0ea5e9/ffffff?text=Lawyer");

        // === SECTION 2: Trust Logos ===
        List<LogoItemDto> trustLogos = List.of(
                new LogoItemDto("https://placehold.co/200x60/cccccc/666666?text=Rzeczpospolita", "Rzeczpospolita", "#"),
                new LogoItemDto("https://placehold.co/200x60/cccccc/666666?text=Gazeta+Prawna", "Gazeta Prawna", "#"),
                new LogoItemDto("https://placehold.co/200x60/cccccc/666666?text=Izba", "Izba Adwokacka", "#"),
                new LogoItemDto("https://placehold.co/200x60/cccccc/666666?text=NRA", "Naczelna Rada Adwokacka", "#"),
                new LogoItemDto("https://placehold.co/200x60/cccccc/666666?text=Forbes", "Forbes Polska", "#"),
                new LogoItemDto("https://placehold.co/200x60/cccccc/666666?text=Money", "Money.pl", "#")
        );
        model.addAttribute("trustLogos", trustLogos);

        // === SECTION 3: Services (6 items) ===
        List<ServiceTileDto> services = List.of(
                new ServiceTileDto("Prawo cywilne", "Kompleksowa obsługa spraw cywilnych", "gavel", "civil",
                        List.of("Sprawy rozwodowe", "Sprawy kontraktowe", "Odszkodowania")),
                new ServiceTileDto("Prawo karne", "Profesjonalna obrona w postępowaniach karnych", "policy", "criminal",
                        List.of("Obrona w sprawach karnych", "Reprezentacja pokrzywdzonych", "Sprawy gospodarcze")),
                new ServiceTileDto("Prawo gospodarcze", "Wsparcie prawne dla firm i przedsiębiorców", "business_center", "civil",
                        List.of("Doradztwo dla firm", "Prawo kontraktowe", "Restrukturyzacje")),
                new ServiceTileDto("Prawo rodzinne", "Empatyczna pomoc w sprawach rodzinnych", "family_restroom", "civil",
                        List.of("Rozwody", "Alimenty", "Kontakty z dziećmi")),
                new ServiceTileDto("Prawo spadkowe", "Obsługa spraw spadkowych od A do Z", "account_balance", "civil",
                        List.of("Działy spadku", "Testamenty", "Stwierdzenie nabycia spadku")),
                new ServiceTileDto("Windykacja należności", "Skuteczne odzyskiwanie długów", "request_quote", "criminal",
                        List.of("Windykacja polubowna", "Windykacja sądowa", "Success fee"))
        );
        model.addAttribute("services", services);

        // === SECTION 4: Value Propositions (4 items) ===
        List<ValuePropositionDto> valueProps = List.of(
                new ValuePropositionDto("verified", "15 lat doświadczenia", "Ponad 1000 wygranych spraw w całej Polsce"),
                new ValuePropositionDto("payments", "Rozliczenie success fee", "Płacisz tylko za sukces - bez ryzyka"),
                new ValuePropositionDto("support_agent", "Osobiste podejście", "Każdy klient jest dla nas najważniejszy"),
                new ValuePropositionDto("schedule", "Dostępność 24/7", "Kontakt w nagłych sprawach karnych")
        );
        model.addAttribute("valueProps", valueProps);

        // === SECTION 5: Process Steps (3 steps) ===
        List<ProcessStepDto> processSteps = List.of(
                new ProcessStepDto("calendar_today", "Bezpłatna konsultacja", "Poznajemy sprawę i oceniamy szanse powodzenia"),
                new ProcessStepDto("pending", "Plan działania", "Opracowujemy strategię i przedstawiamy ofertę"),
                new ProcessStepDto("verified_user", "Realizacja i sukces", "Prowadzimy sprawę do końca")
        );
        model.addAttribute("processSteps", processSteps);

        // === SECTION 6: Team Members (3 lawyers) ===
        List<LawyerProfileDto> teamMembers = List.of(
                new LawyerProfileDto("Dr Anna Kowalska", "Radca prawny, Partner zarządzający",
                        "https://ui-avatars.com/api/?name=Anna+Kowalska&size=600&background=0ea5e9&color=fff&bold=true",
                        "15 lat doświadczenia w prawie cywilnym i rodzinnym. Absolwentka UW, doktor nauk prawnych.",
                        List.of("Prawo cywilne", "Prawo rodzinne", "Sprawy spadkowe")),
                new LawyerProfileDto("Mec. Piotr Nowak", "Adwokat, Specjalista prawa karnego",
                        "https://ui-avatars.com/api/?name=Piotr+Nowak&size=600&background=e0426a&color=fff&bold=true",
                        "Ponad 200 wygranych spraw karnych. Członek Izby Adwokackiej w Warszawie.",
                        List.of("Prawo karne", "Sprawy gospodarcze", "Postępowania wykroczeniowe")),
                new LawyerProfileDto("Mec. Katarzyna Wiśniewska", "Radca prawny",
                        "https://ui-avatars.com/api/?name=Katarzyna+Wisniewska&size=600&background=0ea5e9&color=fff&bold=true",
                        "Specjalizacja w prawie gospodarczym i kontraktowym. MBA w zarządzaniu.",
                        List.of("Prawo gospodarcze", "Prawo kontraktowe", "Windykacja"))
        );
        model.addAttribute("teamMembers", teamMembers);

        // === SECTION 7: Testimonials (6 reviews) ===
        List<TestimonialDto> testimonials = List.of(
                new TestimonialDto("Profesjonalna obsługa i pełne zaangażowanie. Dzięki Pani Kowalskiej wygrałem sprawę rozwodową w rekordowym czasie.",
                        "A.K.", "Klient - Sprawa rozwodowa", 5.0),
                new TestimonialDto("Pan Nowak obronił mnie w trudnej sprawie karnej. Czułem się bezpiecznie i dobrze poinformowany.",
                        "M.Z.", "Klient - Sprawa karna", 5.0),
                new TestimonialDto("Kancelaria pomogła mi odzyskać należność za kontrakt. Success fee to uczciwe rozwiązanie.",
                        "P.W.", "Przedsiębiorca - Windykacja", 4.5),
                new TestimonialDto("Pani Wiśniewska pomogła w sporządzeniu umowy spółki. Wszystko wyjaśniła zrozumiałym językiem.",
                        "J.S.", "Klient - Prawo gospodarcze", 5.0),
                new TestimonialDto("Sprawa spadkowa załatwiona sprawnie i bez komplikacji. Profesjonalizm i cierpliwość.",
                        "E.M.", "Klient - Sprawa spadkowa", 4.5),
                new TestimonialDto("Konsultacja telefoniczna wyjaśniła wszystkie wątpliwości. Bardzo kompetentna obsługa.",
                        "T.L.", "Klient - Konsultacja", 5.0)
        );
        model.addAttribute("testimonials", testimonials);

        // === SECTION 8: FAQ (8 items) ===
        List<FaqItemDto> faqItems = List.of(
                new FaqItemDto("Ile kosztuje konsultacja?",
                        "<p>Pierwsza konsultacja (do 30 minut) jest <strong>całkowicie bezpłatna</strong>.</p>"),
                new FaqItemDto("Jak wygląda rozliczenie success fee?",
                        "<p>W wybranych sprawach oferujemy model success fee - płacisz tylko jeśli wygramy sprawę.</p>"),
                new FaqItemDto("Czy prowadzicie sprawy poza Warszawą?",
                        "<p>Tak, obsługujemy klientów w <strong>całej Polsce</strong>. Konsultacje możemy przeprowadzić online.</p>"),
                new FaqItemDto("Jak długo trwa typowa sprawa sądowa?",
                        "<p>Sprawy karne: 6-18 miesięcy, sprawy cywilne: 12-24 miesiące. Na konsultacji przedstawimy timeline.</p>"),
                new FaqItemDto("Czy mogę się skontaktować po godzinach?",
                        "<p>Tak! Oferujemy wsparcie <strong>24/7 dla pilnych spraw karnych</strong>.</p>"),
                new FaqItemDto("Jakie dokumenty przygotować?",
                        "<p>Wszystkie dokumenty związane ze sprawą. Jeśli nie masz - pomożemy je uzyskać.</p>"),
                new FaqItemDto("Czy udzielają Państwo porad online?",
                        "<p>Tak, prowadzimy konsultacje przez Zoom, Google Meet, Teams.</p>"),
                new FaqItemDto("Czy mogę przerwać współpracę?",
                        "<p>Tak, możesz wypowiedzieć pełnomocnictwo. Rozliczymy się za wykonaną pracę.</p>")
        );
        model.addAttribute("faqItems", faqItems);

        return "pages/index";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Kontakt - Lexpage");
        model.addAttribute("pageDescription", "Get in touch with our legal experts");
        return "pages/contact";
    }

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("pageTitle", "Test");
        model.addAttribute("pageDescription", "Test");
        model.addAttribute("testMessage", "Hello from test page!");
        return "pages/test";
    }
}
