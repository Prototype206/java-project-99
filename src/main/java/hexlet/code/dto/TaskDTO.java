package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private String title; // мапится из name
    private String content; // мапится из description
    private String status; // slug статуса
    private Long assigneeId;

    @JsonProperty("taskLabelIds")
    private Set<Long> taskLabelIds; // Коллекция ID, а не объектов!

    private Integer index;
    private LocalDate createdAt;
}