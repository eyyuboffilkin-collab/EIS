package az.ilkin.eis.controller;

import az.ilkin.eis.dto.request.GradeAnswerRequest;
import az.ilkin.eis.dto.response.AnswerResponse;
import az.ilkin.eis.dto.response.ExamResultResponse;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.service.GradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grading")
@RequiredArgsConstructor
@Tag(name = "Qiymetlendirme", description = "Yalniz TEACHER")
@SecurityRequirement(name = "bearerAuth")
public class GradingController {

    private final GradingService gradingService;


    @GetMapping("/exams/{examId}/results")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Imtahanin butun neticeleri")
    public ResponseEntity<List<ExamResultResponse>>getExamResults(
            @PathVariable Long examId,
            @AuthenticationPrincipal User teacher){
        return ResponseEntity.ok(gradingService.getExamResults(examId,teacher));
    }

    @GetMapping("/results/{resultId}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Tek telebenin neticesini detallari")
    public ResponseEntity<ExamResultResponse>getStudentsResult(
            @PathVariable Long resultId,
            @AuthenticationPrincipal User teacher){
        return ResponseEntity.ok(gradingService.getStudentResult(resultId,teacher));
    }

    @PostMapping("/exams/{examId}/grade")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Aciq suala bal ver")
    public ResponseEntity<AnswerResponse>gradeAnswer(
            @PathVariable Long examId,
            @Valid @RequestBody GradeAnswerRequest request,
            @AuthenticationPrincipal User teacher){
        return ResponseEntity.ok(gradingService.gradeAnswer(examId,request,teacher));
    }

}
