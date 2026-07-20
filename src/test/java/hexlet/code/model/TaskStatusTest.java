package hexlet.code.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskStatusTest {

    @Test
    void testTaskStatusGettersAndSetters() {
        LocalDate now = LocalDate.now();
        TaskStatus status = new TaskStatus();
        status.setId(1L);
        status.setName("In Progress");
        status.setSlug("in_progress");
        status.setCreatedAt(now);

        assertThat(status.getId()).isEqualTo(1L);
        assertThat(status.getName()).isEqualTo("In Progress");
        assertThat(status.getSlug()).isEqualTo("in_progress");
        assertThat(status.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void testTaskStatusAllArgsConstructor() {
        LocalDate now = LocalDate.now();
        TaskStatus status = new TaskStatus(1L, "Draft", "draft", now);

        assertThat(status.getId()).isEqualTo(1L);
        assertThat(status.getName()).isEqualTo("Draft");
        assertThat(status.getSlug()).isEqualTo("draft");
        assertThat(status.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void testTaskStatusEqualsAndHashCode() {
        TaskStatus status1 = new TaskStatus();
        status1.setId(1L);

        TaskStatus status2 = new TaskStatus();
        status2.setId(1L);

        TaskStatus status3 = new TaskStatus();
        status3.setId(2L);

        assertThat(status1).isEqualTo(status2);
        assertThat(status1.hashCode()).isEqualTo(status2.hashCode());
        assertThat(status1).isNotEqualTo(status3);
    }
}