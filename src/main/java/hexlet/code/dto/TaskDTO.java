package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private Long id;

    @JsonProperty("title")
    private String name;

    @JsonProperty("content")
    private String description;

    @JsonProperty("status")
    private String statusSlug;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonProperty("taskLabelIds")
    private Set<Long> taskLabelIds;

    private Integer index;
    private LocalDate createdAt;
}