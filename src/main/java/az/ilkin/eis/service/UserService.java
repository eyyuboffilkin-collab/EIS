package az.ilkin.eis.service;

import az.ilkin.eis.dto.request.CreateUserRequest;
import az.ilkin.eis.dto.request.UpdateUserRequest;
import az.ilkin.eis.dto.response.UserResponse;
import az.ilkin.eis.entity.Classroom;
import az.ilkin.eis.entity.Exam;
import az.ilkin.eis.entity.Question;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.enums.Role;
import az.ilkin.eis.exception.BadRequestException;
import az.ilkin.eis.exception.ResourceNotFoundException;
import az.ilkin.eis.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClassRepository classRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamResultRepository examResultRepository;

    //Butun istifadeciler
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu email artiq movcuddur: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findById(id);

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Bu email artiq movcuddur: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        return toResponse(userRepository.save(user));
    }
    @Transactional
    public void transferContent(Long fromUserId, Long toUserId){
        User from = findById(fromUserId);
        User to = findById(toUserId);

        if(from.getId().equals(to.getId())){
            throw new BadRequestException("Menbe ve hedef istifadeci eyni ola bilmez");
        }
        if(to.getRole() != Role.TEACHER){
            throw new BadRequestException("Melumat yalniz TEACHER roluna kocurule biler");
        }
        List<Exam>exams = examRepository.findByCreatedBy(from);
        for(Exam exam : exams){
            exam.setCreatedBy(to);
        }
        examRepository.saveAll(exams);
        List<Question>questions = questionRepository.findByCreatedBy(from);
        for (Question question : questions) {
            question.setCreatedBy(to);
        }
        questionRepository.saveAll(questions);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        if (!examRepository.findByCreatedBy(user).isEmpty()) {
            throw new BadRequestException(
                    "Bu istifadeci imtahan yaradib silinmezden evvel o imtahanlari basqa muellime kocurun ve ya silin"
            );
        }
        if(!questionRepository.findByCreatedBy(user).isEmpty()){
            throw new BadRequestException(
                    "Bu istifadeci sual bankina elave edib, silinmezden evvel o suallari silin"
            );
        }
        if(!examResultRepository.findByStudent(user).isEmpty()){
            throw new BadRequestException(
                    "Bu telebenin imtahan neticeleri movcuddur, silinmezden evvel netice tarixcesi qorunmalidir"
            );
        }
        List<Classroom>asTeacher = classRepository.findByTeachersContaining(user);
        for(Classroom classroom : asTeacher){
            classroom.getTeachers().remove(user);
        }
        classRepository.saveAll(asTeacher);

        List<Classroom>asStudent = classRepository.findByStudentsContaining(user);
        for (Classroom classroom : asStudent) {
            classroom.getStudents().remove(user);
        }
        classRepository.saveAll(asStudent);
        userRepository.delete(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Istifadeci tapilmadi: id=" + id));
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}


