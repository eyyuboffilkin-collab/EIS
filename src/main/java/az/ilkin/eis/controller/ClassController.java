package az.ilkin.eis.controller;

import az.ilkin.eis.dto.request.ClassRequest;
import az.ilkin.eis.dto.response.ClassResponse;
import az.ilkin.eis.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Sinif İdarəetməsi")
@SecurityRequirement(name = "bearerAuth")
public class ClassController {

    private final ClassService classService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Bütün siniflərin siyahısı")
    public ResponseEntity<List<ClassResponse>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yeni sinif yarat")
    public ResponseEntity<ClassResponse> createClass(@Valid @RequestBody ClassRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classService.createClass(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Sinifi yenilə")
    public ResponseEntity<ClassResponse> updateClass(@PathVariable Long id,
                                                     @Valid @RequestBody ClassRequest request) {
        return ResponseEntity.ok(classService.updateClass(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Sinifi sil")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/students")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Sinfə tələbə əlavə et")
    public ResponseEntity<ClassResponse> addStudent(@PathVariable Long id,
                                                    @RequestParam Long studentId) {
        return ResponseEntity.ok(classService.addStudent(id, studentId));
    }

    @DeleteMapping("/{id}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tələbəni sinifdən çıxart")
    public ResponseEntity<ClassResponse> removeStudent(@PathVariable Long id,
                                                       @PathVariable Long studentId) {
        return ResponseEntity.ok(classService.removeStudent(id, studentId));
    }

    @PostMapping("/{id}/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Sinfə müəllim əlavə et")
    public ResponseEntity<ClassResponse> addTeacher(@PathVariable Long id,
                                                    @RequestParam Long teacherId) {
        return ResponseEntity.ok(classService.addTeacher(id, teacherId));
    }

    @DeleteMapping("/{id}/teachers/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Müəllimi sinifdən çıxart")
    public ResponseEntity<ClassResponse> removeTeacher(@PathVariable Long id,
                                                       @PathVariable Long teacherId) {
        return ResponseEntity.ok(classService.removeTeacher(id, teacherId));
    }
}