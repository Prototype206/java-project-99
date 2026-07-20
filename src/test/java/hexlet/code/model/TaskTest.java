package hexlet.code.model;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskTest {

    @Test
    void testTaskRelations() {
        User user = new User();
        user.setEmail("assignee@example.com");

        TaskStatus status = new TaskStatus();
        status.setSlug("in_progress");

        Label label = new Label();
        label.setName("feature");

        Task task = new Task();
        task.setName("Implement Feature");
        task.setDescription("Task Description");
        task.setAssignee(user);
        task.setTaskStatus(status);
        task.setLabels(new HashSet<>());
        task.getLabels().add(label);

        assertThat(task.getName()).isEqualTo("Implement Feature");
        assertThat(task.getDescription()).isEqualTo("Task Description");
        assertThat(task.getAssignee().getEmail()).isEqualTo("assignee@example.com");
        assertThat(task.getTaskStatus().getSlug()).isEqualTo("in_progress");
        assertThat(task.getLabels()).hasSize(1);
        assertThat(task.getLabels()).contains(label);
    }
}