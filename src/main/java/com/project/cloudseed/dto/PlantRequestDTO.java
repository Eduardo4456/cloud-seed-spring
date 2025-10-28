package com.project.cloudseed.dto;

import com.project.cloudseed.model.WateringFrequency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantRequestDTO {
    private String name;
    private String species;
    private String location;

    private ScheduleRequestDTO schedule;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleRequestDTO {
        private WateringFrequency frequency;
        private LocalDate lastWateringDate;
    }
}
