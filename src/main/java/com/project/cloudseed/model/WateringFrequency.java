package com.project.cloudseed.model;

public enum WateringFrequency {
    DAILY("Diária"),
    BI_DAILY("A cada 2 dias"),
    WEEKLY("Semanal"),
    BI_WEEKLY("Quinzenal"),
    MONTHLY("Mensal"),
    CUSTOM("Personalizada");

    private final String description;

    WateringFrequency(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
