package az.ilkin.eis.controller;


import az.ilkin.eis.dto.request.SubmitAnswerRequest;
import az.ilkin.eis.dto.response.AnswerResponse;
import az.ilkin.eis.dto.response.ExamResultResponse;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.service.StudentExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/exams")
@RequiredArgsConstructor
@Tag(name = "Telebe - Imtahan Prosesi", description = "Yalniz STUDENT")
@SecurityRequirement(name = "bearerAuth")
public class StudentExamController {

    private final StudentExamService studentExamService;

   @PostMapping("/{examId}/start")
   @PreAuthorize("hasRole('STUDENT)'")
   @Operation(summary = "Imtahani baslat")
    public ResponseEntity<ExamResultResponse>startExam(
            @PathVariable Long examId,
            @AuthenticationPrincipal User student){
        return ResponseEntity.ok(studentExamService.startExam(examId,student));
    }


    @PostMapping("/{examId}/answer")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Suala cavab ver (her cavab ayrica gonderilir)")

    public ResponseEntity<AnswerResponse>submitAnswer(
            @PathVariable Long examId,
            @Valid @RequestBody SubmitAnswerRequest request,
            @AuthenticationPrincipal User student){
       return ResponseEntity.ok(studentExamService.submitAnswer(examId,request,student));
    }

    @PostMapping("/{examId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Imtahani teqdim et (bitir)")
    public ResponseEntity<ExamResultResponse>submitExam(
            @PathVariable Long examId,
            @AuthenticationPrincipal User student){
       return ResponseEntity.ok(studentExamService.submitExam(examId,student));
    }


    @GetMapping("/{examId}/result")
    @PreAuthorize("/hasRole('STUDENT')")
    @Operation(summary = "Oz neticeme bax")
    public ResponseEntity<ExamResultResponse>getMyResult(
            @PathVariable Long examId,
            @AuthenticationPrincipal User student){
       return ResponseEntity.ok(studentExamService.getMyResult(examId,student));
    }



}
