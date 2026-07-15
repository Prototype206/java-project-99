package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    @NotBlank
    @Size(min = 1)
    @JsonProperty("title")
    private String name;

    private Integer index;

    @JsonProperty("content")
    private String description;

    @NotBlank
    @JsonProperty("status")
    private String statusSlug;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonProperty("taskLabelIds")
    private java.util.Set<Long> taskLabelIds;
}