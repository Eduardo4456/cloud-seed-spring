package com.project.cloudseed.service;

import com.project.cloudseed.dto.PlantRequestDTO;
import com.project.cloudseed.dto.PlantResponseDTO;
import com.project.cloudseed.model.Plant;
import com.project.cloudseed.model.Schedule; // Entidade Schedule
import com.project.cloudseed.model.User;
import com.project.cloudseed.repository.PlantRepository;
import com.project.cloudseed.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;

    public PlantService(PlantRepository plantRepository, UserRepository userRepository) {
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PlantResponseDTO createPlant(Long userId, PlantRequestDTO plantDTO) {
        // 1. Encontrar user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));

        // 2. DTO de Requisição para Entidade Plant
        Plant plantToSave = mapToPlant(plantDTO);

        // 3. Relacionamento entre plant e user
        plantToSave.setUser(user);

        // 4. Relacionamento bidirecional com Schedule (se existir)
        if(plantToSave.getSchedule() != null) {
            // Este é um passo crucial para o CascadeType.ALL funcionar no OneToOne
            plantToSave.getSchedule().setPlant(plantToSave);
        }

        // 5. Salvar plant
        Plant savedPlant = plantRepository.save(plantToSave);

        return mapToResponseDTO(savedPlant);
    }

    @Transactional(readOnly = true)
    public List<PlantResponseDTO> findAllPlants() {
        List<Plant> plants = plantRepository.findAll();

        // Converte cada entidade para PlantResponseDTO
        return plants.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlantResponseDTO findPlantById(Long plantId) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Planta não encontrada com ID: " + plantId));

        return mapToResponseDTO(plant);
    }

    private Plant mapToPlant(PlantRequestDTO dto) {
        Plant plant = new Plant();
        plant.setName(dto.getName());
        plant.setSpecies(dto.getSpecies());
        plant.setLocation(dto.getLocation());
        plant.setCreatedAt(LocalDateTime.now());

        if (dto.getSchedule() != null) {
            PlantRequestDTO.ScheduleRequestDTO scheduleDTO = dto.getSchedule();

            Schedule schedule = new Schedule();

            // Mapeamento dos campos do Schedule
            schedule.setFrequency(scheduleDTO.getFrequency());
            schedule.setLastWateringDate(scheduleDTO.getLastWateringDate());
            // ⚠️ FALTA: Se você tiver intervalDays ou outros campos no Schedule, mapeie aqui.

            plant.setSchedule(schedule);
        }

        return plant;
    }

    private PlantResponseDTO mapToResponseDTO(Plant plant) {
        PlantResponseDTO dto = new PlantResponseDTO();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setSpecies(plant.getSpecies());
        dto.setLocation(plant.getLocation());
        dto.setCreatedAt(plant.getCreatedAt());

        if (plant.getSchedule() != null) {
            PlantResponseDTO.ScheduleResponseDTO scheduleDTO = new PlantResponseDTO.ScheduleResponseDTO();

            scheduleDTO.setId(plant.getSchedule().getId());
            scheduleDTO.setFrequency(plant.getSchedule().getFrequency());
            scheduleDTO.setLastWateringDate(plant.getSchedule().getLastWateringDate());

            dto.setSchedule(scheduleDTO);
        }

        return dto;
    }
}