package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.model.Label;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.specification.TaskSpecification;

@Service
public class TaskService {

    @Autowired
    private TaskSpecification taskSpecification;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var spec = taskSpecification.build(params);
        return taskRepository.findAll(spec).stream()
                .map(this::toDTO)
                .toList();
    }

    public TaskDTO getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return toDTO(task);
    }

    public TaskDTO create(TaskCreateDTO dto) {
        Task task = new Task();
        task.setName(dto.getName());
        task.setIndex(dto.getIndex());
        task.setDescription(dto.getDescription());

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
        return toDTO(task);
    }

    public TaskDTO update(Long id, TaskUpdateDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (dto.getName().isPresent()) {
            task.setName(dto.getName().orElse(null));
        }
        if (dto.getIndex().isPresent()) {
            task.setIndex(dto.getIndex().orElse(null));
        }
        if (dto.getDescription().isPresent()) {
            task.setDescription(dto.getDescription().orElse(null));
        }
        if (dto.getStatusSlug().isPresent()) {
            var status = taskStatusRepository.findBySlug(dto.getStatusSlug().orElse(null))
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
            task.setTaskStatus(status);
        }
        if (dto.getAssigneeId().isPresent()) {
            Long assigneeId = dto.getAssigneeId().orElse(null);
            if (assigneeId != null) {
                var assignee = userRepository.findById(assigneeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
                task.setAssignee(assignee);
            } else {
                task.setAssignee(null);
            }
        }
        if (dto.getTaskLabelIds().isPresent()) {
            var labelIds = dto.getTaskLabelIds().orElse(null);
            if (labelIds != null) {
                var labels = new HashSet<>(labelRepository.findAllById(labelIds));
                task.setLabels(labels);
            } else {
                task.getLabels().clear();
            }
        }

        taskRepository.save(task);
        return toDTO(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    private TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setIndex(task.getIndex());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setStatusSlug(task.getTaskStatus().getSlug());
        dto.setAssigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null);
        dto.setCreatedAt(task.getCreatedAt());

        if (task.getLabels() != null) {
            var labelIds = task.getLabels().stream()
                    .map(Label::getId)
                    .collect(Collectors.toSet());
            dto.setTaskLabelIds(labelIds);
        } else {
            dto.setTaskLabelIds(new HashSet<>());
        }

        return dto;
    }
}