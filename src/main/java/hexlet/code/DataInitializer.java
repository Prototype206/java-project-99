package hexlet.code;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.UserService;
import hexlet.code.service.TaskStatusService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusService taskStatusService;

    public DataInitializer(UserRepository userRepository,
                           UserService userService,
                           TaskStatusRepository taskStatusRepository,
                           TaskStatusService taskStatusService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.taskStatusRepository = taskStatusRepository;
        this.taskStatusService = taskStatusService;
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

        Map<String, String> defaultStatuses = Map.of(
            "Draft", "draft",
            "To Review", "to_review",
            "To Be Fixed", "to_be_fixed",
            "To Publish", "to_publish",
            "Published", "published"
        );

        for (Map.Entry<String, String> entry : defaultStatuses.entrySet()) {
            if (taskStatusRepository.findBySlug(entry.getValue()).isEmpty()) {
                TaskStatusCreateDTO statusDto = new TaskStatusCreateDTO();
                statusDto.setName(entry.getKey());
                statusDto.setSlug(entry.getValue());
                taskStatusService.create(statusDto);
            }
        }
    }
}