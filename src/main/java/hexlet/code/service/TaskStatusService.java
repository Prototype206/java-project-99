package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    public List<TaskStatusDTO> getAll() {
        return taskStatusRepository.findAll().stream().map(this::toDTO).toList();
    }

    public TaskStatusDTO getById(Long id) {
        TaskStatus status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        return toDTO(status);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        TaskStatus status = new TaskStatus();
        status.setName(dto.getName());
        status.setSlug(dto.getSlug());
        taskStatusRepository.save(status);
        return toDTO(status);
    }

    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto) {
        TaskStatus status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        if (dto.getName().isPresent()) {
            status.setName(dto.getName().orElse(null));
        }

        if (dto.getSlug().isPresent()) {
            status.setSlug(dto.getSlug().orElse(null));
        }

        taskStatusRepository.save(status);
        return toDTO(status);
    }

    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }

    private TaskStatusDTO toDTO(TaskStatus status) {
        TaskStatusDTO dto = new TaskStatusDTO();
        dto.setId(status.getId());
        dto.setName(status.getName());
        dto.setSlug(status.getSlug());
        dto.setCreatedAt(status.getCreatedAt());
        return dto;
    }
}