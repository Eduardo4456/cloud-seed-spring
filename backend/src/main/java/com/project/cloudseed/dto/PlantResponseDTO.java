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
