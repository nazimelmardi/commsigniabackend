package com.commsignia.backend.service.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class ListOfListenerVehicle {
    List<ListenerVehicle> listenerVehicleList = new ArrayList<>();
}
