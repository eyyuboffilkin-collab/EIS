package az.ilkin.eis.dto.response;

import az.ilkin.eis.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
   private Long id;
   private Long questionId;
   private String questionText;
   private QuestionType questionType;
   private String answerText;
   private Integer score;
   private boolean graded;
}
