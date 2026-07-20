package hexlet.code.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskTest {

    @Test
    void testTaskFieldsAndRelations() {
        LocalDate now = LocalDate.now();
        User user = new User();
        user.setId(10L);
        user.setEmail("assignee@example.com");

        TaskStatus status = new TaskStatus();
        status.setId(5L);
        status.setSlug("in_progress");

        Label label = new Label();
        label.setId(100L);
        label.setName("bug");

        Set<Label> labels = new HashSet<>();
        labels.add(label);

        Task task = new Task();
        task.setId(1L);
        task.setName("Fix Critical Bug");
        task.setDescription("Detailed description of the bug");
        task.setIndex(1);
        task.setTaskStatus(status);
        task.setAssignee(user);
        task.setLabels(labels);
        task.setCreatedAt(now);

        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getName()).isEqualTo("Fix Critical Bug");
        assertThat(task.getDescription()).isEqualTo("Detailed description of the bug");
        assertThat(task.getIndex()).isEqualTo(1);
        assertThat(task.getTaskStatus().getSlug()).isEqualTo("in_progress");
        assertThat(task.getAssignee().getId()).isEqualTo(10L);
        assertThat(task.getLabels()).hasSize(1);
        assertThat(task.getLabels()).contains(label);
        assertThat(task.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void testTaskAllArgsConstructor() {
        LocalDate now = LocalDate.now();
        User user = new User();
        TaskStatus status = new TaskStatus();
        Set<Label> labels = new HashSet<>();

        Task task = new Task(1L, "Title", "Desc", status, 2, user, labels, now);

        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getName()).isEqualTo("Title");
        assertThat(task.getDescription()).isEqualTo("Desc");
        assertThat(task.getIndex()).isEqualTo(2);
    }

    @Test
    void testTaskEqualsAndHashCode() {
        Task task1 = new Task();
        task1.setId(1L);

        Task task2 = new Task();
        task2.setId(1L);

        Task task3 = new Task();
        task3.setId(2L);

        assertThat(task1).isEqualTo(task2);
        assertThat(task1.hashCode()).isEqualTo(task2.hashCode());
        assertThat(task1).isNotEqualTo(task3);
    }
}