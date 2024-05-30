package com.commsignia.backend.domain.entity;

import lombok.Data;

@Data
public class FullListDto {
    private String id;

    private Double longitude;

    private Double latitude;

    private String message;

    public FullListDto(String id, Double longitude, Double latitude, String message) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.message = message;
    }
}
