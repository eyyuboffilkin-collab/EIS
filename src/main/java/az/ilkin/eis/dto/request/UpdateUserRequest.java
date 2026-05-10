package az.ilkin.eis.dto.request;

import az.ilkin.eis.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {

    private String name;

    @Email(message = "Email formati duzgun deyil")
    private String email;

    private String password;
    private Role role;
}
