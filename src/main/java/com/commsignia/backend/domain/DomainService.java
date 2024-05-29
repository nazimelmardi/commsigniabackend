package com.commsignia.backend.domain;

import com.commsignia.backend.domain.entity.Notification;
import com.commsignia.backend.domain.entity.Position;
import com.commsignia.backend.domain.entity.Vehicle;
import com.commsignia.backend.domain.entity.VehicleWithLatestPositionDTO;
import com.commsignia.backend.service.pojo.ListenerNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DomainService {



    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Transactional
    public void saveNewPosition (String id, Double lat, Double lon) {
        Position position = new Position();
        position.setLongitude(lon);
        position.setLatitude(lat);
        position.setCreated_at(LocalDateTime.now());

        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isPresent()) {
            vehicle.get().getPositions().add(position);
            vehicleRepository.save(vehicle.get());
        } else {
            throw new RuntimeException("Id not found");
        }
    }

    public List<VehicleWithLatestPositionDTO> getListofVehiclesFilteredByLatestPosition() {
        return vehicleRepository.findVehiclesWithLatestPosition();
    }
    @Transactional
    public String createNewVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setCreated_at(LocalDateTime.now());
        return vehicleRepository.save(vehicle).getId();
    }

    public void createNewNotification(ListenerNotification dto) {
        Notification notification = new Notification();
        notification.setCreated_at(LocalDateTime.now());
        notification.setMessage(dto.getMessage());
        Optional<Vehicle> vehicle = vehicleRepository.findById(dto.getId());
        if (vehicle.isPresent()) {
            vehicle.get().getNotifications().add(notification);
            vehicleRepository.save(vehicle.get());
        } else {
            throw new RuntimeException("couldn't find vehicle by id");
        }
    }
}
