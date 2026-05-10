package az.ilkin.eis.dto.request;

import az.ilkin.eis.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Ad bos ola bilmez")
    private String name;

    @NotBlank(message = "Email bos ola bilmez")
    @Email(message = "Email formati duzgun deyil")
    private String email;

    @NotBlank(message = "Sifre bos ola bilmez")
    private String password;

    @NotBlank(message = "Rol secilmelidir")
    private Role role;

}
