package com.commsignia.backend.service;

import com.commsignia.backend.BackendApplication;
import com.commsignia.backend.domain.DomainService;
import com.commsignia.backend.service.pojo.ListenerLocation;
import com.commsignia.backend.service.pojo.ListenerNotification;
import com.commsignia.backend.service.pojo.ListenerVehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ControllerTest {

    @Mock
    private ListeningService listeningService;

    private Controller controller;

    @MockBean
    private DomainService domainService;

    @BeforeEach
    void setUp() {
        // Initialize the mock service and controller
        listeningService = mock(ListeningService.class);
        domainService = mock(DomainService.class);
        controller = new Controller(listeningService);
    }

    @Test
    void testGetVehiclesInRange() {
        // Define test data
        Double latitude = 123.456;
        Double longitude = 456.789;
        Double radius = 100.0;
        List<ListenerVehicle> vehicles = new ArrayList<>();
        //test vehicle can be added here

        // Mock service method call
        when(listeningService.get(latitude, longitude, radius)).thenReturn(vehicles);

        // Call the controller method
        ResponseEntity<List<ListenerVehicle>> response = controller.getVehiclesInRange(latitude, longitude, radius);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(vehicles, response.getBody());
    }

    @Test
    void testRegisterVehicle() {
        // Mock service method call
        String vehicleId = "testVehicleId"; // Define test vehicle ID
        when(listeningService.newVehicle()).thenReturn(vehicleId);

        // Call the controller method
        ResponseEntity<String> response = controller.registerVehicle();

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(vehicleId, response.getBody());
    }

    @Test
    void testUpdateVehiclePosition() {
        String vehicleId = "123";
        ListenerLocation location = new ListenerLocation();
        location.setLatitude(120.12);
        location.setLongitude(23.23);

        ResponseEntity<Void> response = controller.updateVehiclePosition(vehicleId, location);

        verify(listeningService).newPosition(vehicleId, location);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSendNotification() {
        ListenerNotification notification = new ListenerNotification();
        notification.setId("123");
        notification.setMessage("hello");

        ResponseEntity<Void> response = controller.sendNotification(notification);

        verify(listeningService).newNotification(notification);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
