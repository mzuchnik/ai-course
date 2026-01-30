package pl.klastbit.lexpage.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.klastbit.lexpage.application.article.GenerateArticleWithAIUseCase;
import pl.klastbit.lexpage.application.article.command.GenerateArticleWithAICommand;
import pl.klastbit.lexpage.application.article.dto.AIGeneratedContentDto;
import pl.klastbit.lexpage.infrastructure.web.controller.dto.AIGeneratedContentResponse;
import pl.klastbit.lexpage.infrastructure.web.controller.dto.GenerateArticleAIRequest;

/**
 * REST controller for AI-powered article generation.
 */
@RestController
@RequestMapping("/api/articles/ai")
@RequiredArgsConstructor
@Slf4j
public class ArticleAIController {

    private final GenerateArticleWithAIUseCase generateArticleWithAIUseCase;

    @PostMapping("/generate")
    public ResponseEntity<AIGeneratedContentResponse> generateArticle(
        @Valid @RequestBody GenerateArticleAIRequest request
    ) {
        log.info("POST /api/articles/ai/generate - prompt length: {}", request.userPrompt().length());

        GenerateArticleWithAICommand command = new GenerateArticleWithAICommand(request.userPrompt());
        AIGeneratedContentDto result = generateArticleWithAIUseCase.execute(command);

        return ResponseEntity.ok(AIGeneratedContentResponse.from(result));
    }
}
