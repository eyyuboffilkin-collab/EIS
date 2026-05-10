package az.ilkin.eis.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClassRequest {

    @NotBlank(message = "Sinif adi bos ola bilmez")
    private String name;
}
