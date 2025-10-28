package com.project.cloudseed.repository;

import com.project.cloudseed.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    List<Plant> findByUsers_Id(Long userId);

    Optional<Plant> findByIdAndUsers_Id(Long plantId, Long userId);
}