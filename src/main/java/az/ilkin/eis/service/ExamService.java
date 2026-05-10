package az.ilkin.eis.service;

import az.ilkin.eis.dto.request.ExamRequest;
import az.ilkin.eis.dto.response.ExamResponse;
import az.ilkin.eis.entity.Exam;
import az.ilkin.eis.entity.Question;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.enums.ExamStatus;
import az.ilkin.eis.exception.BadRequestException;
import az.ilkin.eis.exception.ForbiddenException;
import az.ilkin.eis.exception.ResourceNotFoundException;
import az.ilkin.eis.repository.ExamRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final QuestionService questionService;


    //Muellimin oz imtahanlari
    public List<ExamResponse> getMyExams(User teacher) {
        return examRepository.findByCreatedBy(teacher)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //Tek imtahanin detallari
    public ExamResponse getExamById(Long id, User user) {
        Exam exam = findById(id);
        checkOwnership(exam, user);
        return toResponse(exam);
    }

    public ExamResponse createExam( ExamRequest request, User teacher) {
        Exam exam = Exam.builder()
                .title(request.getTitle())
                .duration(request.getDuration())
                .easyScore(request.getEasyScore())
                .mediumScore(request.getMediumScore())
                .hardScore(request.getHardScore())
                .status(ExamStatus.DRAFT)
                .createdBy(teacher)
                .build();

        return toResponse(examRepository.save(exam));
    }

    public ExamResponse updateExam(Long id, ExamRequest request, User teacher) {
        Exam exam = findById(id);
        checkOwnership(exam, teacher);
        requireStatus(exam, ExamStatus.DRAFT, "Yalniz DRAFT statuslu imtahan deyisdirile biler");

        exam.setTitle(request.getTitle());
        exam.setDuration(request.getDuration());
        exam.setEasyScore(request.getEasyScore());
        exam.setMediumScore(request.getMediumScore());
        exam.setHardScore(request.getHardScore());

        return toResponse(examRepository.save(exam));
    }

    //Imtahani sil
    public void deleteExam(Long id, User teacher) {
        Exam exam = findById(id);
        checkOwnership(exam, teacher);
        requireStatus(exam, ExamStatus.DRAFT, "Yalniz DRAFT statuslu imtahan siline biler");
        examRepository.delete(exam);
    }

    @Transactional
    public ExamResponse addQuestion(Long examId, Long questionId, User teacher) {
        Exam exam = findById(examId);
        checkOwnership(exam, teacher);
        requireStatus(exam, ExamStatus.DRAFT, "ACTIVE/FINISHED imtahana sual elave etmek olar");

        Question question = questionService.findById(questionId);

        //Eyni sual iki defe elave edile bilmez
        boolean alreadyAdded = exam.getQuestions()
                .stream()
                .anyMatch(q -> q.getId().equals(questionId));

        if (alreadyAdded) {
            throw new BadRequestException("Bu sual imtahanda artiq movcuddur");
        }
        exam.getQuestions().add(question);
        return toResponse(examRepository.save(exam));
    }

    //Imtahandan sual cixart
    @Transactional
    public ExamResponse removeQuestion(Long examId, Long questionId, User teacher){
        Exam exam = findById(examId);
        checkOwnership(exam,teacher);
        requireStatus(exam,ExamStatus.DRAFT,"ACTIVE/FINISHED imtahandan sual cixarmaq olmaz");

        exam.getQuestions().removeIf(q -> q.getId().equals(questionId));
        return toResponse(examRepository.save(exam));
    }


    //Imtahani baslat DRAFT -> ACTIVE

    @Transactional
    public ExamResponse activateExam(Long id, User teacher){
        Exam exam = findById(id);
        checkOwnership(exam,teacher);
        requireStatus(exam,ExamStatus.DRAFT,"Yalniz DRAFT statuslu imtahan aktivlesdirile biler");

        if(exam.getQuestions().isEmpty()){
            throw new BadRequestException("Imahana en az bir sual elave edilmelidir");
        }
        exam.setStatus(ExamStatus.ACTIVE);
        exam.setStartTime(LocalDateTime.now());

        return toResponse(examRepository.save(exam));
    }

    //Imtahani bitir
    @Transactional
    public ExamResponse finishExam(Long id, User teacher){
        Exam exam = findById(id);
        checkOwnership(exam,teacher);
        requireStatus(exam,ExamStatus.ACTIVE,"Yalniz ACTIVE statuslu imtahan bitirile biler");

        exam.setStatus(ExamStatus.FINISHED);
        return toResponse(examRepository.save(exam));
    }

    //Komekci metodlar

    public Exam findById(Long id){
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imtahan tapilmadi id=" + id));
    }
    //Imtananin muellime mexsus olmasini yoxla
    private void checkOwnership(Exam exam, User user){
        if(!exam.getCreatedBy().getId().equals(user.getId())){
            throw new ForbiddenException("Bu imtahan size mexsus deyil");
        }
    }
    //Imtahanin mueyyen statusda olmasini yoxla
    private void requireStatus(Exam exam, ExamStatus required,String errorMessage){
        if(exam.getStatus() != required){
            throw new BadRequestException(errorMessage);
        }
    }
    //Entity -> DTO cevirme
    public ExamResponse toResponse(Exam exam){
        return ExamResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .status(exam.getStatus())
                .startTime(exam.getStartTime())
                .duration(exam.getDuration())
                .easyScore(exam.getEasyScore())
                .mediumScore(exam.getMediumScore())
                .hardScore(exam.getHardScore())
                .createdByName(exam.getCreatedBy().getName())
                .questions(
                        exam.getQuestions()
                                .stream()
                                .map(questionService::toResponse)
                                .collect(Collectors.toList())
                )
                .build();
    }

}


