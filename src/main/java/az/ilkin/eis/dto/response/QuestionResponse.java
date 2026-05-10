package az.ilkin.eis.dto.response;

import az.ilkin.eis.enums.Difficulty;
import az.ilkin.eis.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String text;
    private QuestionType type;
    private Difficulty difficulty;


    //TEST tipli suallar
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; //Yalniz muellim

    private String createdByName;
}
