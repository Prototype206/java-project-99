package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    @Override
    public List<TaskDTO> getAll(TaskParamsDTO params) {
        Specification<Task> spec = taskSpecification.build(params);
        List<Task> tasks = taskRepository.findAll(spec);
        return tasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    @Override
    public TaskDTO findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return taskMapper.map(task);
    }

    @Override
    @Transactional
    public TaskDTO create(TaskCreateDTO data) {
        Task task = taskMapper.map(data);
        if (data.getStatusSlug() != null) {
            var taskStatus = taskStatusRepository.findBySlug(data.getStatusSlug())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found: " + data.getStatusSlug()));
            task.setTaskStatus(taskStatus);
        }
        if (data.getAssigneeId() != null) {
            var assignee = userRepository.findById(data.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + data.getAssigneeId()));
            task.setAssignee(assignee);
        }
        if (data.getTaskLabelIds() != null && !data.getTaskLabelIds().isEmpty()) {
            var labels = new java.util.HashSet<>(labelRepository.findAllById(data.getTaskLabelIds()));
            task.setLabels(labels);
        }
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @Override
    @Transactional
    public TaskDTO update(TaskUpdateDTO data, Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        taskMapper.update(data, task);

        if (data.getStatusSlug() != null && data.getStatusSlug().isPresent()) {
            String slug = data.getStatusSlug().get();
            var taskStatus = taskStatusRepository.findBySlug(slug)
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found: " + slug));
            task.setTaskStatus(taskStatus);
        }

        if (data.getAssigneeId() != null && data.getAssigneeId().isPresent()) {
            Long assigneeId = data.getAssigneeId().get();
            if (assigneeId == null) {
                task.setAssignee(null);
            } else {
                var assignee = userRepository.findById(assigneeId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + assigneeId));
                task.setAssignee(assignee);
            }
        }

        if (data.getTaskLabelIds() != null && data.getTaskLabelIds().isPresent()) {
            java.util.Set<Long> labelIds = data.getTaskLabelIds().get();
            if (labelIds == null || labelIds.isEmpty()) {
                task.getLabels().clear();
            } else {
                var labels = new java.util.HashSet<>(labelRepository.findAllById(labelIds));
                task.setLabels(labels);
            }
        }

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task with id " + id + " not found");
        }
        taskRepository.deleteById(id);
    }
}