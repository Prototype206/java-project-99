package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    @Override
    public List<TaskStatusDTO> getAll() {
        return taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
    }

    @Override
    public TaskStatusDTO getById(Long id) {
        TaskStatus status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        return taskStatusMapper.map(status);
    }

    @Override
    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        TaskStatus status = taskStatusMapper.map(dto);
        taskStatusRepository.save(status);
        return taskStatusMapper.map(status);
    }

    @Override
    @Transactional
    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto) {
        TaskStatus status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        taskStatusMapper.update(dto, status);
        taskStatusRepository.save(status);
        return taskStatusMapper.map(status);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }
}