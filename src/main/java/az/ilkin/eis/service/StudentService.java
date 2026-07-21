package az.ilkin.eis.service;


import az.ilkin.eis.dto.response.ExamResponse;
import az.ilkin.eis.dto.response.ExamResultResponse;
import az.ilkin.eis.dto.response.UserResponse;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.enums.ExamStatus;
import az.ilkin.eis.repository.ClassRepository;
import az.ilkin.eis.repository.ExamRepository;
import az.ilkin.eis.repository.ExamResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamService examService;
    private final StudentExamService studentExamService;
    private final UserService userService;
    private final ClassRepository classRepository;

    //Telebenin profili
    public UserResponse getProfile(User student){
        return userService.toResponse(student);
    }
    //Hazirda ACTIVE olan butun imtahanlar

    public List<ExamResponse>getActiveExams(User student){
        return examRepository.findByStatus(ExamStatus.ACTIVE)
                .stream()
                .filter(exam -> classRepository.existsByTeacherAndStudent(exam.getCreatedBy(),student))
                .map(examService::toResponse)
                .collect(Collectors.toList());
    }

    //Telebenin butun neticeleri
    public List<ExamResultResponse>getMyResults(User student){
        return examResultRepository.findByStudent(student)
                .stream()
                .map(studentExamService::toResponse)
                .collect(Collectors.toList());
    }



}
