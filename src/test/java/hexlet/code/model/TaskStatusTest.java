package hexlet.code.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskStatusTest {

    @Test
    void testTaskStatusFields() {
        TaskStatus status = new TaskStatus();
        status.setName("In Progress");
        status.setSlug("in_progress");

        assertThat(status.getName()).isEqualTo("In Progress");
        assertThat(status.getSlug()).isEqualTo("in_progress");
    }
}