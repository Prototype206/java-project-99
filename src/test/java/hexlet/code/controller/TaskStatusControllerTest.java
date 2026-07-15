package hexlet.code.controller;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.openapitools.jackson.nullable.JsonNullable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetStatuses() throws Exception {
        mockMvc.perform(get("/api/task_statuses")
                        .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetStatusesUnauthorized() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateStatus() throws Exception {
        var dto = new TaskStatusCreateDTO();
        dto.setName("In Testing");
        dto.setSlug("in_testing");

        mockMvc.perform(post("/api/task_statuses")
                        .with(jwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        assertThat(taskStatusRepository.findBySlug("in_testing")).isPresent();
    }

    @Test
    public void testUpdateStatus() throws Exception {
        TaskStatus statusEntity = new TaskStatus();
        statusEntity.setName("Old Name");
        statusEntity.setSlug("old_slug");
        taskStatusRepository.save(statusEntity);

        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("New Name"));

        mockMvc.perform(put("/api/task_statuses/" + statusEntity.getId())
                        .with(jwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        var updated = taskStatusRepository.findById(statusEntity.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getSlug()).isEqualTo("old_slug");
    }

    @Test
    public void testDeleteStatus() throws Exception {
        TaskStatus statusEntity = new TaskStatus();
        statusEntity.setName("To Delete");
        statusEntity.setSlug("to_delete");
        taskStatusRepository.save(statusEntity);

        mockMvc.perform(delete("/api/task_statuses/" + statusEntity.getId())
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.findById(statusEntity.getId())).isEmpty();
    }
}