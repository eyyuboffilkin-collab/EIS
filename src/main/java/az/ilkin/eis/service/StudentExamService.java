package az.ilkin.eis.service;

import az.ilkin.eis.dto.request.SubmitAnswerRequest;
import az.ilkin.eis.dto.response.AnswerResponse;
import az.ilkin.eis.dto.response.ExamResultResponse;
import az.ilkin.eis.entity.*;
import az.ilkin.eis.enums.Difficulty;
import az.ilkin.eis.enums.ExamStatus;
import az.ilkin.eis.enums.QuestionType;
import az.ilkin.eis.exception.BadRequestException;
import az.ilkin.eis.exception.ResourceNotFoundException;
import az.ilkin.eis.repository.AnswerRepository;
import az.ilkin.eis.repository.ClassRepository;
import az.ilkin.eis.repository.ExamResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentExamService {

    private final ExamService examService;
    private final ExamResultRepository examResultRepository;
    private final AnswerRepository answerRepository;
    private final ClassRepository classRepository;

    @Transactional
    public ExamResultResponse startExam(Long examId, User student) {
        Exam exam = examService.findById(examId);

        if (exam.getStatus() != ExamStatus.ACTIVE) {
            throw new BadRequestException("Bu imtahan aktiv deyil");
        }

        if (examResultRepository.existsByExamAndStudent(exam, student)) {
            ExamResult existing = examResultRepository
                    .findByExamAndStudent(exam, student)
                    .orElseThrow();
            return toResponse(existing);
        }

        if (isTimeExpired(exam)) {
            throw new BadRequestException("İmtahanın vaxtı bitib");
        }

        ExamResult result = ExamResult.builder()
                .exam(exam)
                .student(student)
                .startedAt(LocalDateTime.now())
                .build();

        return toResponse(examResultRepository.save(result));
    }

    @Transactional
    public AnswerResponse submitAnswer(Long examId, SubmitAnswerRequest request, User student) {
        Exam exam = examService.findById(examId);
        ExamResult result = getActiveResult(exam, student);

        if (isTimeExpired(exam)) {
            autoSubmit(result, exam);
            throw new BadRequestException("Vaxt bitib — imtahan avtomatik təqdim edildi");
        }

        Question question = exam.getQuestions()
                .stream()
                .filter(q -> q.getId().equals(request.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Bu sual həmin imtahana aid deyil"));

        Answer answer = answerRepository
                .findByExamResultAndQuestion(result, question)
                .orElse(Answer.builder()
                        .examResult(result)
                        .question(question)
                        .build());

        answer.setAnswerText(request.getAnswerText());

        if (question.getType() == QuestionType.TEST) {
            boolean correct = question.getCorrectOption()
                    .equalsIgnoreCase(request.getAnswerText());
            answer.setScore(correct ? getScoreByDifficulty(exam, question.getDifficulty()) : 0);
            answer.setGraded(true);
        }

        return toAnswerResponse(answerRepository.save(answer));
    }

    @Transactional
    public ExamResultResponse submitExam(Long examId, User student) {
        Exam exam = examService.findById(examId);
        ExamResult result = getActiveResult(exam, student);

        if (result.getSubmittedAt() != null) {
            throw new BadRequestException("Bu imtahanı artıq təqdim etmisiniz");
        }

        finalizeResult(result, exam);
        return toResponse(examResultRepository.save(result));
    }

    @Transactional
    public void autoSubmit(ExamResult result, Exam exam) {
        if (result.getSubmittedAt() != null) return;
        finalizeResult(result, exam);
        examResultRepository.save(result);
    }

    public ExamResultResponse getMyResult(Long examId, User student) {
        Exam exam = examService.findById(examId);
        ExamResult result = examResultRepository.findByExamAndStudent(exam, student)
                .orElseThrow(() -> new ResourceNotFoundException("Bu imtahana aid nəticəniz tapılmadı"));
        return toResponse(result);
    }

    // ─────────────────────────────────────────────
    // Daxili köməkçi metodlar
    // ─────────────────────────────────────────────

    private void finalizeResult(ExamResult result, Exam exam) {
        result.setSubmittedAt(LocalDateTime.now());

        int total = result.getAnswers()
                .stream()
                .mapToInt(a -> a.getScore() != null ? a.getScore() : 0)
                .sum();
        result.setTotalScore(total);

        boolean hasUngraded = result.getAnswers()
                .stream()
                .anyMatch(a -> !a.isGraded());
        result.setFullyGraded(!hasUngraded);
    }

    private ExamResult getActiveResult(Exam exam, User student) {
        return examResultRepository.findByExamAndStudent(exam, student)
                .orElseThrow(() -> new BadRequestException("Əvvəlcə imtahanı başlatmalısınız"));
    }

    private boolean isTimeExpired(Exam exam) {
        if (exam.getStartTime() == null) return false;
        LocalDateTime endTime = exam.getStartTime().plusMinutes(exam.getDuration());
        return LocalDateTime.now().isAfter(endTime);
    }

    private int getScoreByDifficulty(Exam exam, Difficulty difficulty) {
        return switch (difficulty) {
            case EASY   -> exam.getEasyScore();
            case MEDIUM -> exam.getMediumScore();
            case HARD   -> exam.getHardScore();
        };
    }

    // ─────────────────────────────────────────────
    // DTO çevirmə metodları
    // ─────────────────────────────────────────────

    public ExamResultResponse toResponse(ExamResult result) {
        List<AnswerResponse> answers = result.getAnswers()
                .stream()
                .map(this::toAnswerResponse)
                .collect(Collectors.toList());

        return ExamResultResponse.builder()
                .id(result.getId())
                .examId(result.getExam().getId())
                .examTitle(result.getExam().getTitle())
                .studentName(result.getStudent().getName())
                .startedAt(result.getStartedAt())
                .submittedAt(result.getSubmittedAt())
                .totalScore(result.getTotalScore())
                .fullyGraded(result.isFullyGraded())
                .answers(answers)
                .build();
    }

    public AnswerResponse toAnswerResponse(Answer answer) {
        return toAnswerResponse(answer,false);
    }

    public AnswerResponse toAnswerResponse(Answer answer, boolean reveal){
        Question question = answer.getQuestion();
        AnswerResponse.AnswerResponseBuilder builder = AnswerResponse.builder()
                .id(answer.getId())
                .questionId(question.getId())
                .questionText(question.getText())
                .questionType(question.getType())
                .answerText(answer.getAnswerText())
                .score(answer.getScore())
                .graded(answer.isGraded());

        if(reveal && question.getType() == QuestionType.TEST){
            builder.correctOption(question.getCorrectOption());
            builder.correct(question.getCorrectOption() != null
            && question.getCorrectOption().equalsIgnoreCase(answer.getAnswerText()));
        }
        return builder.build();
    }
}
