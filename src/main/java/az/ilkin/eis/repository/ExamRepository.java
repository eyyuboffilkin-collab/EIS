package az.ilkin.eis.repository;

import az.ilkin.eis.entity.Exam;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.enums.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam,Long> {
    List<Exam> findByCreatedBy(User teacher);
    List<Exam>findByStatus(ExamStatus status);
}
