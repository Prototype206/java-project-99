package hexlet.code;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    public DataInitializer(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            UserCreateDTO admin = new UserCreateDTO();
            admin.setEmail("hexlet@example.com");
            admin.setPassword("qwerty");
            admin.setFirstName("Admin");
            admin.setLastName("Adminov");
            userService.create(admin);
        }
    }
}