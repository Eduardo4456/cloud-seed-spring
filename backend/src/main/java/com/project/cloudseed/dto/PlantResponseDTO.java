package com.project.cloudseed.dto;

import com.project.cloudseed.model.WateringFrequency;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlantResponseDTO {
    private Long id;
    private String name;
    private String species;
    private String location;
    private LocalDateTime createdAt;

    // Campo adicionado para que o PlantService consiga usar o setUserId()
    // e o Frontend consiga saber a quem pertence a planta.
    private Long userId;

    private ScheduleResponseDTO schedule;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ScheduleResponseDTO {
        private Long id;
        private WateringFrequency frequency;
        private LocalDate lastWateringDate;
    }
}