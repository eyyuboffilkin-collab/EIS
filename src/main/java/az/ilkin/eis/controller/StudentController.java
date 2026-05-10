package az.ilkin.eis.controller;


import az.ilkin.eis.dto.response.ExamResponse;
import az.ilkin.eis.dto.response.ExamResultResponse;
import az.ilkin.eis.dto.response.UserResponse;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "Telebe - Umumi", description = "Yalniz STUDENT")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentServie;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Oz profilim")
    public ResponseEntity<UserResponse>getMyProfile(@AuthenticationPrincipal User student){
        return ResponseEntity.ok(studentServie.getProfile(student));
    }

    @GetMapping("/exams")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Aktiv imhanlarin siyahisi")
    public ResponseEntity<List<ExamResponse>>getActiveExams(
            @AuthenticationPrincipal User student){
        return ResponseEntity.ok(studentServie.getActiveExams(student));
    }

    @GetMapping("/results")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Oz butun neticelerim")
    public ResponseEntity<List<ExamResultResponse>>getMyResults(
            @AuthenticationPrincipal User student){
        return ResponseEntity.ok(studentServie.getMyResults(student));
    }


}
