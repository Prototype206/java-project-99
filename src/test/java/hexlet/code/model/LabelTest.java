package hexlet.code.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class LabelTest {

    @Test
    void testLabelGettersAndSetters() {
        LocalDate now = LocalDate.now();
        Label label = new Label();
        label.setId(1L);
        label.setName("bug");
        label.setCreatedAt(now);

        assertThat(label.getId()).isEqualTo(1L);
        assertThat(label.getName()).isEqualTo("bug");
        assertThat(label.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void testLabelAllArgsConstructor() {
        LocalDate now = LocalDate.now();
        Label label = new Label(1L, "feature", now);

        assertThat(label.getId()).isEqualTo(1L);
        assertThat(label.getName()).isEqualTo("feature");
        assertThat(label.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void testLabelEqualsAndHashCode() {
        Label label1 = new Label();
        label1.setId(1L);
        label1.setName("bug");

        Label label2 = new Label();
        label2.setId(1L);
        label2.setName("enhancement");

        Label label3 = new Label();
        label3.setId(2L);
        label3.setName("bug");

        assertThat(label1).isEqualTo(label2);
        assertThat(label1.hashCode()).isEqualTo(label2.hashCode());
        assertThat(label1).isNotEqualTo(label3);
    }
}