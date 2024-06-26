package com.commsignia.backend.service;

import com.commsignia.backend.service.pojo.ListenerLocation;
import com.commsignia.backend.service.pojo.ListenerNotification;
import com.commsignia.backend.service.pojo.ListenerVehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class Controller {

    private final ListeningService listeningService;

    @Autowired
    public Controller(ListeningService listeningService) {
        this.listeningService = listeningService;
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<ListenerVehicle>> getVehiclesInRange(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radius) {
        log.info("GET /vehicles called");

        List<ListenerVehicle> dto = listeningService.get(latitude, longitude, radius);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/vehicles")
    public ResponseEntity<String> registerVehicle() {
        log.info("POST register called");
        return ResponseEntity.ok(listeningService.newVehicle());
    }

    @PostMapping("/vehicle/{id}")
    public ResponseEntity<Void> updateVehiclePosition (@PathVariable String id, @RequestBody ListenerLocation location) {
        log.info("POST update vehicle called");
        listeningService.newPosition(id, location);
        log.info("update returned to controller");
        return ResponseEntity.ok().build();

    }

    @PostMapping("/notifications/")
    public ResponseEntity<Void> sendNotification(@RequestBody ListenerNotification notification) {
        log.info("POST notifications called");
        listeningService.newNotification(notification);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vehicles/refresh")
    public Void sendFullTable() {
        listeningService.getTheLatestRecords();
        return null;
    }

}
