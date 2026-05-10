package az.ilkin.eis.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email bos ola bilmez")
    @Email(message = "Email formati duzgun deyil")
    private String email;


    @NotBlank(message = "Sifre bos ola bilmez")
    private String password;
}
