package com.group13.ecopark_bicycle_parking.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager") // Giữ nguyên đường dẫn cũ
public class StationMonitorController {

    private final StationMonitorService stationMonitorService;

    public StationMonitorController(StationMonitorService stationMonitorService) {
        this.stationMonitorService = stationMonitorService;
    }

    // API cũ của Manager
    @GetMapping("/station-monitor")
    public ResponseEntity<?> getStationMonitor(@RequestParam Integer managerId) {
        try {
            return ResponseEntity.ok(stationMonitorService.getMonitorStation(managerId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== API DÀNH CHO ADMIN ====================

    // 🔴 THÊM API NÀY: Lấy danh sách trạm cho Admin
    @GetMapping("/admin/stations")
    public ResponseEntity<?> getAllStationsForAdmin() {
        try {
            return ResponseEntity.ok(stationMonitorService.getAllStationsForAdmin());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔴 THÊM API NÀY: Chuyển sang bảo trì
    @PutMapping("/admin/stations/{id}/maintenance")
    public ResponseEntity<?> setMaintenance(@PathVariable Integer id) {
        try {
            stationMonitorService.setStationMaintenance(id);
            return ResponseEntity.ok("Chuyển sang bảo trì thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔴 THÊM API NÀY: Gỡ bảo trì
    @PutMapping("/admin/stations/{id}/active")
    public ResponseEntity<?> setActive(@PathVariable Integer id) {
        try {
            stationMonitorService.setStationActive(id);
            return ResponseEntity.ok("Gỡ bảo trì thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔴 THÊM API NÀY: Thêm trạm mới
    @PostMapping("/admin/stations")
    public ResponseEntity<?> addStation(@RequestBody Station station) {
        try {
            Station savedStation = stationMonitorService.addStation(station);
            return ResponseEntity.ok(savedStation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}