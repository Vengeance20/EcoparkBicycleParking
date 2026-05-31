package com.group13.ecopark_bicycle_parking.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiv1/manager")
public class StationMonitorController {

    private final StationMonitorService stationMonitorService;

    public StationMonitorController(StationMonitorService stationMonitorService) {
        this.stationMonitorService = stationMonitorService;
    }

    @GetMapping("/station-monitor")
    public ResponseEntity<?> getStationMonitor(@RequestParam Integer managerId) {
        try {
            return ResponseEntity.ok(stationMonitorService.getMonitorStation(managerId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
