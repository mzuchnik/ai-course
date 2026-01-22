package pl.klastbit.lexpage.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity for ai_generations table.
 * Tracks AI content generation history with daily limit support.
 */
@Entity
@Table(name = "ai_generations")
@Getter
@Setter
@NoArgsConstructor
public class AIGenerationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ai_generations_user"))
    private UserEntity user;

    @Column(name = "prompt", nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "generated_content", nullable = false, columnDefinition = "TEXT")
    private String generatedContent;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "generation_time_ms")
    private Integer generationTimeMs;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "generation_status_enum")
    private GenerationStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", foreignKey = @ForeignKey(name = "fk_ai_generations_article"))
    private ArticleEntity article;
}
