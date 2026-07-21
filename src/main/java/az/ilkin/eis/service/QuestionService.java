package az.ilkin.eis.service;

import az.ilkin.eis.dto.request.QuestionRequest;
import az.ilkin.eis.dto.response.QuestionResponse;
import az.ilkin.eis.entity.Question;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.enums.QuestionType;
import az.ilkin.eis.exception.BadRequestException;
import az.ilkin.eis.exception.ForbiddenException;
import az.ilkin.eis.exception.ResourceNotFoundException;
import az.ilkin.eis.repository.AnswerRepository;
import az.ilkin.eis.repository.ExamRepository;
import az.ilkin.eis.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;

    //muellimin oz suallari
    public List<QuestionResponse> getMyQuestions(User teacher) {
        return questionRepository.findByCreatedBy(teacher)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //Yeni sual
    public QuestionResponse createQuestion(QuestionRequest request, User teacher) {
        validateRequest(request);

        Question question = Question.builder()
                .text(request.getText())
                .type(request.getType())
                .difficulty(request.getDifficulty())
                .createdBy(teacher)
                .build();

        //Test tipli suallar ucun varianlari set etmek
        if (request.getType() == QuestionType.TEST) {
            question.setOptionA(request.getOptionA());
            question.setOptionB(request.getOptionB());
            question.setOptionC(request.getOptionC());
            question.setOptionD(request.getOptionD());
            question.setCorrectOption(request.getCorrectOption());
        }
        return toResponse(questionRepository.save(question));
    }

    //Suali yenilemek
    public QuestionResponse updateQuestion(Long id, QuestionRequest request, User teacher) {
        Question question = findById(id);

        //Yalniz oz sualini deyismek
        if (!question.getCreatedBy().getId().equals(teacher.getId())) {
            throw new ForbiddenException("Bu sual size mexsus deyil");
        }
        validateRequest(request);

        question.setText(request.getText());
        question.setType(request.getType());
        question.setDifficulty(request.getDifficulty());

        if (request.getType() == QuestionType.TEST) {
            question.setOptionA(request.getOptionA());
            question.setOptionB(request.getOptionB());
            question.setOptionC(request.getOptionC());
            question.setOptionD(request.getOptionD());
            question.setCorrectOption(request.getCorrectOption());
        } else {
            //OPEN tipine kecirse varianlar temizlenir
            question.setOptionA(null);
            question.setOptionB(null);
            question.setOptionC(null);
            question.setOptionD(null);
            question.setCorrectOption(null);
        }
        return toResponse(questionRepository.save(question));
    }

    //Suali silmek
    public void deleteQuestion(Long id, User teacher) {
        Question question = findById(id);

        if (!question.getCreatedBy().getId().equals(teacher.getId())) {
            throw new ForbiddenException("Bu sual size mexsus deyil");
        }
        if(answerRepository.existsByQuestion(question)){
            throw new BadRequestException(
                    "Bu suala artiq telebe cavabi verilib, silinmezden evvel netice tarixcesi qorunmalidir"
            );
        }
        if(examRepository.existsByQuestionsContaining(question)){
            throw new BadRequestException(
                    "Bu sual bir imtahana daxil edilib, evvelce suali imtahandan cixarin"
            );
        }
        questionRepository.delete(question);
    }

    //Validation TEST tipli sual ucun varianlar mecburidir
    private void validateRequest(QuestionRequest request) {
        if (request.getType() == QuestionType.TEST) {
            if (request.getOptionA() == null || request.getOptionA().isBlank() ||
                    request.getOptionB() == null || request.getOptionB().isBlank() ||
                    request.getOptionC() == null || request.getOptionC().isBlank() ||
                    request.getOptionD() == null || request.getOptionD().isBlank()) {
                throw new BadRequestException("TEST tipli sual ucun butun 4 variant doldurulmalidir");
            }
            if (request.getCorrectOption() == null ||
                    !List.of("A", "B", "C", "D").contains(request.getCorrectOption().toUpperCase())) {
                throw new BadRequestException("Duzgun cavab A,B,C ve ya D olmalidir");
            }
            request.setCorrectOption(request.getCorrectOption().toUpperCase());
        }
    }
    //Komekci metodlar
    public Question findById(Long id){
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sual tapilmadi id=" + id));
    }
    public QuestionResponse toResponse(Question q){
        return QuestionResponse.builder()
                .id(q.getId())
                .text(q.getText())
                .type(q.getType())
                .difficulty(q.getDifficulty())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .optionD(q.getOptionD())
                .correctOption(q.getCorrectOption())
                .createdByName(q.getCreatedBy().getName())
                .build();
    }

}
