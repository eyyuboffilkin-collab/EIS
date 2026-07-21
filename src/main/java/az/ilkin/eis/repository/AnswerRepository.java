package az.ilkin.eis.repository;

import az.ilkin.eis.entity.Answer;
import az.ilkin.eis.entity.ExamResult;
import az.ilkin.eis.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByExamResultAndQuestion(ExamResult examResult, Question question);

    List<Answer> findByExamResult(ExamResult examResult);

    @Query(value = "SELECT a FROM Answer a WHERE a.examResult.exam.id = :examId " +
            "AND a.question.type = 'OPEN' AND a.graded = false")
    List<Answer> findUngradedOpenAnswersByExamId(@Param("examId") Long examId);

    @Query("SELECT a FROM Answer a WHERE a.examResult.exam.id = :examId " +
            "AND a.question.type = 'OPEN'")
    List<Answer> findOpenAnswersByExamId(@Param("examId") Long examId);

    boolean existsByQuestion(Question question);

}


