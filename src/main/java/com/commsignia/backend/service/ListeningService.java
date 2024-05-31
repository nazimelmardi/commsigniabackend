package com.commsignia.backend.service;

import com.commsignia.backend.domain.DomainService;
import com.commsignia.backend.domain.entity.FullListDto;
import com.commsignia.backend.domain.entity.VehicleWithLatestPositionDTO;
import com.commsignia.backend.service.pojo.ListOfTableElementsDto;
import com.commsignia.backend.service.pojo.ListenerLocation;
import com.commsignia.backend.service.pojo.ListenerNotification;
import com.commsignia.backend.service.pojo.ListenerVehicle;
import com.commsignia.backend.service.pojo.LocationForUIDto;
import com.commsignia.backend.service.pojo.TableDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class ListeningService {

    private final DomainService domainService;
    private final WebClient webClient;





    @Autowired
    public ListeningService (DomainService domainService, WebClient.Builder webClientBuilder) {
        this.domainService = domainService;
        this.webClient = webClientBuilder.build();

    }

    public List<ListenerVehicle> get (Double lat, Double lon, Double rad) {
        log.info("GET service level reached");
        List<ListenerVehicle> listenerVehicleList = new ArrayList<>();
        List<VehicleWithLatestPositionDTO> unfiteredDtoList = domainService.getListofVehiclesFilteredByLatestPosition();
        log.info("the domain service was successful, now the mapper comes at service level in GET");
        for (VehicleWithLatestPositionDTO dto : unfiteredDtoList) {
            log.info("Processing DTO: {}", dto);
            if (isWithinRange(dto.getLatestLatitude(), dto.getLatestLongitude(), lat, lon, rad)) {
                ListenerVehicle vehicle = new ListenerVehicle();
                vehicle.setId(dto.getVehicleId());
                vehicle.setLatitude(dto.getLatestLatitude());
                vehicle.setLongitude(dto.getLatestLongitude());
                listenerVehicleList.add(vehicle);
            }
        }
        log.info("Returning listenerVehicleList: {}", listenerVehicleList);
        return listenerVehicleList;
    }

    public void newPosition (String id, ListenerLocation location) {
        log.info("new position POST at service level");
        domainService.saveNewPosition(id, location.getLatitude(),location.getLongitude());
        LocationForUIDto ui = new LocationForUIDto();
        ui.setId(id);
        ui.setLongitude(location.getLongitude());
        ui.setLatitude(location.getLongitude());
        sendUpdateLocation(ui);
    }

    public String newVehicle() {
        log.info("register POST at service level");
        getTheLatestRecords();
        return domainService.createNewVehicle();
    }

    public void newNotification(ListenerNotification notification) {
        log.info("new notification POST at service level");
        domainService.createNewNotification(notification);
        sendNotification(notification);
    }

    private boolean isWithinRange(double latitude, double longitude, Double lat, Double lon, Double rad) {
        double earthRadius =  6373.0; // Earth radius in km
        double dLat = Math.toRadians(latitude - lat);
        double dLon = Math.toRadians(longitude - lon);
        double lat1 = Math.toRadians(lat);
        double lat2 = Math.toRadians(latitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = earthRadius * c;
        log.info("Calculated distance: {} for vehicle at lat: {}, lon: {}", distance, latitude, longitude);

        return distance <= rad;
    }


    public Mono<String> sendNotification(ListenerNotification notification) {
        return webClient.post()
                .uri("/notifications/ui")
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> sendUpdateLocation(LocationForUIDto location) {
        return webClient.post()
                .uri("/vehicles/update")
                .bodyValue(location)
                .retrieve()
                .bodyToMono(String.class);
    }

    public void getTheLatestRecords () {
        List<FullListDto>filterDtos = domainService.getLatestList();
        ListOfTableElementsDto dto = new ListOfTableElementsDto();
        List<TableDto> dtos = new ArrayList<>();
        for (FullListDto f : filterDtos) {
            TableDto d = new TableDto();
            d.setId(f.getId());
            d.setLongitude(f.getLongitude());
            d.setLatitude(f.getLatitude());
            d.setMessage(f.getMessage());
            dtos.add(d);
        }
        dto.setTableDtos(dtos);
        sendFullTable(dto);
    }
    public Mono<Void> sendFullTable(ListOfTableElementsDto dto) {

        return (Mono<Void>) webClient.method(HttpMethod.GET).uri("/vehicles/refresh").bodyValue(dto);
    }
}
