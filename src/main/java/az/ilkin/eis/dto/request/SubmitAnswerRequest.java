package az.ilkin.eis.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAnswerRequest {
    @NotNull(message = "Sual ID-si gosterilmelidir")
    private Long questionId;

    private String answerText;
}
