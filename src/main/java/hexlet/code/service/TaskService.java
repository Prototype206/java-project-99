package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskSpecification taskSpecification;
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final TaskMapper taskMapper;

    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var spec = taskSpecification.build(params);
        return taskRepository.findAll(spec).stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return taskMapper.map(task);
    }

    public TaskDTO create(TaskCreateDTO dto) {
        Task task = taskMapper.map(dto);

        var status = taskStatusRepository.findBySlug(dto.getStatusSlug())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        task.setTaskStatus(status);

        if (dto.getAssigneeId() != null) {
            var assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        }

        if (dto.getTaskLabelIds() != null) {
            var labels = new HashSet<>(labelRepository.findAllById(dto.getTaskLabelIds()));
            task.setLabels(labels);
        }

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO update(Long id, TaskUpdateDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        taskMapper.update(dto, task);

        if (dto.getStatusSlug() != null && dto.getStatusSlug().isPresent()) {
            var status = taskStatusRepository.findBySlug(dto.getStatusSlug().orElse(null))
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
            task.setTaskStatus(status);
        }

        if (dto.getAssigneeId() != null && dto.getAssigneeId().isPresent()) {
            Long assigneeId = dto.getAssigneeId().orElse(null);
            if (assigneeId != null) {
                var assignee = userRepository.findById(assigneeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
                task.setAssignee(assignee);
            } else {
                task.setAssignee(null);
            }
        }

        if (dto.getTaskLabelIds() != null && dto.getTaskLabelIds().isPresent()) {
            var labelIds = dto.getTaskLabelIds().orElse(null);
            if (labelIds != null) {
                var labels = new HashSet<>(labelRepository.findAllById(labelIds));
                task.setLabels(labels);
            } else {
                task.getLabels().clear();
            }
        }

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}