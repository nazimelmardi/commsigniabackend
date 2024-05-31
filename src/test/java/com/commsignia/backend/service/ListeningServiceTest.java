package com.commsignia.backend.service;

import com.commsignia.backend.domain.DomainService;
import com.commsignia.backend.domain.entity.VehicleWithLatestPositionDTO;
import com.commsignia.backend.service.pojo.ListenerVehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListeningServiceTest {

    @Mock
    private DomainService domainService;


    private ListeningService listeningService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listeningService = new ListeningService(domainService);
    }

    @Test
    void testGet() {
        // Given
        double lat = 34.05; // Example latitude
        double lon = -118.25; // Example longitude
        double rad = 10.0; // 10 km radius

        VehicleWithLatestPositionDTO vehicle1 = new VehicleWithLatestPositionDTO("1", 34.052235, -118.243683);
        VehicleWithLatestPositionDTO vehicle2 = new VehicleWithLatestPositionDTO("2", 34.062235, -118.243683);
        VehicleWithLatestPositionDTO vehicle3 = new VehicleWithLatestPositionDTO("3", -118.25, 34.055); // out of range

        List<VehicleWithLatestPositionDTO> vehicleList = Arrays.asList(vehicle1, vehicle2, vehicle3);

        when(domainService.getListofVehiclesFilteredByLatestPosition()).thenReturn(vehicleList);

        // When
        List<ListenerVehicle> result = listeningService.get(lat, lon, rad);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Only vehicle1 and vehicle2 should be within range

        verify(domainService).getListofVehiclesFilteredByLatestPosition();
    }
}




