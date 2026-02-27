package com.project.cloudseed.dto;
import com.project.cloudseed.model.WateringFrequency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlantRequestDTO {
    @NotBlank(message = "O nome da planta é obrigatório")
    private String name;

    private String species;

    private String location;

    //lembrar de não permitir vazio
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