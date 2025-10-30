package com.project.cloudseed.service;

import com.project.cloudseed.dto.PlantRequestDTO;
import com.project.cloudseed.dto.PlantResponseDTO;
import com.project.cloudseed.model.Plant;
import com.project.cloudseed.model.Schedule;
import com.project.cloudseed.model.User;
import com.project.cloudseed.repository.PlantRepository;
import com.project.cloudseed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        //Encontrar user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));

        //DTO de Requisição para Entidade Plant
        Plant plantToSave = mapToPlant(plantDTO);

        //relacionamento entre plant e user
        plantToSave.setUser(user);

        if(plantToSave.getSchedule() != null) {
            plantToSave.getSchedule().setPlant(plantToSave);
        }

        //salvar plant
        Plant savedPlant = plantRepository.save(plantToSave);

        return mapToResponseDTO(savedPlant);
    }

    private Plant mapToPlant(PlantRequestDTO dto) {
        Plant plant = new Plant();
        plant.setName(dto.getName());
        plant.setSpecies(dto.getSpecies());
        plant.setLocation(dto.getLocation());
        plant.setCreatedAt(LocalDateTime.now());

        if (dto.getSchedule() != null) {
            Schedule schedule = new Schedule();

            PlantRequestDTO.ScheduleRequestDTO scheduleDTO = dto.getSchedule();

            schedule.setFrequency(scheduleDTO.getFrequency());
            schedule.setLastWateringDate(scheduleDTO.getLastWateringDate());
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
        }

        return dto;
    }
}