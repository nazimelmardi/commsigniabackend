package com.commsignia.backend.service;

import com.commsignia.backend.domain.DomainService;
import com.commsignia.backend.domain.entity.VehicleWithLatestPositionDTO;
import com.commsignia.backend.service.pojo.ListenerLocation;
import com.commsignia.backend.service.pojo.ListenerNotification;
import com.commsignia.backend.service.pojo.ListenerVehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class ListeningService {

    private final DomainService domainService;

    @Autowired
    public ListeningService (DomainService domainService) {
        this.domainService = domainService;
    }

    public List<ListenerVehicle> get (Double lat, Double lon, Double rad) {
        log.info("GET service level reached");
        List<ListenerVehicle> listenerVehicleList = new ArrayList<>();
        List<VehicleWithLatestPositionDTO> unfiteredDtoList = domainService.getListofVehiclesFilteredByLatestPosition();
        log.info("the domain service was successful, now the mapper comes at service level in GET");
        for (VehicleWithLatestPositionDTO dto : unfiteredDtoList) {
            if (isWithinRange(dto.getLatestLatitude(), dto.getLatestLongitude(), lat, lon, rad)) {
                ListenerVehicle vehicle = new ListenerVehicle();
                vehicle.setId(dto.getVehicleId());
                vehicle.setLatitude(dto.getLatestLatitude());
                vehicle.setLongitude(dto.getLatestLongitude());
                listenerVehicleList.add(vehicle);
            }
        }
        return listenerVehicleList;
    }

    public void newPosition (String id, ListenerLocation location) {
        log.info("new position POST at service level");
        domainService.saveNewPosition(id, location.getLatitude(),location.getLongitude());
    }

    public String newVehicle() {
        log.info("register POST at service level");
        return domainService.createNewVehicle();
    }

    public void newNotification(ListenerNotification notification) {
        log.info("new notification POST at service level");
        domainService.createNewNotification(notification);
    }

    private boolean isWithinRange(double latitude, double longitude, Double lat, Double lon, Double rad) {
        double earthRadius =  6373000.0; // Earth radius in meters
        double dLat = Math.toRadians(latitude - lat);
        double dLon = Math.toRadians(longitude - lon);
        double lat1 = Math.toRadians(latitude);
        double lat2 = Math.toRadians(latitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = earthRadius * c;
        return distance <= rad;
    }
}
