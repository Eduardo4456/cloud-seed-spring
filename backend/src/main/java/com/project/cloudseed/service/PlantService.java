package com.project.cloudseed.service;

import com.project.cloudseed.dto.PlantRequestDTO;
import com.project.cloudseed.dto.PlantResponseDTO;
import com.project.cloudseed.model.Plant;
import com.project.cloudseed.model.Schedule;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));

        Plant plantToSave = mapToPlant(plantDTO);
        plantToSave.setUser(user);

        if(plantToSave.getSchedule() != null) {
            plantToSave.getSchedule().setPlant(plantToSave);
        }

        Plant savedPlant = plantRepository.save(plantToSave);
        return mapToResponseDTO(savedPlant);
    }

    // ⭐ AQUI ESTÁ A FUNÇÃO NOVA QUE FALTAVA ⭐
    @Transactional(readOnly = true)
    public List<PlantResponseDTO> getPlantsByUserId(Long userId) {
        // Vai buscar à base de dados APENAS as plantas deste utilizador
        List<Plant> userPlants = plantRepository.findByUserId(userId);

        // Converte a lista de entidades para DTOs
        return userPlants.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlantResponseDTO> findAllPlants() {
        List<Plant> plants = plantRepository.findAll();

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
            schedule.setFrequency(scheduleDTO.getFrequency());
            schedule.setLastWateringDate(scheduleDTO.getLastWateringDate());
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

        // ⭐ CORREÇÃO CRUCIAL AQUI: Enviar o userId para o Frontend saber quem é o dono! ⭐
        if (plant.getUser() != null) {
            dto.setUserId(plant.getUser().getId());
        }

        if (plant.getSchedule() != null) {
            PlantResponseDTO.ScheduleResponseDTO scheduleDTO = new PlantResponseDTO.ScheduleResponseDTO();
            scheduleDTO.setId(plant.getSchedule().getId());
            scheduleDTO.setFrequency(plant.getSchedule().getFrequency());
            scheduleDTO.setLastWateringDate(plant.getSchedule().getLastWateringDate());
            dto.setSchedule(scheduleDTO);
        }

        return dto;
    }

    @Transactional
    public PlantResponseDTO updatePlant(Long plantId, PlantRequestDTO dto) {
        Plant existingPlant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Planta não encontrada, id:" + plantId));

        existingPlant.setName(dto.getName());
        existingPlant.setSpecies(dto.getSpecies());
        existingPlant.setLocation(dto.getLocation());

        if (dto.getSchedule() != null) {
            Schedule existingSchedule = existingPlant.getSchedule() != null ?
                    existingPlant.getSchedule() : new Schedule();

            PlantRequestDTO.ScheduleRequestDTO scheduleDTO = dto.getSchedule();

            existingSchedule.setFrequency(scheduleDTO.getFrequency());
            existingSchedule.setLastWateringDate(scheduleDTO.getLastWateringDate());

            if (existingPlant.getSchedule() == null) {
                existingSchedule.setPlant(existingPlant);
                existingPlant.setSchedule(existingSchedule);
            }
        }

        Plant updatedPlant = plantRepository.save(existingPlant);
        return mapToResponseDTO(updatedPlant);
    }

    @Transactional
    public void deletePlant(Long plantId) {
        if (!plantRepository.existsById(plantId)) {
            throw new RuntimeException("Planta não encontrada com ID: " + plantId);
        }
        plantRepository.deleteById(plantId);
    }
}