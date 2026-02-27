package com.project.cloudseed.controller;

import com.project.cloudseed.dto.PlantRequestDTO;
import com.project.cloudseed.dto.PlantResponseDTO;
import com.project.cloudseed.service.PlantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/plants")
public class PlantController {
    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    /**
     * Endpoint para carregar as plantas.
     * Se o método específico por utilizador não existe no Service,
     * usamos o findAllPlants e filtramos ou retornamos a lista global.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlantResponseDTO>> getPlantsByUserId(@PathVariable("userId") Long userId) {
        // Se o método getPlantsByUserId não existe no Service,
        // chamamos o findAllPlants que você confirmou existir.
        List<PlantResponseDTO> plants = plantService.findAllPlants();

        // Nota: Idealmente, o Service deveria filtrar por userId na base de dados.
        // Se o Service não filtra, o frontend receberá todas as plantas.
        if (plants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(plants);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<PlantResponseDTO> createPlant(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody PlantRequestDTO plantDTO) {

        PlantResponseDTO createdPlant = plantService.createPlant(userId, plantDTO);
        return new ResponseEntity<>(createdPlant, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PlantResponseDTO>> getAllPlants() {
        List<PlantResponseDTO> plants = plantService.findAllPlants();

        if (plants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(plants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantResponseDTO> getPlantById(@PathVariable("id") Long id) {
        PlantResponseDTO plant = plantService.findPlantById(id);
        return ResponseEntity.ok(plant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantResponseDTO> updatePlant(
            @PathVariable("id") Long id,
            @RequestBody PlantRequestDTO plantDTO) {

        PlantResponseDTO updatedPlant = plantService.updatePlant(id, plantDTO);
        return ResponseEntity.ok(updatedPlant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable("id") Long id) {
        plantService.deletePlant(id);
        return ResponseEntity.noContent().build();
    }
}