package com.project.cloudseed.service;

import com.project.cloudseed.dto.UserRequestDTO;
import com.project.cloudseed.dto.UserResponseDTO;
import com.project.cloudseed.model.User;
import com.project.cloudseed.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userDTO) {

        // 1. Mapeamento Manual: DTO de Requisição para Entidade User
        User userToSave = mapToUser(userDTO);

        // 2. Salvar o Usuário
        User savedUser = userRepository.save(userToSave);

        // 3. Mapeamento Manual: Entidade Salva para DTO de Resposta
        return mapToResponseDTO(savedUser);
    }

    private User mapToUser(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(hashedPassword);

        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
