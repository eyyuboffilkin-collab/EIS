package az.ilkin.eis.controller;

import az.ilkin.eis.dto.request.ExamRequest;
import az.ilkin.eis.dto.response.ExamResponse;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.service.ExamService;
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
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@Tag(name = "Imtahan Idareetmesi", description = "Yalniz TEACHER")
@SecurityRequirement(name = "bearerAuth")
public class ExamController {
    private final ExamService examService;

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Oz imtahanlarinin siyahisi")
    public ResponseEntity<List<ExamResponse>> getMyExams(@AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(examService.getMyExams(teacher));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Imtahanin detallari")
    public ResponseEntity<ExamResponse> getExam(@PathVariable Long id, @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(examService.getExamById(id, teacher));
    }


    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Yeni imtahan yarat (DRAFT)")
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamRequest request,
                                                   @AuthenticationPrincipal User teacher) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(request, teacher));
    }


    @PutMapping("{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Imtahani yenile (yalniz DRAFT)")
    public ResponseEntity<ExamResponse>updateExam(@PathVariable Long id,@Valid @RequestBody ExamRequest request,@AuthenticationPrincipal User teacher){
        return ResponseEntity.ok(examService.updateExam(id,request,teacher));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Imtahani sil (yalniz DRAFT)")
    public ResponseEntity<Void>deleteExam(@PathVariable Long id,
                                          @AuthenticationPrincipal User teacher){
        examService.deleteExam(id,teacher);
        return ResponseEntity.noContent().build();
    }

    //Sual emeliyatlari

    @PostMapping("/{examId}/questions/{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Imtahana sual elave et (yalniz DRAFT)")
    public ResponseEntity<ExamResponse>addQuestion(@PathVariable Long examId,
                                                   @PathVariable Long questionId,
                                                   @AuthenticationPrincipal User teacher){
        return ResponseEntity.ok(examService.addQuestion(examId,questionId,teacher));
    }

    @DeleteMapping("/{examId}/questions/{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Imtahandan sual cixart (yalniz DRAFT)")
    public ResponseEntity<ExamResponse>removeQuestion(@PathVariable Long examId,
                                                      @PathVariable Long questionId,
                                                      @AuthenticationPrincipal User teacher){
        return ResponseEntity.ok(examService.removeQuestion(examId,questionId,teacher));
    }
    //Status kecidleri

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Imtahani bitir: ACTIVE -> FINISHED")
    public ResponseEntity<ExamResponse>activate(@PathVariable Long id,
                                                @AuthenticationPrincipal User teacher){
        return ResponseEntity.ok(examService.activateExam(id,teacher));
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Imtahani bitir: ACTIVE -> FINISHED")
    public ResponseEntity<ExamResponse>finish(@PathVariable Long id, @AuthenticationPrincipal User teacher){
        return ResponseEntity.ok(examService.finishExam(id,teacher));
    }


}
