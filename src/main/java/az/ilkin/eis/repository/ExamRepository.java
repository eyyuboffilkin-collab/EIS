package az.ilkin.eis.repository;

import az.ilkin.eis.entity.Exam;
import az.ilkin.eis.entity.Question;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.enums.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam,Long> {
    List<Exam> findByCreatedBy(User teacher);
    List<Exam>findByStatus(ExamStatus status);


    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Exam e JOIN e.questions " +
            "q WHERE q = :question")
    boolean existsByQuestionsContaining(@Param("question") Question question);
}
