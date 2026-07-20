package hexlet.code.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LabelTest {

    @Test
    void testLabelFields() {
        Label label = new Label();
        label.setName("bug");

        assertThat(label.getName()).isEqualTo("bug");
    }
}