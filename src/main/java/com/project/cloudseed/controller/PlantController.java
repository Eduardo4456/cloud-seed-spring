package com.project.cloudseed.controller;

import com.project.cloudseed.dto.PlantRequestDTO;
import com.project.cloudseed.dto.PlantResponseDTO;
import com.project.cloudseed.service.PlantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plants")
public class PlantController {
    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<PlantResponseDTO> createPlant(
            @PathVariable Long userId, // pega o ID do URL
            @RequestBody PlantRequestDTO plantDTO) {

        PlantResponseDTO createdPlant = plantService.createPlant(userId, plantDTO);
        return new ResponseEntity<>(createdPlant, HttpStatus.CREATED); // Retorna status 201
    }

    @GetMapping
    public ResponseEntity<List<PlantResponseDTO>> getAllPlants() {
        List<PlantResponseDTO> plants = plantService.findAllPlants();

        if (plants.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content se não houver plantas
        }
        return ResponseEntity.ok(plants); // 200 OK com a lista
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantResponseDTO> getPlantById(@PathVariable Long id) {
        // O serviço retorna o DTO de resposta ou lança uma exceção (tratada pelo Spring)
        PlantResponseDTO plant = plantService.findPlantById(id);

        return ResponseEntity.ok(plant); // Retorna 200 OK com a planta
    }
}
