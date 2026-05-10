package az.ilkin.eis.service;

import az.ilkin.eis.dto.request.ClassRequest;
import az.ilkin.eis.dto.response.ClassResponse;
import az.ilkin.eis.dto.response.UserResponse;
import az.ilkin.eis.entity.Classroom;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.enums.Role;
import az.ilkin.eis.exception.BadRequestException;
import az.ilkin.eis.exception.ResourceNotFoundException;
import az.ilkin.eis.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final UserService userService;

    public List<ClassResponse> getAllClasses() {
        return classRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClassResponse createClass(ClassRequest request) {
        if (classRepository.existsByName(request.getName())) {
            throw new BadRequestException("Bu adlı sinif artıq mövcuddur: " + request.getName());
        }
        Classroom cls = Classroom.builder()
                .name(request.getName())
                .build();
        return toResponse(classRepository.save(cls));
    }

    public ClassResponse updateClass(Long id, ClassRequest request) {
        Classroom cls = findById(id);
        cls.setName(request.getName());
        return toResponse(classRepository.save(cls));
    }

    public void deleteClass(Long id) {
        Classroom cls = findById(id);
        classRepository.delete(cls);
    }

    @Transactional
    public ClassResponse addStudent(Long classId, Long studentId) {
        Classroom cls = findById(classId);
        User student = userService.findById(studentId);

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("İstifadəçi tələbə deyil");
        }
        if (cls.getStudents().contains(student)) {
            throw new BadRequestException("Bu tələbə artıq sinifdədir");
        }
        cls.getStudents().add(student);
        return toResponse(classRepository.save(cls));
    }

    @Transactional
    public ClassResponse removeStudent(Long classId, Long studentId) {
        Classroom cls = findById(classId);
        User student = userService.findById(studentId);
        cls.getStudents().remove(student);
        return toResponse(classRepository.save(cls));
    }

    @Transactional
    public ClassResponse addTeacher(Long classId, Long teacherId) {
        Classroom cls = findById(classId);
        User teacher = userService.findById(teacherId);

        if (teacher.getRole() != Role.TEACHER) {
            throw new BadRequestException("İstifadəçi müəllim deyil");
        }
        if (cls.getTeachers().contains(teacher)) {
            throw new BadRequestException("Bu müəllim artıq sinifdədir");
        }
        cls.getTeachers().add(teacher);
        return toResponse(classRepository.save(cls));
    }

    @Transactional
    public ClassResponse removeTeacher(Long classId, Long teacherId) {
        Classroom cls = findById(classId);
        User teacher = userService.findById(teacherId);
        cls.getTeachers().remove(teacher);
        return toResponse(classRepository.save(cls));
    }

    public Classroom findById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinif tapılmadı: id=" + id));
    }

    private ClassResponse toResponse(Classroom cls) {
        List<UserResponse> students = cls.getStudents()
                .stream()
                .map(userService::toResponse)
                .collect(Collectors.toList());

        List<UserResponse> teachers = cls.getTeachers()
                .stream()
                .map(userService::toResponse)
                .collect(Collectors.toList());

        return ClassResponse.builder()
                .id(cls.getId())
                .name(cls.getName())
                .students(students)
                .teachers(teachers)
                .build();
    }
}