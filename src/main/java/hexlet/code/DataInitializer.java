package hexlet.code;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.UserService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.LabelService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusService taskStatusService;
    private final LabelRepository labelRepository;
    private final LabelService labelService;

    public DataInitializer(UserRepository userRepository,
                           UserService userService,
                           TaskStatusRepository taskStatusRepository,
                           TaskStatusService taskStatusService,
                           LabelRepository labelRepository,
                           LabelService labelService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.taskStatusRepository = taskStatusRepository;
        this.taskStatusService = taskStatusService;
        this.labelRepository = labelRepository;
        this.labelService = labelService;
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

        List<String> defaultLabels = List.of("feature", "bug");
        for (String labelName : defaultLabels) {
            if (labelRepository.findByName(labelName).isEmpty()) {
                LabelCreateDTO labelDto = new LabelCreateDTO();
                labelDto.setName(labelName);
                labelService.create(labelDto);
            }
        }
    }
}