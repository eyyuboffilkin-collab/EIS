package az.ilkin.eis.service;

import az.ilkin.eis.dto.request.GradeAnswerRequest;
import az.ilkin.eis.dto.response.AnswerResponse;
import az.ilkin.eis.dto.response.ExamResultResponse;
import az.ilkin.eis.entity.Answer;
import az.ilkin.eis.entity.Exam;
import az.ilkin.eis.entity.ExamResult;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.enums.Difficulty;
import az.ilkin.eis.enums.QuestionType;
import az.ilkin.eis.exception.BadRequestException;
import az.ilkin.eis.exception.ForbiddenException;
import az.ilkin.eis.exception.ResourceNotFoundException;
import az.ilkin.eis.repository.AnswerRepository;
import az.ilkin.eis.repository.ExamResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradingService {

    private final ExamService examService;
    private final ExamResultRepository examResultRepository;
    private final AnswerRepository answerRepository;
    private final StudentExamService studentExamService;

    //Imtahanin butun neticeleri
    public List<ExamResultResponse>getExamResults(Long examId, User teacher){
        Exam exam = examService.findById(examId);
        checkOwnership(exam,teacher);

        return examResultRepository.findByExam(exam)
                .stream()
                .map(studentExamService::toResponse)
                .collect(Collectors.toList());
    }
    //Tek telebenin neticesi
    public ExamResultResponse getStudentResult(Long resultId,User teacher){
        ExamResult result = findResultById(resultId);
        checkOwnership(result.getExam(),teacher);
        return studentExamService.toResponse(result);
    }
    //Aciq suallari yoxla
    @Transactional
    public AnswerResponse gradeAnswer(Long examId, GradeAnswerRequest request,User teacher){
        Exam exam = examService.findById(examId);
        checkOwnership(exam,teacher);

        Answer answer = answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cavab tapilmadi"));

        //Yalniz aciq suallar manuel yoxlanilir
        if(answer.getQuestion().getType() != QuestionType.OPEN){
            throw new BadRequestException("TEST suallari avtomatik yoxlanilir,manual bal vermek olmaz");
        }
        //Balin maksimum heddini yoxla
        int maxScore = getMaxScore(exam,answer.getQuestion().getDifficulty());
        if(request.getScore() > maxScore){
            throw new BadRequestException(
                    "Bal cox yuksekdir. Bu sual ucun maksimum bal: " + maxScore
            );
        }
        answer.setScore(request.getScore());
        answer.setGraded(true);
        answerRepository.save(answer);

        //Neticenin umumi balini yenile
        updateResultScore(answer.getExamResult());
        return studentExamService.toAnswerResponse(answer);
    }

    //Neticenin balini yeniden hesabla
    private void updateResultScore(ExamResult result){
        int total = result.getAnswers()
                .stream()
                .mapToInt(a -> a.getScore() != null ? a.getScore() : 0)
                .sum();
        result.setTotalScore(total);

        //Butun cavablar yoxlanilibmi
        boolean allGraded = result.getAnswers()
                .stream()
                .allMatch(Answer::isGraded);
        result.setFullyGraded(allGraded);

        examResultRepository.save(result);
    }

    private int getMaxScore(Exam exam, Difficulty difficulty){
        return switch (difficulty){
            case EASY -> exam.getEasyScore();
            case MEDIUM -> exam.getMediumScore();
            case HARD -> exam.getHardScore();
        };
    }
    private void checkOwnership(Exam exam,User teacher){
        if(!exam.getCreatedBy().getId().equals(teacher.getId())){
            throw new ForbiddenException("Bu imtahan size mexsus deyil");
        }
    }
    private ExamResult findResultById(Long id){
        return examResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Netice tapilmadi: id=" + id));
    }


}
