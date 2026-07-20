package hexlet.code.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    void testUserGettersAndSetters() {
        LocalDate now = LocalDate.now();
        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("secret_hash");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPassword()).isEqualTo("secret_hash");
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testUserAllArgsConstructor() {
        LocalDate now = LocalDate.now();
        User user = new User(1L, "jane@example.com", "Jane", "Doe", "pass", now, now);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Doe");
    }

    @Test
    void testUserEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(1L);

        User user3 = new User();
        user3.setId(2L);

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        assertThat(user1).isNotEqualTo(user3);
    }
}