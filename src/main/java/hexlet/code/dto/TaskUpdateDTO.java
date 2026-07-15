package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {
    @JsonProperty("title")
    private JsonNullable<@Size(min = 1) String> name = JsonNullable.undefined();

    private JsonNullable<Integer> index = JsonNullable.undefined();

    @JsonProperty("content")
    private JsonNullable<String> description = JsonNullable.undefined();

    @JsonProperty("status")
    private JsonNullable<String> statusSlug = JsonNullable.undefined();

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId = JsonNullable.undefined();
}