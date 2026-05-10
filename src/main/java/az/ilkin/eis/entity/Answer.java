package az.ilkin.eis.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_result_id",nullable = false)
    private ExamResult examResult;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id",nullable = false)
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String answerText;

    @Builder.Default
    private Integer score = 0;

    @Builder.Default
    private boolean graded = false;
}


