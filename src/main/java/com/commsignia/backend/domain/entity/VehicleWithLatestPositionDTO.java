package com.commsignia.backend.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleWithLatestPositionDTO {

    private String vehicleId;
    private Double latestLongitude;
    private Double latestLatitude;

    public VehicleWithLatestPositionDTO(String vehicleId, Double latestLongitude, Double latestLatitude) {
        this.vehicleId = vehicleId;
        this.latestLongitude = latestLongitude;
        this.latestLatitude = latestLatitude;
    }


}
