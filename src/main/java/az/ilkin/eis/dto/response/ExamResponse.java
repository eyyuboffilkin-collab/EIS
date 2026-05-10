package az.ilkin.eis.dto.response;

import az.ilkin.eis.enums.ExamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {

    private Long id;
    private String title;
    private ExamStatus status;
    private LocalDateTime startTime;
    private Integer duration;

    //Bal sistemi
    private Integer easyScore;
    private Integer mediumScore;
    private Integer hardScore;

    private String createdByName;
    private List<QuestionResponse> questions;

}
