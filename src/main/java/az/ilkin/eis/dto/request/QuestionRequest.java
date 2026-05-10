package az.ilkin.eis.dto.request;

import az.ilkin.eis.enums.Difficulty;
import az.ilkin.eis.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionRequest {

    @NotBlank(message = "Sual metni bos ola bilmez")
    private String text;

    @NotNull(message = "Sual tipi secilmelidir")
    private QuestionType type;


    @NotNull(message = "Cetinlik derecesi secilmelidir")
    private Difficulty difficulty;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
}
