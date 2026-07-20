package hexlet.code.controller;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private TaskStatus testStatus;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();

        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
        labelRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("developer@example.com");
        testUser.setPassword("password");
        userRepository.save(testUser);

        testStatus = new TaskStatus();
        testStatus.setName("In Progress");
        testStatus.setSlug("in_progress");
        taskStatusRepository.save(testStatus);
    }

    @Test
    public void testGetTasks() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateTask() throws Exception {
        var dto = new TaskCreateDTO();
        dto.setName("Write integration tests");
        dto.setIndex(1);
        dto.setDescription("Testing the Task Controller layer");
        dto.setStatusSlug(testStatus.getSlug());
        dto.setAssigneeId(testUser.getId());

        var response = mockMvc.perform(post("/api/tasks")
                        .with(jwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).contains("content");
        assertThat(response).contains("Testing the Task Controller layer");

        assertThat(taskRepository.findAll()).hasSize(1);
        Task createdTask = taskRepository.findAll().get(0);
        assertThat(createdTask.getName()).isEqualTo("Write integration tests");
        assertThat(createdTask.getDescription()).isEqualTo("Testing the Task Controller layer");
        assertThat(createdTask.getAssignee().getId()).isEqualTo(testUser.getId());
    }

    @Test
    public void testUpdateTask() throws Exception {
        Task task = new Task();
        task.setName("Old Task Name");
        task.setTaskStatus(testStatus);
        taskRepository.save(task);

        var dto = new TaskUpdateDTO();
        dto.setName(JsonNullable.of("New Task Name"));

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .with(jwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Task updated = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Task Name");
    }

    @Test
    public void testDeleteTask() throws Exception {
        Task task = new Task();
        task.setName("To be deleted");
        task.setTaskStatus(testStatus);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/tasks/" + task.getId())
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }

    @Test
    public void testGetTasksWithFilters() throws Exception {
        var label = new hexlet.code.model.Label();
        label.setName("urgent");
        labelRepository.save(label);

        Task task1 = new Task();
        task1.setName("Fix security vulnerability");
        task1.setTaskStatus(testStatus);
        task1.setAssignee(testUser);
        task1.getLabels().add(label);
        taskRepository.save(task1);

        TaskStatus anotherStatus = taskStatusRepository.findBySlug("draft")
                .orElseGet(() -> {
                    TaskStatus newStatus = new TaskStatus();
                    newStatus.setName("Draft");
                    newStatus.setSlug("draft");
                    return taskStatusRepository.save(newStatus);
                });

        Task task2 = new Task();
        task2.setName("Write documentation");
        task2.setTaskStatus(anotherStatus);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks?titleCont=security")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("Fix security vulnerability");
                    assertThat(body).doesNotContain("Write documentation");
                });

        mockMvc.perform(get("/api/tasks?assigneeId=" + testUser.getId())
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("Fix security vulnerability");
                    assertThat(body).doesNotContain("Write documentation");
                });

        mockMvc.perform(get("/api/tasks?status=draft")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("Write documentation");
                    assertThat(body).doesNotContain("Fix security vulnerability");
                });

        mockMvc.perform(get("/api/tasks?labelId=" + label.getId())
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("Fix security vulnerability");
                    assertThat(body).doesNotContain("Write documentation");
                });
    }
}