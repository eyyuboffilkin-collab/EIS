package az.ilkin.eis.service;

import az.ilkin.eis.dto.request.CreateUserRequest;
import az.ilkin.eis.dto.request.UpdateUserRequest;
import az.ilkin.eis.dto.response.UserResponse;
import az.ilkin.eis.entity.User;
import az.ilkin.eis.exception.BadRequestException;
import az.ilkin.eis.exception.ResourceNotFoundException;
import az.ilkin.eis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //Butun istifadeciler
    public List<UserResponse>getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse createUser(CreateUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
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

    public UserResponse updateUser(Long id, UpdateUserRequest request){
        User user = findById(id);

        if(request.getName() != null){
            user.setName(request.getName());
        }
        if(request.getEmail() != null && !request.getEmail().equals(user.getEmail())){
            if(userRepository.existsByEmail(request.getEmail())){
                throw new BadRequestException("Bu email artiq movcuddur: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        if(request.getPassword() != null && !request.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if(request.getRole() != null){
            user.setRole(request.getRole());
        }
        return toResponse(userRepository.save(user));
    }
    public void deleteUser(Long id){
        User user = findById(id);
        userRepository.delete(user);
    }

    public User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Istifadeci tapilmadi: id=" + id));
    }
    public UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}


