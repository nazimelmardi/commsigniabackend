package com.commsignia.backend.service.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ListenerVehicle {

    @JsonProperty("id")
    private String id;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("latitude")
    private Double latitude;

}
