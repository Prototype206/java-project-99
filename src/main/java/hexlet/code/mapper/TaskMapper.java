package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
    )
public abstract class TaskMapper {

    @Autowired
    protected TaskStatusRepository taskStatusRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected LabelRepository labelRepository;


    @Mapping(target = "taskStatus", source = "statusSlug", qualifiedByName = "slugToStatus")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "idToUser")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "labelIdsToLabels")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "taskStatus.slug", target = "statusSlug")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "labelsToLabelIds")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "taskStatus", source = "statusSlug", qualifiedByName = "slugToStatus")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "idToUser")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "labelIdsToLabels")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    @Named("slugToStatus")
    public TaskStatus slugToStatus(String slug) {
        return slug == null ? null : taskStatusRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("TaskStatus not found: " + slug));
    }

    @Named("idToUser")
    public User idToUser(Long id) {
        return id == null ? null : userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Named("labelIdsToLabels")
    public Set<Label> labelIdsToLabels(Set<Long> labelIds) {
        if (labelIds == null) {
            return Collections.emptySet();
        }
        return labelIds.stream()
            .map(id -> labelRepository.findById(id).orElseThrow())
            .collect(Collectors.toSet());
    }

    @Named("labelsToLabelIds")
    public Set<Long> labelsToLabelIds(Set<Label> labels) {
        if (labels == null) {
            return Collections.emptySet();
        }
        return labels.stream()
            .map(Label::getId)
            .collect(Collectors.toSet());
    }
}