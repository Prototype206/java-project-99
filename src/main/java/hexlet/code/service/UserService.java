package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public UserDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDTO(user);
    }

    public UserDTO create(UserCreateDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return toDTO(user);
    }


    public UserDTO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getFirstName().isPresent()) {
            user.setFirstName(dto.getFirstName().orElse(null));
        }

        if (dto.getLastName().isPresent()) {
            user.setLastName(dto.getLastName().orElse(null));
        }

        if (dto.getEmail().isPresent()) {
            user.setEmail(dto.getEmail().orElse(null));
        }

        if (dto.getPassword().isPresent()) {
            String rawPassword = dto.getPassword().orElse(null);
            if (rawPassword != null) {
                user.setPassword(passwordEncoder.encode(rawPassword));
            }
        }

        userRepository.save(user);
        return toDTO(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}