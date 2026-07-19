package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@NoArgsConstructor
public class TaskStatusUpdateDTO {

    private JsonNullable<@NotBlank @Size(min = 1) String> name = JsonNullable.undefined();
    private JsonNullable<@NotBlank @Size(min = 1) String> slug = JsonNullable.undefined();
}