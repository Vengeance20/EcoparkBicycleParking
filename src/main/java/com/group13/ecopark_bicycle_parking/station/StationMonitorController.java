package com.group13.ecopark_bicycle_parking.station;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class StationMonitorController {

    private final StationMonitorService stationMonitorService;
    private final StationRepository stationRepository;

    public StationMonitorController(StationMonitorService stationMonitorService,
                                    StationRepository stationRepository) {
        this.stationMonitorService = stationMonitorService;
        this.stationRepository = stationRepository;
    }

    // =============================================
    // MANAGER: Xem thông tin trạm mình quản lý
    // GET /apiv1/manager/station-monitor?managerId=2
    // =============================================
    @GetMapping("/apiv1/manager/station-monitor")
    public ResponseEntity<?> getStationMonitor(@RequestParam Integer managerId) {
        try {
            return ResponseEntity.ok(stationMonitorService.getMonitorStation(managerId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =============================================
    // ADMIN: Lấy toàn bộ danh sách trạm
    // GET /apiv1/admin/stations
    // =============================================
    @GetMapping("/apiv1/admin/stations")
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationRepository.findAll());
    }

    // =============================================
    // ADMIN: Tạo trạm mới
    // POST /apiv1/admin/stations
    // =============================================
    @PostMapping("/apiv1/admin/stations")
    public ResponseEntity<?> createStation(@RequestBody Map<String, Object> body) {
        try {
            Station station = buildStation(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(stationRepository.save(station));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tạo trạm: " + e.getMessage());
        }
    }

    // =============================================
    // ADMIN: Cập nhật trạm
    // PUT /apiv1/admin/stations/{id}
    // =============================================
    @PutMapping("/apiv1/admin/stations/{id}")
    public ResponseEntity<?> updateStation(@PathVariable Integer id,
                                           @RequestBody Map<String, Object> body) {
        try {
            Station existing = stationRepository.findByStationIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạm ID: " + id));

            String name = str(body, "name");
            if (name != null) existing.setName(name);

            if (body.get("capacity") != null)
                existing.setCapacity(Integer.parseInt(body.get("capacity").toString()));
            if (body.get("latitude") != null)
                existing.setLatitude(new BigDecimal(body.get("latitude").toString()));
            if (body.get("longitude") != null)
                existing.setLongitude(new BigDecimal(body.get("longitude").toString()));

            String status = str(body, "status");
            if (status != null) existing.setStatus(status);

            return ResponseEntity.ok(stationRepository.save(existing));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật trạm: " + e.getMessage());
        }
    }

    // =============================================
    // ADMIN: Xóa mềm trạm
    // DELETE /apiv1/admin/stations/{id}
    // =============================================
    @DeleteMapping("/apiv1/admin/stations/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Integer id) {
        try {
            Station station = stationRepository.findByStationIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạm ID: " + id));
            stationRepository.delete(station); // @SQLDelete → set is_deleted = true
            return ResponseEntity.ok("Đã xóa trạm thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // ---- Helpers ----

    private Station buildStation(Map<String, Object> body) {
        String name = str(body, "name");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Thiếu tên trạm!");

        Object capObj = body.get("capacity");
        if (capObj == null) throw new IllegalArgumentException("Thiếu sức chứa!");

        BigDecimal lat = body.get("latitude") != null
                ? new BigDecimal(body.get("latitude").toString()) : BigDecimal.ZERO;
        BigDecimal lng = body.get("longitude") != null
                ? new BigDecimal(body.get("longitude").toString()) : BigDecimal.ZERO;

        String status = str(body, "status");
        if (status == null) status = "ACTIVE";

        return Station.builder()
                .name(name)
                .capacity(Integer.parseInt(capObj.toString()))
                .latitude(lat)
                .longitude(lng)
                .status(status)
                .isDeleted(false)
                .build();
    }

    private String str(Map<String, Object> body, String key) {
        Object v = body.get(key);
        return (v != null && !v.toString().isBlank()) ? v.toString().trim() : null;
    }
}
