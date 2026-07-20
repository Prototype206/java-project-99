package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openapitools.jackson.nullable.JsonNullable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskSpecification taskSpecification;

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LabelRepository labelRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskDTO taskDTO;
    private TaskStatus taskStatus;
    private User user;
    private Label label;

    @BeforeEach
    void setUp() {
        taskStatus = new TaskStatus();
        taskStatus.setId(1L);
        taskStatus.setSlug("in_progress");

        user = new User();
        user.setId(2L);
        user.setEmail("user@example.com");

        label = new Label();
        label.setId(3L);
        label.setName("bug");

        task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setTaskStatus(taskStatus);
        task.setAssignee(user);

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setName("Test Task");
    }

    @Test
    void testGetAll() {
        TaskParamsDTO params = new TaskParamsDTO();
        Specification<Task> spec = (root, query, cb) -> null;

        when(taskSpecification.build(params)).thenReturn(spec);
        when(taskRepository.findAll(spec)).thenReturn(List.of(task));
        when(taskMapper.map(task)).thenReturn(taskDTO);

        List<TaskDTO> result = taskService.getAll(params);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Task");
    }

    @Test
    void testFindByIdSuccess() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.map(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testCreateTask() {
        TaskCreateDTO createDTO = new TaskCreateDTO();
        createDTO.setName("New Task");
        createDTO.setStatusSlug("in_progress");
        createDTO.setAssigneeId(2L);
        createDTO.setTaskLabelIds(Set.of(3L));

        when(taskMapper.map(createDTO)).thenReturn(task);
        when(taskStatusRepository.findBySlug("in_progress")).thenReturn(Optional.of(taskStatus));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(labelRepository.findAllById(Set.of(3L))).thenReturn(List.of(label));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.map(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.create(createDTO);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testUpdateTaskSuccess() {
        TaskUpdateDTO updateDTO = new TaskUpdateDTO();
        updateDTO.setStatusSlug(JsonNullable.of("in_progress"));
        updateDTO.setAssigneeId(JsonNullable.of(2L));
        updateDTO.setTaskLabelIds(JsonNullable.of(Set.of(3L)));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskStatusRepository.findBySlug("in_progress")).thenReturn(Optional.of(taskStatus));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(labelRepository.findAllById(Set.of(3L))).thenReturn(List.of(label));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.map(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.update(updateDTO, 1L);

        assertThat(result).isNotNull();
        verify(taskMapper, times(1)).update(updateDTO, task);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testDeleteTaskSuccess() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTaskNotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}