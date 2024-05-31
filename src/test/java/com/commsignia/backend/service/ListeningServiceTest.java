package com.commsignia.backend.service;

import com.commsignia.backend.domain.DomainService;
import com.commsignia.backend.domain.entity.VehicleWithLatestPositionDTO;
import com.commsignia.backend.service.pojo.ListenerNotification;
import com.commsignia.backend.service.pojo.ListenerVehicle;
import com.commsignia.backend.service.pojo.LocationForUIDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ListeningServiceTest {

    @Mock
    private DomainService domainService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RequestBodySpec requestBodySpec;

    @Mock
    private RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    private ListeningService listeningService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
        listeningService = new ListeningService(domainService, webClientBuilder);
    }

    @Test
    void testSendNotification() {
        ListenerNotification notification = new ListenerNotification();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/notifications/ui"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(eq(notification))).thenReturn((RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Notification Sent"));

        Mono<String> result = listeningService.sendNotification(notification);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri(eq("/notifications/ui"));
        verify(requestBodySpec).bodyValue(eq(notification));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);

        assertEquals("Notification Sent", result.block());
    }

    @Test
    void testSendUpdateLocation() {
        LocationForUIDto location = new LocationForUIDto();
        location.setId("1");
        location.setLatitude(12.34);
        location.setLongitude(56.78);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/vehicles/update"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(eq(location))).thenReturn((RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Location Updated"));

        Mono<String> result = listeningService.sendUpdateLocation(location);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri(eq("/vehicles/update"));
        verify(requestBodySpec).bodyValue(eq(location));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);

        assertEquals("Location Updated", result.block());
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




