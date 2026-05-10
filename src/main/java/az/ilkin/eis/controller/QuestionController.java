package az.ilkin.eis.controller;


import az.ilkin.eis.dto.request.QuestionRequest;
import az.ilkin.eis.dto.response.QuestionResponse;
import az.ilkin.eis.entity.Question;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Sual idareetmesi", description = "Yalniz TEACHER")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {
    private final QuestionService questionService;


    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Oz suallarinin siyahisi")
    public ResponseEntity<List<QuestionResponse>> getMyQuestions(@AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(questionService.getMyQuestions(teacher));
    }


    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Yeni sual yarat")
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request,
                                                           @AuthenticationPrincipal User teacher) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.createQuestion(request, teacher));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Suali yenile")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id,
                                                           @Valid @RequestBody QuestionRequest request,
                                                           @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(questionService.updateQuestion(id, request, teacher));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Suali sil")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id,
                                               @AuthenticationPrincipal User teacher) {
        questionService.deleteQuestion(id, teacher);
        return ResponseEntity.noContent().build();
    }

}
