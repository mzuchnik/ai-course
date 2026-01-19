package pl.klastbit.lexpage.domain.service;

import java.util.Objects;

/**
 * FAQ Item value object for Service FAQ.
 * Immutable value object representing a single FAQ question-answer pair.
 */
public record FaqItem(String question, String answer) {

    public FaqItem {
        Objects.requireNonNull(question, "Question cannot be null");
        Objects.requireNonNull(answer, "Answer cannot be null");

        if (question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be empty");
        }

        if (answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer cannot be empty");
        }
    }
}
