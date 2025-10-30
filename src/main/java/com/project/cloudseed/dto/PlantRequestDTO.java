package com.project.cloudseed.dto;


import com.project.cloudseed.model.WateringFrequency;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlantRequestDTO {
    private String name;
    private String species;
    private String location;

    private ScheduleRequestDTO schedule;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ScheduleRequestDTO {
        private WateringFrequency frequency;
        private LocalDate lastWateringDate;
    }
}
