package com.commsignia.backend.domain;

import com.commsignia.backend.domain.entity.Vehicle;
import com.commsignia.backend.domain.entity.VehicleWithLatestPositionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    @Query("SELECT new com.commsignia.backend.domain.entity.VehicleWithLatestPositionDTO(v.id, p.longitude, p.latitude) " +
            "FROM Vehicle v " +
            "JOIN v.positions p " +
            "WHERE p.created_at = (SELECT MAX(p2.created_at) FROM Position p2 WHERE p2.vehicle.id = v.id)")
    List<VehicleWithLatestPositionDTO> findVehiclesWithLatestPosition();
}
