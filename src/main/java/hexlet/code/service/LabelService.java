package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    public List<LabelDTO> getAll() {
        return labelRepository.findAll().stream().map(this::toDTO).toList();
    }

    public LabelDTO getById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        return toDTO(label);
    }

    public LabelDTO create(LabelCreateDTO dto) {
        Label label = new Label();
        label.setName(dto.getName());
        labelRepository.save(label);
        return toDTO(label);
    }

    public LabelDTO update(Long id, LabelUpdateDTO dto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        if (dto.getName().isPresent()) {
            label.setName(dto.getName().orElse(null));
        }

        labelRepository.save(label);
        return toDTO(label);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }

    private LabelDTO toDTO(Label label) {
        LabelDTO dto = new LabelDTO();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setCreatedAt(label.getCreatedAt());
        return dto;
    }
}