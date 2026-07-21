package az.ilkin.eis.controller;

import az.ilkin.eis.dto.request.LoginRequest;
import az.ilkin.eis.dto.request.RegisterRequest;
import az.ilkin.eis.dto.response.AuthResponse;
import az.ilkin.eis.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenfikasiya", description = "Qeydiyyat ve giris")
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "Yeni istifadeci qeydiyyati")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @GetMapping
    public String password(@RequestParam String password){
        return passwordEncoder.encode(password);

    }

    @PostMapping("/login")
    @Operation(summary = "Giris - JWT token qaytarir")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
