package hexlet.code.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import tools.jackson.databind.ObjectMapper;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;
import org.openapitools.jackson.nullable.JsonNullable;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "test_user")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateUser() throws Exception {
        var dto = new UserCreateDTO();
        dto.setEmail("email@example.com");
        dto.setPassword("password");

        var request = post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateUserInvalid() throws Exception {
        var dto = new UserCreateDTO();
        dto.setEmail("not-an-email");
        dto.setPassword("12");

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPartialUpdateUser() throws Exception {
        User user = new User();
        user.setEmail("initial@example.com");
        user.setPassword("rawPassword");
        user.setFirstName("Alex");
        userRepository.save(user);

        UserUpdateDTO updateDto = new UserUpdateDTO();
        updateDto.setFirstName(JsonNullable.of("Alexander"));

        mockMvc.perform(put("/api/users/" + user.getId())
                        .with(jwt().jwt(builder -> builder.subject(user.getEmail())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getFirstName()).isEqualTo("Alexander");
        assertThat(updatedUser.getEmail()).isEqualTo("initial@example.com");
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User();
        user.setEmail("temporary@example.com");
        user.setPassword("tempPass");
        userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .with(jwt().jwt(builder -> builder.subject(user.getEmail())))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}