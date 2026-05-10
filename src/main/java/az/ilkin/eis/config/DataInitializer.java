package az.ilkin.eis.config;

import az.ilkin.eis.entity.User;
import az.ilkin.eis.enums.Role;
import az.ilkin.eis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args)  {
        if(userRepository.existsByEmail("admin@eis.az")){
            return;
        }
        User admin = User.builder()
                .name("Super Admin")
                .email("admin@eis.az")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Default ADMIN yaradildi - email: admin@eis.az | sifre: Admin@123");

    }
}
