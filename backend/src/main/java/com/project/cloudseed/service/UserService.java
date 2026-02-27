package com.project.cloudseed.service;

import com.project.cloudseed.dto.LoginRequestDTO;
import com.project.cloudseed.dto.UserRequestDTO;
import com.project.cloudseed.dto.UserResponseDTO;
import com.project.cloudseed.model.User;
import com.project.cloudseed.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO authenticate(LoginRequestDTO loginDTO) {
        // 1. Tenta encontrar o usuário pelo email no repository
        // Nota: Certifique-se que o seu UserRepository tem o método: Optional<User> findByEmail(String email);
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado com o email: " + loginDTO.getEmail()));

        // 2. Compara a senha em texto puro do DTO com a senha criptografada do banco
        if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            // 3. Se coincidir, converte para ResponseDTO usando o seu método existente
            return UserResponseDTO.fromUser(user);
        }

        // 4. Se a senha não coincidir, lançamos uma exceção ou retornamos null
        throw new RuntimeException("Senha incorreta.");
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userDTO) {

        // 1. Mapeamento Manual: DTO de Requisição para Entidade User
        User userToSave = mapToUser(userDTO);

        // 2. Salvar o Usuário
        User savedUser = userRepository.save(userToSave);

        User freshUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("Falha ao recuperar o usuário recém-criado para obter o createdAt."));

        // 3. Retorna o objeto recarregado
        return UserResponseDTO.fromUser(freshUser);
    }

    public List<UserResponseDTO> findAllUsers() {
        // 1. Usa o método padrão findAll() do JpaRepository
        List<User> users = userRepository.findAll();

        // 2. Converte a lista de entidades User para a lista de DTOs UserResponseDTO
        return users.stream()
                .map(UserResponseDTO::fromUser) // ⚠️ Requer o método estático 'fromUser' no seu DTO
                .collect(Collectors.toList());
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
        //dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    @Transactional
    public UserResponseDTO updateUser(Long userId, UserRequestDTO dto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));

        existingUser.setUsername(dto.getUsername());
        existingUser.setEmail(dto.getEmail());

        //implementar endpoint para alteração de senhas

        User updatedUser = userRepository.save(existingUser);

        return UserResponseDTO.fromUser(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {


        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + userId);
        }

        userRepository.deleteById(userId);
    }
}