package hexlet.code.controller;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.openapitools.jackson.nullable.JsonNullable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void setUp() {
        labelRepository.deleteAll();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetLabels() throws Exception {
        mockMvc.perform(get("/api/labels")
                        .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateLabel() throws Exception {
        var dto = new LabelCreateDTO();
        dto.setName("hotfix");

        mockMvc.perform(post("/api/labels")
                        .with(jwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        assertThat(labelRepository.findByName("hotfix")).isPresent();
    }

    @Test
    public void testUpdateLabel() throws Exception {
        Label label = new Label();
        label.setName("refactoring");
        labelRepository.save(label);

        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("code-style"));

        mockMvc.perform(put("/api/labels/" + label.getId())
                        .with(jwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Label updated = labelRepository.findById(label.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("code-style");
    }

    @Test
    public void testDeleteLabel() throws Exception {
        Label label = new Label();
        label.setName("disposable");
        labelRepository.save(label);

        mockMvc.perform(delete("/api/labels/" + label.getId())
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.findById(label.getId())).isEmpty();
    }
}