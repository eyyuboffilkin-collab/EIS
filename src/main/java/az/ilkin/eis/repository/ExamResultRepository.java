package az.ilkin.eis.repository;

import az.ilkin.eis.entity.Exam;
import az.ilkin.eis.entity.ExamResult;
import az.ilkin.eis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult,Long> {
    Optional<ExamResult>findByExamAndStudent(Exam exam, User student);

    List<ExamResult>findByStudent(User student);

    List<ExamResult>findByExam(Exam exam);

    boolean existsByExamAndStudent(Exam exam, User student);
}
