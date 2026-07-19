package az.ilkin.eis.dto.response;

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
public class ExamResultResponse {
    private Long id;
    private Long examId;
    private String examTitle;
    private String studentName;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Integer totalScore;
    private boolean fullyGraded;
    private List<AnswerResponse> answers;
}