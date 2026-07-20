package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskDTO taskDTO;
    private TaskCreateDTO createDTO;
    private TaskStatus taskStatus;

    @BeforeEach
    void setUp() {
        taskStatus = new TaskStatus();
        taskStatus.setName("Draft");
        taskStatus.setSlug("draft");

        task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setTaskStatus(taskStatus);

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setName("Test Task");

        createDTO = new TaskCreateDTO();
        createDTO.setName("Test Task");
        createDTO.setStatusSlug("draft");
    }

    @Test
    void testFindByIdSuccess() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.map(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testCreateTask() {
        when(taskStatusRepository.findBySlug("draft")).thenReturn(Optional.of(taskStatus));
        when(taskMapper.map(createDTO)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.map(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.create(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testDeleteTaskSuccess() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTaskNotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, never()).deleteById(anyLong());
    }
}