package com.project.cloudseed.dto;

import com.project.cloudseed.model.WateringFrequency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantResponseDTO {
    private Long id;
    private String name;
    private String species;
    private String location;
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleResponseDTO {
        private Long id;
        private WateringFrequency frequency;
        private LocalDate lastWateringDate;
    }
}
