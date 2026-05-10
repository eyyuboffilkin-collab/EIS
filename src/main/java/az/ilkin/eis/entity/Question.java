package az.ilkin.eis.entity;

import az.ilkin.eis.enums.Difficulty;
import az.ilkin.eis.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false,columnDefinition = "TEXT")
    private String text;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type; //TEST | OPEN


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;  //EASY | MEDIUM | HARD


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",nullable = false)
    private User createdBy;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String correctOption;
}
