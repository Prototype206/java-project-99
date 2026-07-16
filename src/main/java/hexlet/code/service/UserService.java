package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import java.util.List;

public interface UserService {
    UserDTO create(UserCreateDTO dto);
    UserDTO update(Long id, UserUpdateDTO dto);
    UserDTO getById(Long id);
    List<UserDTO> getAll();
    void delete(Long id);
}