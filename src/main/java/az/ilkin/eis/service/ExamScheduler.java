package az.ilkin.eis.service;

import az.ilkin.eis.entity.Exam;
import az.ilkin.eis.entity.ExamResult;
import az.ilkin.eis.enums.ExamStatus;
import az.ilkin.eis.repository.ExamRepository;
import az.ilkin.eis.repository.ExamResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExamScheduler {
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final StudentExamService studentExamService;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void autoSubmitExpiredExams() {
        LocalDateTime now = LocalDateTime.now();
        List<Exam> activeExams = examRepository.findByStatus(ExamStatus.ACTIVE);

        for (Exam exam : activeExams) {
            if (exam.getStartTime() == null) continue;

            LocalDateTime endTime = exam.getStartTime().plusMinutes(exam.getDuration());

            if (now.isAfter(endTime)) {
                List<ExamResult> results = examResultRepository.findByExam(exam);

                for (ExamResult result : results) {
                    if (result.getSubmittedAt() == null) {
                        log.info("Auto-submit: student={}, exam={}",
                                result.getStudent().getEmail(), exam.getTitle());
                        studentExamService.autoSubmit(result, exam);
                    }
                }

                exam.setStatus(ExamStatus.FINISHED);
                examRepository.save(exam);
                log.info("Imtahan avtomatik bitirildi: {}", exam.getTitle());
            }
        }
    }
}