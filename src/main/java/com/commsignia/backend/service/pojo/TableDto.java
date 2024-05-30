package com.commsignia.backend.service.pojo;

import lombok.Data;

@Data
public class TableDto {
    private String id;
    private Double longitude;
    private Double latitude;
    private String message;
}
