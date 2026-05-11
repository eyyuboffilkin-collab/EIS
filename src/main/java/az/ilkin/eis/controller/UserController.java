package az.ilkin.eis.controller;

import az.ilkin.eis.dto.request.CreateUserRequest;
import az.ilkin.eis.dto.request.UpdateUserRequest;
import az.ilkin.eis.dto.response.UserResponse;
import az.ilkin.eis.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Istifadeci idareetmesi",description = "Yalniz ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Butun istifadecilerin siyahisi")
    public ResponseEntity<List<UserResponse>>getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yeni istifadeci yarat")
    public ResponseEntity<UserResponse>createUser(@Valid @RequestBody CreateUserRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Istifadecini yenile")
    public ResponseEntity<UserResponse>updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request){
        return ResponseEntity.ok(userService.updateUser(id,request));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Istifadecini sil")
    public ResponseEntity<Void>deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
