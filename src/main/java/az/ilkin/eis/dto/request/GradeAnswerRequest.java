package az.ilkin.eis.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeAnswerRequest {

    @NotNull(message = "Cavab ID-si gosterilmelidir")
    private Long answerId;

    @NotNull(message = "Bal gosterilmelidir")
    @Min(value = 0,message = "Bal menfi ola bilmez")
    private Integer score;
}
